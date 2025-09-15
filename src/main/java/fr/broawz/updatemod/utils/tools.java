package fr.broawz.updatemod.utils;

public class tools {
    public static int color(int hexRGB) {
        return 0xFF000000 | (hexRGB & 0xFFFFFF);
    }

}
