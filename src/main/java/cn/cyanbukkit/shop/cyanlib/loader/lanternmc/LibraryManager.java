// Decompiled with: CFR 0.152
// Class Version: 8
package cn.cyanbukkit.shop.cyanlib.loader.lanternmc;


import cn.cyanbukkit.shop.cyanlib.loader.lanternmc.Logger.LogLevel;
import cn.cyanbukkit.shop.cyanlib.loader.lanternmc.Logger.Logger;
import cn.cyanbukkit.shop.cyanlib.loader.lanternmc.Logger.adapters.LogAdapter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class LibraryManager {
    protected final Logger logger;
    protected final Path saveDirectory;
    private final Set<String> repositories = new LinkedHashSet<String>();
    private RelocationHelper relocator;
    private final Map<String, IsolatedClassLoader> isolatedLibraries = new HashMap<String, IsolatedClassLoader>();

    @Deprecated
    protected LibraryManager(LogAdapter logAdapter, Path dataDirectory) {
        this.logger = new Logger(Objects.requireNonNull(logAdapter, "logAdapter"));
        this.saveDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory").toAbsolutePath().resolve("lib");
    }

    protected LibraryManager(LogAdapter logAdapter, Path dataDirectory, String directoryName) {
        this.logger = new Logger(Objects.requireNonNull(logAdapter, "logAdapter"));
        this.saveDirectory = Objects.requireNonNull(dataDirectory, "dataDirectory").toAbsolutePath().resolve(Objects.requireNonNull(directoryName, "directoryName"));
    }

    protected abstract void addToClasspath(Path var1);

    protected void addToIsolatedClasspath(Library library, Path file) {
        String id = library.getId();
        IsolatedClassLoader classLoader = id != null ? this.isolatedLibraries.computeIfAbsent(id, s -> new IsolatedClassLoader(new URL[0])) : new IsolatedClassLoader(new URL[0]);
        classLoader.addPath(file);
    }

    public IsolatedClassLoader getIsolatedClassLoaderOf(String libraryId) {
        return this.isolatedLibraries.get(libraryId);
    }

    public LogLevel getLogLevel() {
        return this.logger.getLevel();
    }

    public void setLogLevel(LogLevel level) {
        this.logger.setLevel(level);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Collection<String> getRepositories() {
        LinkedList<String> urls;
        Set<String> set = this.repositories;
        synchronized (set) {
            urls = new LinkedList<String>(this.repositories);
        }
        return Collections.unmodifiableList(urls);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addRepository(String url) {
        String repo = Objects.requireNonNull(url, "url").endsWith("/") ? url : url + '/';
        Set<String> set = this.repositories;
        synchronized (set) {
            this.repositories.add(repo);
        }
    }

    public void addMavenLocal() {
        this.addRepository(Paths.get(System.getProperty("user.home"), new String[0]).resolve(".m2/repository").toUri().toString());
    }

    public void addMavenCentral() {
        this.addRepository("https://repo1.maven.org/maven2/");
    }

    public void addSonatype() {
        this.addRepository("https://oss.sonatype.org/content/groups/public/");
    }

    public void addJCenter() {
        this.addRepository("https://jcenter.bintray.com/");
    }

    public void addJitPack() {
        this.addRepository("https://jitpack.io/");
    }

    public Collection<String> resolveLibrary(Library library) {
        LinkedHashSet<String> urls = new LinkedHashSet<String>(Objects.requireNonNull(library, "library").getUrls());
        for (String repository : library.getRepositories()) {
            urls.add(repository + library.getPath());
        }
        for (String repository : this.getRepositories()) {
            urls.add(repository + library.getPath());
        }
        return Collections.unmodifiableSet(urls);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private byte[] downloadLibrary(String url) {
        try {
            URLConnection connection = new URL(Objects.requireNonNull(url, "url")).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "libby/1.1.5");
            InputStream in = connection.getInputStream();
            try {
                byte[] buf = new byte[8192];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    int len;
                    while ((len = in.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                }
                catch (SocketTimeoutException e) {
                    this.logger.warn("Download timed out: " + connection.getURL());
                    byte[] byArray2 = null;
                    if (in == null) return byArray2;
                    in.close();
                    return byArray2;
                }
                this.logger.info("Downloaded library " + connection.getURL());
                byte[] byArray = out.toByteArray();
                return byArray;
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (Throwable throwable2) {
                        Throwable throwable = null;
                        throwable.addSuppressed(throwable2);
                    }
                }
            }
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                this.logger.debug("File not found: " + url);
                return null;
            }
            if (e instanceof SocketTimeoutException) {
                this.logger.debug("Connect timed out: " + url);
                return null;
            }
            if (e instanceof UnknownHostException) {
                this.logger.debug("Unknown host: " + url);
                return null;
            }
            this.logger.debug("Unexpected IOException", e);
            return null;
        }
    }

    public Path downloadLibrary(Library library) {
        Path file = this.saveDirectory.resolve(Objects.requireNonNull(library, "library").getPath());
        if (Files.exists(file, new LinkOption[0])) {
            return file;
        }
        Collection<String> urls = this.resolveLibrary(library);
        if (urls.isEmpty()) {
            throw new RuntimeException("Library '" + library + "' couldn't be resolved, add a repository");
        }
        MessageDigest md = null;
        if (library.hasChecksum()) {
            try {
                md = MessageDigest.getInstance("SHA-256");
            }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        Path out = file.resolveSibling(file.getFileName() + ".tmp");
        out.toFile().deleteOnExit();
        try {
            Files.createDirectories(file.getParent(), new FileAttribute[0]);
            for (String url : urls) {
                byte[] checksum;
                byte[] bytes = this.downloadLibrary(url);
                if (bytes == null) continue;
                if (md != null && !Arrays.equals(checksum = md.digest(bytes), library.getChecksum())) {
                    this.logger.warn("*** INVALID CHECKSUM ***");
                    this.logger.warn(" Library :  " + library);
                    this.logger.warn(" URL :  " + url);
                    this.logger.warn(" Expected :  " + Base64.getEncoder().encodeToString(library.getChecksum()));
                    this.logger.warn(" Actual :  " + Base64.getEncoder().encodeToString(checksum));
                    continue;
                }
                Files.write(out, bytes, new OpenOption[0]);
                Files.move(out, file, new CopyOption[0]);
                Path path = file;
                return path;
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        finally {
            try {
                Files.deleteIfExists(out);
            }
            catch (IOException iOException) {}
        }
        throw new RuntimeException("Failed to download library '" + library + "'");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Path relocate(Path in, String out, Collection<Relocation> relocations) {
        Objects.requireNonNull(in, "in");
        Objects.requireNonNull(out, "out");
        Objects.requireNonNull(relocations, "relocations");
        Path file = this.saveDirectory.resolve(out);
        if (Files.exists(file, new LinkOption[0])) {
            return file;
        }
        Path tmpOut = file.resolveSibling(file.getFileName() + ".tmp");
        tmpOut.toFile().deleteOnExit();
        Object object = this;
        synchronized (object) {
            if (this.relocator == null) {
                this.relocator = new RelocationHelper(this);
            }
        }
        try {
            this.relocator.relocate(in, tmpOut, relocations);
            Files.move(tmpOut, file, new CopyOption[0]);
            this.logger.info("Relocations applied to " + this.saveDirectory.getParent().relativize(in));
            object = file;
            return (Path) object;
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        finally {
            try {
                Files.deleteIfExists(tmpOut);
            }
            catch (IOException iOException) {}
        }
    }

    public void loadLibrary(Library library) {
        Path file = this.downloadLibrary(Objects.requireNonNull(library, "library"));
        if (library.hasRelocations()) {
            file = this.relocate(file, library.getRelocatedPath(), library.getRelocations());
        }
        if (library.isIsolatedLoad()) {
            this.addToIsolatedClasspath(library, file);
        } else {
            this.addToClasspath(file);
        }
    }
}
