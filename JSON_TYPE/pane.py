def get_blockstate_json(name, modid):
    """
    Génère le JSON blockstate pour un bloc type glass pane ou iron bars.

    Args:
        name (str): Nom du bloc.
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
    Génère le modèle block JSON pour un glass pane ou iron bars.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: Dictionnaire modèle block.
    """
    model = {
        "parent": "block/template_glass_pane",
        "textures": {
            "pane": f"{modid}:blocks/{name}",
            "edge": f"{modid}:blocks/{name}_edge"
        }
    }
    return {name: model}


def get_item_model_json(name, modid):
    """
    Génère le modèle item JSON pour le bloc type glass pane ou iron bars.

    Args:
        name (str): Nom du bloc.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON modèle item.
    """
    return {
        "parent": f"{modid}:block/{name}"
    }
