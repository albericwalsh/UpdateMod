def get_blockstate_json(name, modid):
    """
    Génère le JSON pour un blockstate avec propriétés de facing (orientation).

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans blockstates/{name}.json
    """
    return {
        "variants": {
            "normal": {"model": f"{modid}:{name}"},
            "facing=south": {"model": f"{modid}:{name}", "y": 180},
            "facing=west": {"model": f"{modid}:{name}", "y": 270},
            "facing=east": {"model": f"{modid}:{name}", "y": 90},
            "facing=up": {"model": f"{modid}:{name}", "x": 270},
            "facing=down": {"model": f"{modid}:{name}", "x": 90}
        }
    }


def get_block_model_json(name, modid):
    """
    Génère le JSON du modèle block.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans models/block/{name}.json
    """
    return {
        "parent": "block/orientable",
        "textures": {
            "top": f"{modid}:blocks/{name}",
            "front": f"{modid}:blocks/{name}",
            "side": f"{modid}:blocks/{name}"
        }
    }


def get_item_model_json(name, modid):
    """
    Génère le JSON du modèle item.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans models/item/{name}.json
    """
    return {
        "parent": f"{modid}:block/{name}"
    }
