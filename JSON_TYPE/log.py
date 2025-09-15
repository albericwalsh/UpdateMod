def get_blockstate_json(name, modid):
    """
    Génère le JSON blockstate pour une bûche (log).

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans blockstates/{name}.json
    """
    return {
        "variants": {
            "axis=y": {"model": f"{modid}:{name}"},
            "axis=x": {"model": f"{modid}:{name}", "x": 90, "y": 90},
            "axis=z": {"model": f"{modid}:{name}", "x": 90}
        }
    }


def get_block_model_json(name, modid):
    """
    Génère le JSON du modèle block pour une bûche.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans models/block/{name}.json
    """
    return {
        "parent": "block/cube_column",
        "textures": {
            "end": f"{modid}:blocks/{name}_top",
            "side": f"{modid}:blocks/{name}"
        }
    }


def get_item_model_json(name, modid):
    """
    Génère le JSON du modèle item pour la bûche.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans models/item/{name}.json
    """
    return {
        "parent": f"{modid}:block/{name}"
    }
