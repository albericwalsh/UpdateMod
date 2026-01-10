# Documentation du mod Update Mod (1.10.2)
## 1. Présentation générale

**Nom du mod :** `Update Mod`

**Version :** `1.0.0`

**Version Minecraft :** `1.10.2`

**Objectif principal :** Ajouter des panneaux personnalisables avec texte, icônes, couleurs, surlignages et alignement, entièrement interactifs via GUI.

## 2. Structure du projet

**Le projet est organisé en plusieurs packages principaux :**

| Package        | Contenu                                                                      | Rôle principal                                                                                       |
| -------------- | ---------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| `blocks`       | `AbstractBlockSign`, `AbstractTileEntitySign`, `SignPreset`, `ModBlocksInit` | Définition des blocs de panneau et TileEntities associés. Gère rendu, variants, alignment et styles. |
| `client`       | `render`, `font`                                                             | Gestion graphique côté client : rendu des panneaux, icônes et polices personnalisées.                |
| `creativetabs` | `UpdatedCreativeTabs`                                                        | Création d’un onglet créatif pour les blocs du mod.                                                  |
| `gui`          | `GuiCustomSign`, `GuiDebugStick`                                             | Interfaces utilisateurs pour éditer les panneaux et tester les états de bloc (debug).                |
| `utils`        | `GuiHandler`, `References`, `tools`                                          | Classes utilitaires pour GUI, constantes du mod et fonctions graphiques.                             |


## 3. Détails des principaux fichiers
###   3.1 Blocks / TileEntities

* `AbstractBlockSign`
  * Base pour tous les blocs panneaux.
  * Définit : variantes (`SignVariant`), collision & bounding box, BlockState et interaction lors du placement.
  * Interagit avec `AbstractTileEntitySign` pour appliquer le texte, la couleur et l’orientation.

* `AbstractTileEntitySign`

  * TileEntity pour les panneaux.
  * Gère :
    * Texte (`lines`)
    * Alignement (`Align`)
    * Orientation (`EnumFacing`)
    * Variants (`variant`)
    * Couleur du texte et surlignage (`lineColors`, `lineHighlightColor`)
    * Font (`lineFonts`, `lineFontSizes`)
  * Fonctionnalités NBT pour sauvegarde et synchronisation client/serveur.
  * Méthodes `setLine`, `setAlign`, `setVariant`, `setFacing` avec synchronisation.

* `SignPreset`

  * Stocke les presets de couleurs et surlignages par variante.
  * Permet de créer rapidement un preset simple ou complet (`newPreset`).

### 3.2 Client / Rendering

* `TileEntityCustomSignRenderer`
  * Renderer spécifique pour les panneaux.
  * Dessine :
    * Surlignage (`highlight`)
    * Texte ligne par ligne
    * Icônes intégrées (`SignIcons`)
  * Gère alignement, scaling et orientation selon TileEntity.
  * Divisé en passe 1 (surlignage) et passe 2 (texte et icônes).
* `SignIcons`
  * Map de tokens texte → icônes (`[v]`, `[PL]`, etc.).
  * Méthodes utiles :
    * `isIcon(token)`, `getIcon(token)`, `parseLine(line)`
    * Permet de découper une ligne en texte + icônes pour le rendu.
  * Stocke aussi si une icône doit prendre la couleur du texte ou non (`ColorBend`).
* `CustomFontRenderer`
  * Fournit un `FontRenderer` personnalisé pour chaque police et taille.
  * Permet de dessiner du texte avec des polices spécifiques.

### 3.3 GUI

* `GuiCustomSign`
  * Interface pour éditer un panneau.
  * Fonctionnalités :
    * Modifier texte ligne par ligne.
    * Choisir alignement (Left, Center, Right).
    * Définir la taille du texte (`textSpan`).
    * Liste scrollable d’icônes à insérer.
    * Synchronisation avec le serveur via `PacketUpdateSign`.
  * Classe interne `IconList` gère la liste d’icônes avec scroll.
* `GuiDebugStick`
  * GUI pour tester et modifier l’état d’un bloc.
  * Génère dynamiquement un bouton pour chaque propriété (`IProperty`) du bloc.
  * Boutons cycliques pour changer les valeurs.
  * Permet validation (`Valider`) ou annulation (`Annuler`).

### 3.4 Utils

* `GuiHandler`
  * Gestion des GUIs côté client et serveur.
  * Serveur : pas de conteneur pour les panneaux.
  * Client : retourne `GuiCustomSign` pour les panneaux.
* `References`
  * Constantes globales du mod :
    * `MODID`, `NAME`, `VERSION`
    * Proxies côté client/serveur
    * Onglet créatif (`UPDATED_MOD`)
* `tools`
  * Méthodes utilitaires :
    * `FIXED_SPACE_WIDTH` pour rendu texte
    * `color(hexRGB)` → ARGB complet
    * `fixSpaces(String)` → token pour doubles espaces
    * `drawColoredQuadRaw` → dessine un rectangle coloré pour le rendu direct OpenGL

## 4. Interactions clés

1. Bloc placé
  * `AbstractBlockSign.onBlockPlacedBy()` configure variant, facing et preset.
  * TileEntity (`AbstractTileEntitySign`) stocke état et texte.
2. Rendu
  * `TileEntityCustomSignRenderer` récupère le TileEntity et rend le texte + icônes + surlignage.
  * `SignIcons` découpe les lignes en tokens pour le rendu.
  * `CustomFontRenderer` dessine le texte avec la bonne police et taille.
3. GUI
  * `GuiHandler` ouvre `GuiCustomSign` côté client.
  * L’utilisateur modifie texte, alignement, textSpan et ajoute des icônes.
  * `PacketUpdateSign` envoie les changements au serveur.
  * TileEntity est mis à jour, ce qui déclenche un rendu mis à jour.

## 5. Notes importantes pour développement futur

* La synchronisation TileEntity → client est cruciale pour que le texte/alignement/couleurs soient cohérents.
* Les presets (`SignPreset`) permettent d’étendre facilement les variantes de panneaux.
* `tools.drawColoredQuadRaw` est utile pour tout rendu personnalisé sans passer par Minecraft GUI.
* `IconList` et `SignIcons` peuvent être étendus pour ajouter plus de symboles.
* Le GUI et le renderer sont découplés : le renderer ne dépend pas du GUI.

### 6. Résumé visuel du workflow
```scss
   Joueur place le bloc
   │
   ▼
   AbstractBlockSign.onBlockPlacedBy()
   │
   ▼
   TileEntitySign (texte, couleurs, preset, variant)
   │
   ├──► TileEntityCustomSignRenderer → rend le texte, surlignages, icônes
   │
   └──► GuiHandler → GuiCustomSign (édition)
               │
               ▼
               PacketUpdateSign → serveur
               │
               ▼
               TileEntitySign mis à jour
```
## 7. Versions / Compatibilité

* Mod développé pour **Minecraft 1.10.2**
* Compatible avec forge standard 1.10.2
* Les icônes et polices personnalisées sont gérées côté client uniquement.