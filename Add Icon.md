# Ajout d'icon
Pour ajouter une icône à votre projet, vous pouvez suivre les étapes suivantes :
1. **Importer votre icon**: déplacez vortre icon dans `assets/updatemod/textures/icons`.
2. **Déclarer l'icon dans le fichier JSON**: ajoutez une entrée dans le fichier JSON `assets/updatemod/data/icons.json` pour référencer l'icon. Suivez le model suivant :
```json
{ 
  "token": "[^]",
  "name": "Up Arrow",
  "path": "textures/icons/arrow_up.png",
  "tintable": true,
  "category": "arrow",
  "CategoryIcon": true
}
```
- `token` : Un identifiant unique pour l'icon.
- `name` : Le nom de l'icon.
- `path` : Le chemin relatif vers le fichier de l'icon.
- `tintable` : Indique si l'icon peut être teintée en bitmap (true/false).
- `category` : La catégorie à laquelle l'icon appartient.
- `CategoryIcon` : Indique si l'icon est une icône de catégorie (true/false). Une icon unique par catégorie est requise.
3. **Enregistrer les modifications**: Sauvegardez le fichier JSON après avoir ajouté l'entrée pour votre icon.
4. **Tester l'icon**: Build et Lancez minecraft pour vérifier que l'icon s'affiche correctement dans le jeu.

En suivant ces étapes, vous pourrez ajouter de nouvelles icônes à UpdateMod facilement.