// Decompiled with: CFR 0.152
// Class Version: 8
package cn.cyanbukkit.shop.cyanlib.loader.lanternmc;

import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;

public class Library {
    private final Collection<String> urls;
    private final Collection<String> repositories;
    private final String id;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String classifier;
    private final byte[] checksum;
    private final Collection<Relocation> relocations;
    private final String path;
    private final String relocatedPath;
    private final boolean isolatedLoad;

    private Library(Collection<String> urls,
                    String id, String groupId, String artifactId, String version, String classifier, byte[] checksum, Collection<Relocation> relocations, boolean isolatedLoad) {
        this(urls, null, id, groupId, artifactId, version, classifier, checksum, relocations, isolatedLoad);
    }

    Library(Collection<String> urls, Collection<String> repositories, String id, String groupId, String artifactId, String version, String classifier, byte[] checksum, Collection<Relocation> relocations, boolean isolatedLoad) {
        this.urls = urls != null ? Collections.unmodifiableList(new LinkedList<String>(urls)) : Collections.emptyList();
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.groupId = Objects.requireNonNull(groupId, "groupId").replace("{}", ".");
        this.artifactId = Objects.requireNonNull(artifactId, "artifactId");
        this.version = Objects.requireNonNull(version, "version");
        this.classifier = classifier;
        this.checksum = checksum;
        this.relocations = relocations != null ? Collections.unmodifiableList(new LinkedList<Relocation>(relocations)) : Collections.emptyList();
        String path = this.groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/' + artifactId + '-' + version;
        if (this.hasClassifier()) {
            path = path + '-' + classifier;
        }
        this.path = path + ".jar";
        this.repositories = repositories != null ? Collections.unmodifiableList(new LinkedList<String>(repositories)) : Collections.emptyList();
        this.relocatedPath = this.hasRelocations() ? path + "-relocated.jar" : null;
        this.isolatedLoad = isolatedLoad;
    }

    public Collection<String> getUrls() {
        return this.urls;
    }

    public Collection<String> getRepositories() {
        return this.repositories;
    }

    public String getId() {
        return this.id;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public String getVersion() {
        return this.version;
    }

    public String getClassifier() {
        return this.classifier;
    }

    public boolean hasClassifier() {
        return this.classifier != null;
    }

    public byte[] getChecksum() {
        return this.checksum;
    }

    public boolean hasChecksum() {
        return this.checksum != null;
    }

    public Collection<Relocation> getRelocations() {
        return this.relocations;
    }

    public boolean hasRelocations() {
        return !this.relocations.isEmpty();
    }

    public String getPath() {
        return this.path;
    }

    public String getRelocatedPath() {
        return this.relocatedPath;
    }

    public boolean isIsolatedLoad() {
        return this.isolatedLoad;
    }

    public String toString() {
        String name = this.groupId + ':' + this.artifactId + ':' + this.version;
        if (this.hasClassifier()) {
            name = name + ':' + this.classifier;
        }
        return name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Collection<String> urls = new LinkedList<String>();
        private final Collection<String> repositories = new LinkedList<String>();
        private String id;
        private String groupId;
        private String artifactId;
        private String version;
        private String classifier;
        private byte[] checksum;
        private boolean isolatedLoad;
        private final Collection<Relocation> relocations = new LinkedList<Relocation>();

        public Builder url(String url) {
            this.urls.add(Objects.requireNonNull(url, "url"));
            return this;
        }

        public Builder repository(String url) {
            this.repositories.add(Objects.requireNonNull(url, "repository").endsWith("/") ? url : url + '/');
            return this;
        }

        public Builder id(String id) {
            this.id = id != null ? id : UUID.randomUUID().toString();
            return this;
        }

        public Builder groupId(String groupId) {
            this.groupId = Objects.requireNonNull(groupId, "groupId");
            return this;
        }

        public Builder artifactId(String artifactId) {
            this.artifactId = Objects.requireNonNull(artifactId, "artifactId");
            return this;
        }

        public Builder version(String version) {
            this.version = Objects.requireNonNull(version, "version");
            return this;
        }

        public Builder classifier(String classifier) {
            this.classifier = Objects.requireNonNull(classifier, "classifier");
            return this;
        }

        public Builder checksum(byte[] checksum) {
            this.checksum = Objects.requireNonNull(checksum, "checksum");
            return this;
        }

        public Builder checksum(String checksum) {
            return this.checksum(Base64.getDecoder().decode(Objects.requireNonNull(checksum, "checksum")));
        }

        public Builder isolatedLoad(boolean isolatedLoad) {
            this.isolatedLoad = isolatedLoad;
            return this;
        }

        public Builder relocate(Relocation relocation) {
            this.relocations.add(Objects.requireNonNull(relocation, "relocation"));
            return this;
        }

        public Builder relocate(String pattern, String relocatedPattern) {
            return this.relocate(new Relocation(pattern, relocatedPattern));
        }

        public Library build() {
            return new Library(this.urls, this.repositories, this.id, this.groupId, this.artifactId, this.version, this.classifier, this.checksum, this.relocations, this.isolatedLoad);
        }
    }
}
