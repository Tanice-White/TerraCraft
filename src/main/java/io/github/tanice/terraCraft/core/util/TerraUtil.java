package io.github.tanice.terraCraft.core.util;

public class TerraUtil {

    public static double[] extendArray(double[] original, int newLength) {
        if (original == null) return new double[newLength];
        if (original.length >= newLength) return original;
        double[] extended = new double[newLength];
        System.arraycopy(original, 0, extended, 0, original.length);
        return extended;
    }
}
