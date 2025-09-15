import json
import os
import sys
import JSON_TYPE

MODID = "updatemod"
ASSETS_DIR = f"src/main/resources/assets/{MODID}"
DATA_FILE = os.path.join(ASSETS_DIR, "data/blocks.json")
BLOCKSTATES_DIR = os.path.join(ASSETS_DIR, "blockstates")
MODELS_BLOCK_DIR = os.path.join(ASSETS_DIR, "models/block")
MODELS_ITEM_DIR = os.path.join(ASSETS_DIR, "models/item")
LANG_FILE = os.path.join(ASSETS_DIR, "lang/en_US.lang")

os.makedirs(BLOCKSTATES_DIR, exist_ok=True)
os.makedirs(MODELS_BLOCK_DIR, exist_ok=True)
os.makedirs(MODELS_ITEM_DIR, exist_ok=True)
os.makedirs(os.path.dirname(LANG_FILE), exist_ok=True)

OVERWRITE = "--overwrite" in sys.argv

def save_json(filepath, data):
    if not OVERWRITE and os.path.exists(filepath):
        print(f"⏩ Skipping existing file: {filepath}")
        return
    with open(filepath, "w") as f:
        json.dump(data, f, indent=2)

def write_lang(id, label):
    line = f"tile.{MODID}.{id}.name={label}\n"
    if OVERWRITE:
        with open(LANG_FILE, "a") as f:
            f.write(line)
    else:
        if os.path.exists(LANG_FILE):
            with open(LANG_FILE, "r") as f:
                if line in f.readlines():
                    return
        with open(LANG_FILE, "a") as f:
            f.write(line)

def get_generator_module(btype):
    mapping = {
        "normal": JSON_TYPE.normal,
        "facing": JSON_TYPE.facing,
        "facing_multi": JSON_TYPE.facing_multi,
        "stairs": JSON_TYPE.stair,
        "slab": JSON_TYPE.slab,
        "log": JSON_TYPE.log,
        "wall": JSON_TYPE.wall,
        "door": JSON_TYPE.door,
        "trapdoor": JSON_TYPE.trapdoor,
        "fence": JSON_TYPE.fence,
        "pane": JSON_TYPE.pane,
    }
    mod = mapping.get(btype)
    if mod is None:
        print(f"⚠️  Unknown block type '{btype}', defaulting to normal")
        mod = JSON_TYPE.normal
    return mod

def main():
    if not os.path.exists(DATA_FILE):
        print(f"❌ Missing {DATA_FILE}")
        return

    if OVERWRITE:
        open(LANG_FILE, "w").close()
    elif not os.path.exists(LANG_FILE):
        open(LANG_FILE, "w").close()

    with open(DATA_FILE, "r") as f:
        blocks = json.load(f)

    for block in blocks:
        btype = block.get("type", "normal")
        gen_mod = get_generator_module(btype)

        blockstate_data = gen_mod.get_blockstate_json(block["id"], MODID)
        block_model_data = gen_mod.get_block_model_json(block["id"], MODID)
        item_model_data = gen_mod.get_item_model_json(block["id"], MODID)

        save_json(os.path.join(BLOCKSTATES_DIR, f"{block['id']}.json"), blockstate_data)
        save_json(os.path.join(MODELS_BLOCK_DIR, f"{block['id']}.json"), block_model_data)
        save_json(os.path.join(MODELS_ITEM_DIR, f"{block['id']}.json"), item_model_data)
        write_lang(block["id"], block["label"])

    print("✅ Tous les fichiers ont été générés.")

if __name__ == "__main__":
    main()
