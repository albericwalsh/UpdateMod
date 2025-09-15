# 📦 Documentation des Types de Blocs Minecraft 1.10.2

Ce document détaille comment générer les fichiers `blockstates` et `models` pour chaque type de bloc supporté par le script Python.

## 🧩 Structure Générale

### 📁 Dossiers utilisés :
- `blockstates/` : Définitions des états de bloc.
- `models/block/` : Modèles 3D des blocs.
- `models/item/` : Modèles des items liés.
- `lang/en_US.lang` : Traductions du nom des blocs.

## ✅ Cas par Cas

### 1. `normal`
Blocs simples avec la même texture sur toutes les faces.

- **blockstates** :
```json
{
  "variants": {
    "": { "model": "modid:block_name" }
  }
}
```

- **models/block** :
```json
{
  "parent": "block/cube_all",
  "textures": {
    "all": "modid:blocks/block_name"
  }
}
```

### 2. `facing`
Bloc orienté vers une direction (north, south, etc.).

- **blockstates** :
```json
{
  "variants": {
    "facing=north": {"model": "modid:block_name"},
    "facing=south": {"model": "modid:block_name", "y": 180},
    "facing=west":  {"model": "modid:block_name", "y": 270},
    "facing=east":  {"model": "modid:block_name", "y": 90},
    "facing=up":    {"model": "modid:block_name", "x": 270},
    "facing=down":  {"model": "modid:block_name", "x": 90}
  }
}
```

- **models/block** :
```json
{
  "parent": "block/orientable",
  "textures": {
    "top": "modid:blocks/block_name_top",
    "front": "modid:blocks/block_name_front",
    "side": "modid:blocks/block_name_side"
  }
}
```

### 3. `log`
Bloc avec textures différentes pour le bout et le côté.

- **blockstates** :
```json
{
  "variants": {
    "axis=y":    {"model": "modid:block_name"},
    "axis=x":    {"model": "modid:block_name", "x": 90, "y": 90},
    "axis=z":    {"model": "modid:block_name", "x": 90},
    "axis=none": {"model": "modid:block_name"}
  }
}
```

- **models/block** :
```json
{
  "parent": "block/cube_column",
  "textures": {
    "end": "modid:blocks/block_name_top",
    "side": "modid:blocks/block_name"
  }
}
```

### 4. `stairs`
Bloc avec forme d’escalier, nécessite un traitement via Forge.

- **blockstates** : très complexe (facing, half, shape), souvent copié d'un modèle vanilla.
- **models/block** : 3 fichiers (`stairs`, `stairs_inner`, `stairs_outer`) héritant de `block/stairs`.

### 5. `slab`
Blocs demi-hauteur.

- **blockstates** :
```json
{
  "variants": {
    "half=bottom": { "model": "modid:block_name" },
    "half=top":    { "model": "modid:block_name_top" }
  }
}
```

- **models/block** :
`block_name.json`
```json
{
  "parent": "block/slab",
  "textures": { "bottom": "modid:blocks/block_name" }
}
```
`block_name_top.json`
```json
{
  "parent": "block/slab_top",
  "textures": { "bottom": "modid:blocks/block_name" }
}
```

### 6. `wall`
Blocs type "mur", se connectant entre eux.

- **blockstates** :
Utiliser les templates vanilla avec connexions.

- **models/block** :
`wall_inventory`, `wall_post`, `wall_side` etc.

### 7. `door` et `trapdoor`
Spécial, nécessite `door_bottom`, `door_top`, `trapdoor_bottom`, etc. selon l'état (open, half).

### 8. `fence`
Utiliser `fence_post`, `fence_side`, `fence_inventory`.

### 9. `pane` (e.g. glass, iron_bar)
Utiliser :
- `template_glass_pane_post`
- `template_glass_pane_side`
- `template_glass_pane_noside`
et leurs variantes `_alt`.

---

## 📌 Remarques
- Toujours utiliser les bons `parents` selon Minecraft 1.10.2.
- Respecter les noms et extensions `.json`.
- Le nom des fichiers doit toujours correspondre à celui du bloc.