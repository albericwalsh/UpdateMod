def get_blockstate_json(name, modid):
    """
    Génère le JSON blockstate pour un escalier avec toutes les combinaisons de facing, half et shape.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans blockstates/{name}.json
    """
    shapes = [
        "straight", "inner_left", "inner_right", "outer_left", "outer_right"
    ]
    facings = {
        "north": 0,
        "south": 180,
        "west": 270,
        "east": 90
    }

    variants = {}
    for half in ["top", "bottom"]:
        for facing, y_rot in facings.items():
            for shape in shapes:
                variant = f"facing={facing},half={half},shape={shape}"
                model = f"{modid}:{name}"
                x_rot = 180 if half == "top" else 0
                variants[variant] = {
                    "model": model,
                    "x": x_rot,
                    "y": y_rot
                }

    return {
        "variants": variants
    }


def get_block_model_json(name, modid):
    """
    Génère le JSON du modèle block (parent stairs).

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans models/block/{name}.json
    """
    return {
        "parent": "block/stairs",
        "textures": {
            "bottom": f"{modid}:blocks/{name}",
            "top": f"{modid}:blocks/{name}",
            "side": f"{modid}:blocks/{name}"
        }
    }


def get_item_model_json(name, modid):
    """
    Génère le JSON du modèle item pour l'escalier.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans models/item/{name}.json
    """
    return {
        "parent": f"{modid}:block/{name}"
    }
