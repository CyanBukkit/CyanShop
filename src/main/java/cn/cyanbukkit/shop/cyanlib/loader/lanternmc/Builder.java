// Decompiled with: CFR 0.152
// Class Version: 8
package cn.cyanbukkit.shop.cyanlib.loader.lanternmc;

import java.util.Base64;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;

public class Builder {
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
        return new Library(this.urls,
                this.repositories,
                this.id,
                this.groupId,
                this.artifactId,
                this.version,
                this.classifier,
                this.checksum,
                this.relocations,
                this.isolatedLoad);
    }
}
