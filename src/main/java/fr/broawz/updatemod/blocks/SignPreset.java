package fr.broawz.updatemod.blocks;

public class SignPreset {
    private final int[] lineColors;
    private final int[] lineHighlightColors;

    public SignPreset(int[] lineColors, int[] lineHighlightColors) {
        this.lineColors = lineColors;
        this.lineHighlightColors = lineHighlightColors;
    }

    public int[] getLineColors() {
        return lineColors;
    }

    public int[] getLineHighlightColors() {
        return lineHighlightColors;
    }

    // Factory pour un preset simple (juste une couleur de texte, pas de highlight)
    public static SignPreset newPreset(int color) {
        return new SignPreset(
                new int[]{color, color, color, color},
                new int[]{0, 0, 0, 0}
        );
    }

    // Factory pour un preset avec des couleurs de texte et de surlignage personnalisées
    public static SignPreset newPreset(int[] textColors, int[] highlightColors) {
        return new SignPreset(textColors, highlightColors);
    }


}
