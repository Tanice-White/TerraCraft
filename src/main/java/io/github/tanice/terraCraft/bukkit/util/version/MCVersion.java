package io.github.tanice.terraCraft.bukkit.util.version;

import java.util.Comparator;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class MCVersion implements Comparable<MCVersion> {
    public static final Comparator<MCVersion> COMPARATOR = Comparator.nullsFirst(Comparator.comparingInt(MCVersion::getMajor).thenComparingInt(MCVersion::getMinor).thenComparingInt(MCVersion::getBuild));
    private final int major;
    private final int minor;
    private final int build;

    public static MCVersion getRuntimeVersion() {
        return MinecraftVersions.RUNTIME_VERSION;
    }

    public static MCVersion of(int major, int minor, int build) {
        return new MCVersion(major, minor, build);
    }

    public static MCVersion parse(String version) throws IllegalArgumentException {
        int[] versionComponents;
        String[] parts = version.split("-");
        try {
            versionComponents = MCVersion.parseVersion(parts[0]);
        } catch (IllegalStateException ex) {
            return MCVersion.of(1, 0, 0);
        }
        int major = versionComponents[0];
        int minor = versionComponents[1];
        int build = versionComponents[2];
        return MCVersion.of(major, minor, build);
    }

    private static int[] parseVersion(String version) {
        String[] elements = version.split("\\.");
        int[] numbers = new int[3];
        if (elements.length < 1) {
            throw new IllegalStateException("Invalid MC version: " + version);
        }
        for (int i = 0; i < Math.min(numbers.length, elements.length); ++i) {
            numbers[i] = Integer.parseInt(elements[i].trim());
        }
        return numbers;
    }

    private MCVersion(int major, int minor, int build) {
        this.major = major;
        this.minor = minor;
        this.build = build;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getBuild() {
        return this.build;
    }

    @Nonnull
    public String getVersion() {
        return String.format("%s.%s.%s", this.getMajor(), this.getMinor(), this.getBuild());
    }

    @Override
    public int compareTo(MCVersion another) {
        return COMPARATOR.compare(this, another);
    }

    public boolean isAfter(MCVersion other) {
        return this.compareTo(other) > 0;
    }

    public boolean isAfterOrEq(MCVersion other) {
        return this.compareTo(other) >= 0;
    }

    public boolean isBefore(MCVersion other) {
        return this.compareTo(other) < 0;
    }

    public boolean isBeforeOrEq(MCVersion other) {
        return this.compareTo(other) <= 0;
    }

    public boolean isBetween(MCVersion o1, MCVersion o2) {
        return this.isAfterOrEq(o1) && this.isBeforeOrEq(o2) || this.isBeforeOrEq(o1) && this.isAfterOrEq(o2);
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof MCVersion other)) return false;
        return this.getMajor() == other.getMajor() && this.getMinor() == other.getMinor() && this.getBuild() == other.getBuild();
    }

    public int hashCode() {
        return Objects.hash(this.getMajor(), this.getMinor(), this.getBuild());
    }

    public String toString() {
        return String.format("(MC: %s)", this.getVersion());
    }
}

