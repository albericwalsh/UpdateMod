def get_blockstate_json(name, modid):
    """
    Génère le JSON pour un blockstate normal (sans propriétés particulières).

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans blockstates/{name}.json
    """
    return {
        "variants": {
            "normal": {"model": f"{modid}:{name}"}
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
        "parent": "block/cube_all",
        "textures": {
            "all": f"{modid}:blocks/{name}"
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
