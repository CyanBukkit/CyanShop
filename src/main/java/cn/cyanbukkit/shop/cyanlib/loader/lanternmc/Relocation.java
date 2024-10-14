// Decompiled with: CFR 0.152
// Class Version: 8
package cn.cyanbukkit.shop.cyanlib.loader.lanternmc;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

public class Relocation {
    private final String pattern;
    private final String relocatedPattern;
    private final Collection<String> includes;
    private final Collection<String> excludes;

    public Relocation(String pattern, String relocatedPattern, Collection<String> includes, Collection<String> excludes) {
        this.pattern = Objects.requireNonNull(pattern, "pattern").replace("{}", ".");
        this.relocatedPattern = Objects.requireNonNull(relocatedPattern, "relocatedPattern").replace("{}", ".");
        this.includes = includes != null ? Collections.unmodifiableList(new LinkedList<String>(includes)) : Collections.emptyList();
        this.excludes = excludes != null ? Collections.unmodifiableList(new LinkedList<String>(excludes)) : Collections.emptyList();
    }

    public Relocation(String pattern, String relocatedPattern) {
        this(pattern, relocatedPattern, null, null);
    }

    public String getPattern() {
        return this.pattern;
    }

    public String getRelocatedPattern() {
        return this.relocatedPattern;
    }

    public Collection<String> getIncludes() {
        return this.includes;
    }

    public Collection<String> getExcludes() {
        return this.excludes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String pattern;
        private String relocatedPattern;
        private final Collection<String> includes = new LinkedList<String>();
        private final Collection<String> excludes = new LinkedList<String>();

        public Builder pattern(String pattern) {
            this.pattern = Objects.requireNonNull(pattern, "pattern");
            return this;
        }

        public Builder relocatedPattern(String relocatedPattern) {
            this.relocatedPattern = Objects.requireNonNull(relocatedPattern, "relocatedPattern");
            return this;
        }

        public Builder include(String include) {
            this.includes.add(Objects.requireNonNull(include, "include"));
            return this;
        }

        public Builder exclude(String exclude) {
            this.excludes.add(Objects.requireNonNull(exclude, "exclude"));
            return this;
        }

        public Relocation build() {
            return new Relocation(this.pattern, this.relocatedPattern, this.includes, this.excludes);
        }
    }
}
