import requests
from lxml import html
import json
from concurrent.futures import ThreadPoolExecutor, as_completed

BASE_URL = "https://www.gamergeeks.net/"
VERSIONS = [f"1.{i}" for i in range(11, 22)]
HEADERS = {
    "User-Agent": "Mozilla/5.0"
}
MAX_THREADS = 10


def get_blocks(version):
    url = f"{BASE_URL}/apps/minecraft/new-blocks-in-{version}-java-edition"
    response = requests.get(url, headers=HEADERS)
    tree = html.fromstring(response.content)

    block_labels = tree.xpath('/html/body/div[2]/div[1]/article/div[2]/div[2]/div/div[1]/div//span//a/text()') + \
                   tree.xpath(
                       '/html/body/div[2]/div[1]/article/div[2]/div[2]/div/div[1]/div//span[not(contains(@class, "icon-minecraft"))]//text()')

    block_ids = [label.strip().replace(" ", "_").lower() for label in block_labels if label.strip()]

    # Combinaison ID / label
    combined = [(bid, lbl.strip()) for bid, lbl in zip(block_ids, block_labels)]

    # 🔴 Filtrer les "spawn_egg"
    filtered = [item for item in combined if "spawn_egg" not in item[0]]

    # ✅ Supprimer les doublons (conserve le premier)
    seen = set()
    unique = []
    for item in filtered:
        if item[0] not in seen:
            seen.add(item[0])
            unique.append(item)

    return unique

def scrape_version(version):
    print(f"[{version}] Fetching block names...")
    blocks = get_blocks(version)
    results = []

    print(f"[{version}] Processing {len(blocks)} blocks...")
    for i, (block_id, label) in enumerate(blocks, 1):
        results.append({
            "version": version,
            "id": block_id,
            "label": label.strip(),
            "type": "",
            "hardness": 0.0,
            "tab": ""
        })
        print(f"  [{version}] Processed {i}/{len(blocks)}: {label.strip()}")

    return results


def main():
    all_blocks = []
    for version in VERSIONS:
        all_blocks.extend(scrape_version(version))
    with open("all_minecraft_blocks.json", "w", encoding="utf-8") as f:
        json.dump(all_blocks, f, indent=4, ensure_ascii=False)
    print(f"\n[✓] Finished. Total blocks: {len(all_blocks)} written to all_minecraft_blocks.json")


if __name__ == "__main__":
    main()
