package fr.broawz.updatemod.blocks.tileentity;

import fr.broawz.updatemod.blocks.AbstractTileEntitySign;

public class TileEntityBasicSign extends AbstractTileEntitySign {

    public TileEntityBasicSign() {
        super();
        // Initialiser les 4 lignes avec du texte vide (blanc par défaut)
        this.lines = new String[]{"", "", "", ""};
        setLineColor(new int[]{0xFFFFFF,0xFFFFFF,0xFFFFFF,0xFFFFFF});
    }
}
