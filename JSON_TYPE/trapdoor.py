def get_blockstate_json(name, modid):
    """
    Génère le JSON blockstate pour une trapdoor.

    Args:
        name (str): Nom de la trapdoor.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON blockstate.
    """
    variants = {}
    facings = ["north", "south", "west", "east"]
    opens = [True, False]
    halves = ["top", "bottom"]
    powereds = [True, False]

    for facing in facings:
        for open_ in opens:
            for half in halves:
                for powered in powereds:
                    key = f"facing={facing},open={str(open_).lower()},half={half},powered={str(powered).lower()}"
                    yrot = {"north": 0, "east": 90, "south": 180, "west": 270}[facing]

                    model = f"{modid}:block/{name}"
                    # Rotation depends on facing and half, and open state usually handled in model JSON
                    # Here just rotate y by facing rotation
                    variants[key] = {"model": model, "y": yrot}

    return {"variants": variants}


def get_block_model_json(name, modid):
    """
    Génère le modèle JSON pour une trapdoor.

    Args:
        name (str): Nom de la trapdoor.
        modid (str): Identifiant du mod.

    Returns:
        dict: Dictionnaire modèles (nom -> JSON).
    """
    textures = {
        "trapdoor": f"{modid}:blocks/{name}"
    }

    model = {
        "parent": "block/trapdoor",
        "textures": textures
    }

    return {
        name: model
    }


def get_item_model_json(name, modid):
    """
    Génère le modèle item JSON pour la trapdoor.

    Args:
        name (str): Nom de la trapdoor.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON modèle item.
    """
    return {
        "parent": f"{modid}:block/{name}"
    }
