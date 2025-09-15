def get_blockstate_json(name, modid):
    """
    Génère le JSON blockstate pour une porte.

    Args:
        name (str): Nom de la porte.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON blockstate.
    """
    variants = {}
    halfs = ["lower", "upper"]
    facings = ["north", "south", "west", "east"]
    opens = [True, False]
    powereds = [True, False]

    for half in halfs:
        for facing in facings:
            for open_ in opens:
                for powered in powereds:
                    key = f"half={half},facing={facing},open={str(open_).lower()},powered={str(powered).lower()}"
                    model = ""
                    yrot = {"north": 0, "east": 90, "south": 180, "west": 270}[facing]

                    if half == "lower":
                        if open_:
                            model = f"{modid}:block/{name}_open_lower"
                        else:
                            model = f"{modid}:block/{name}_lower"
                    else:  # upper
                        if open_:
                            model = f"{modid}:block/{name}_open_upper"
                        else:
                            model = f"{modid}:block/{name}_upper"

                    variants[key] = {"model": model, "y": yrot}

    return {"variants": variants}


def get_block_model_json(name, modid):
    """
    Génère les 4 modèles JSON pour une porte.

    Args:
        name (str): Nom de la porte.
        modid (str): Identifiant du mod.

    Returns:
        dict: Dictionnaire modèles (nom -> JSON).
    """
    textures = {
        "bottom": f"{modid}:blocks/{name}_bottom",
        "top": f"{modid}:blocks/{name}_top"
    }

    lower = {
        "parent": "block/door_lower",
        "textures": textures
    }
    upper = {
        "parent": "block/door_upper",
        "textures": textures
    }
    open_lower = {
        "parent": "block/door_lower_open",
        "textures": textures
    }
    open_upper = {
        "parent": "block/door_upper_open",
        "textures": textures
    }

    return {
        f"{name}_lower": lower,
        f"{name}_upper": upper,
        f"{name}_open_lower": open_lower,
        f"{name}_open_upper": open_upper
    }


def get_item_model_json(name, modid):
    """
    Génère le modèle item JSON pour la porte.

    Args:
        name (str): Nom de la porte.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON modèle item.
    """
    return {
        "parent": f"{modid}:block/{name}_lower"
    }
