def get_blockstate_json(name, modid):
    """
    Génère le JSON blockstate pour un bloc 'facing' avec plusieurs textures.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON blockstate.
    """
    return {
        "variants": {
            "facing=down": {"model": f"{modid}:{name}", "x": 180},
            "facing=up": {"model": f"{modid}:{name}"},
            "facing=north": {"model": f"{modid}:{name}"},
            "facing=south": {"model": f"{modid}:{name}", "y": 180},
            "facing=west": {"model": f"{modid}:{name}", "y": 270},
            "facing=east": {"model": f"{modid}:{name}", "y": 90}
        }
    }


def get_block_model_json(name, modid):
    """
    Génère le JSON modèle block avec plusieurs textures.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON modèle block.
    """
    return {
        "parent": "block/orientable",
        "textures": {
            "front": f"{modid}:blocks/{name}_front",
            "side": f"{modid}:blocks/{name}_side",
            "back": f"{modid}:blocks/{name}_back",
            "bottom": f"{modid}:blocks/{name}_top",
            "top": f"{modid}:blocks/{name}_top"
        }
    }


def get_item_model_json(name, modid):
    """
    Génère le JSON modèle item qui référence le modèle block.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON modèle item.
    """
    return {
        "parent": f"{modid}:block/{name}"
    }
