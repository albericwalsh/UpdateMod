def get_blockstate_json(name, modid):
    """
    Génère le JSON blockstate pour une dalle (slab).

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans blockstates/{name}.json
    """
    return {
        "variants": {
            "type=bottom": {
                "model": f"{modid}:{name}",
                "y": 0
            },
            "type=top": {
                "model": f"{modid}:{name}",
                "y": 180
            },
            "type=double": {
                "model": f"{modid}:{name}_double"
            }
        }
    }


def get_block_model_json(name, modid):
    """
    Génère le JSON du modèle block pour une dalle.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans models/block/{name}.json et {name}_double.json
    """
    base_model = {
        "parent": "block/slab",
        "textures": {
            "bottom": f"{modid}:blocks/{name}",
            "top": f"{modid}:blocks/{name}",
            "side": f"{modid}:blocks/{name}"
        }
    }
    double_model = {
        "parent": "block/slab_top",
        "textures": {
            "bottom": f"{modid}:blocks/{name}",
            "top": f"{modid}:blocks/{name}",
            "side": f"{modid}:blocks/{name}"
        }
    }

    return base_model, double_model


def get_item_model_json(name, modid):
    """
    Génère le JSON du modèle item pour la dalle.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON à écrire dans models/item/{name}.json
    """
    return {
        "parent": f"{modid}:block/{name}"
    }
