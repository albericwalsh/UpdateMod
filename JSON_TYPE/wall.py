def get_blockstate_json(name, modid):
    """
    Génère le JSON blockstate pour un mur (wall).

    Args:
        name (str): Nom du mur.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON blockstate.
    """
    variants = {}
    # On génère toutes les combinaisons possibles des 5 propriétés booléennes
    for up in [True, False]:
        for north in [True, False]:
            for south in [True, False]:
                for west in [True, False]:
                    for east in [True, False]:
                        key = f"up={str(up).lower()},north={str(north).lower()},south={str(south).lower()},west={str(west).lower()},east={str(east).lower()}"
                        model = ""
                        rotation = 0

                        # Si up true, on utilise le model post
                        if up:
                            model = f"{modid}:block/{name}_post"
                        # Sinon si un des cotés est vrai, model side
                        elif north or south or west or east:
                            model = f"{modid}:block/{name}_side"
                        else:
                            model = f"{modid}:block/{name}_post"  # cas par défaut

                        variants[key] = {"model": model, "y": rotation}

    return {"variants": variants}


def get_block_model_json(name, modid):
    """
    Génère les 3 modèles JSON pour un mur (post, side, inventory).

    Args:
        name (str): Nom du mur.
        modid (str): Identifiant du mod.

    Returns:
        dict: Dictionnaire où les clés sont les noms des modèles, valeurs le JSON du modèle.
    """
    base_textures = {
        "all": f"{modid}:blocks/{name}"
    }

    post = {
        "parent": "block/wall_post",
        "textures": base_textures
    }

    side = {
        "parent": "block/wall_side",
        "textures": base_textures
    }

    inventory = {
        "parent": "block/wall_inventory",
        "textures": base_textures
    }

    return {
        f"{name}_post": post,
        f"{name}_side": side,
        f"{name}_inventory": inventory
    }


def get_item_model_json(name, modid):
    """
    Génère le JSON modèle item qui référence le modèle inventory.

    Args:
        name (str): Nom du mur.
        modid (str): Identifiant du mod.

    Returns:
        dict: JSON modèle item.
    """
    return {
        "parent": f"{modid}:block/{name}_inventory"
    }
