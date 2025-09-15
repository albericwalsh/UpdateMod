def get_blockstate_json(name, modid):
    """
    Génère le JSON blockstate pour une clôture (fence).

    Args:
        name (str): Nom de la clôture.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON blockstate.
    """
    variants = {
        "": {"model": f"{modid}:block/{name}"}
    }
    return {"variants": variants}


def get_block_model_json(name, modid):
    """
    Génère le modèle block JSON pour une clôture.

    Args:
        name (str): Nom de la clôture.
        modid (str): Identifiant du mod.

    Returns:
        dict: Dictionnaire modèle block.
    """
    model = {
        "parent": "block/fence",
        "textures": {
            "texture": f"{modid}:blocks/{name}"
        }
    }
    return {name: model}


def get_item_model_json(name, modid):
    """
    Génère le modèle item JSON pour la clôture.

    Args:
        name (str): Nom de la clôture.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON modèle item.
    """
    return {
        "parent": f"{modid}:block/{name}"
    }
