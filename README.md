# Potion Cauldron

----------------

### Description
- A feature from Bedrock edition
- Store up to 3 bottles of same potion  in a cauldron
- Right-click with arrows in hand to craft tipped arrows
- Apply potion effect to any entities standing inside
- Generates in swamp huts

<img style="margin-top: 10px; margin-left: 30px" src="https://raw.githubusercontent.com/maxoduke/Potion-Cauldron/1.20/media/gifs/1.gif" alt="Image 1" width="500" />
<img style="margin-top: 10px; margin-left: 30px" src="https://raw.githubusercontent.com/maxoduke/Potion-Cauldron/1.20/media/gifs/2.gif" alt="Image 2" width="500" />

### Requirements
- <a target="_blank" href="https://fabricmc.net">Fabric Loader</a> with <a target="_blank" href="https://modrinth.com/mod/fabric-api">Fabric API</a>
  - **OR**
- <a target="_blank" href="https://quiltmc.org">Quilt Loader</a> with <a target="_blank" href="https://modrinth.com/mod/qsl">QFAPI</a>
- <a target="_blank" href="https://modrinth.com/mod/modmenu">Mod Menu</a> for config GUI (optional)

### Installation
- Download the appropriate JAR from either <a target="_blank" href="https://modrinth.com/mod/potion-cauldron">Modrinth</a> or <a target="_blank" href="https://curseforge.com/minecraft/mc-mods/potion-cauldron">CurseForge</a> and put it in **_.minecraft/mods_** folder.
- For multiplayer, the mod must be installed on all clients and server side.

### Configuration
- File Location: **_.minecraft/config/potion-cauldron.json_**
- GUI available through <a target="_blank" href="https://modrinth.com/mod/modmenu">Mod Menu</a>


- Changing config doesn't require restarting your game (or server).
  - On single player and LAN hosted worlds:
    - When using Mod Menu, clicking the "Save" button will sync with all connected clients.
    - When not using Mod Menu, change the JSON file and use the command "/potioncauldron config reload".
  - On dedicated servers, after changing the config:
    - Use the "/potioncauldron config reload" command on the server console.
    - OP players can also use the above command in-game.


- Available configuration options :
  - **Evaporate if different potions are mixed**
    - **ON:** If a player tries to put a different potion in a potion cauldron, the potion from the bottle and the cauldron will evaporate and disappear.
    - **OFF:** Potion in hand is used as normal if it's different from the potion in cauldron.
  - **Allow merging different types of the same potion**
    - **ON:** Allows merging normal, splash and lingering variants of the same potion. Taking back the potion will give whatever type was put in last.
    - **OFF:** Applies whatever is set in **Evaporate if different potions are mixed** option.
    - It's how it works in Bedrock edition. For instance, you can put a Potion of Fire Resistance, Splash Potion of Fire Resistance and a Lingering Potion of Fire Resistance in the same cauldron. Picking up would give back 3 bottles of Lingering Potion of Fire Resistance.
  - **Apply potion effects to entities standing inside and its duration**
  - **Allow creating tipped arrows and the maximum number of arrows for each level**
  - **Generate in swamp huts**
    - **ON:** When a swamp hut (witch hut) is generated, a cauldron filled with a random potion, type and level will be generated instead of an empty one.
    - **OFF:** Generates with an empty vanilla cauldron.
    - The chances of the generated random potion, its type and level can be configured.

### Compatibility with other mods
- Compatible with <a href="https://modrinth.com/mod/sodium">Sodium</a>
- Should be compatible with mods that modify vanilla cauldron behavior.
- Should be compatible with modded potions that are added using the vanilla potion registry.
- May not be compatible with custom potion systems.

### Inclusion in modpacks
- Feel free to include this mod in any modpacks.

### Known issues
- Liquid transparency or potion particle effects may not work with some shaders.
- Fabulous graphics setting breaks liquid transparency.

**The mod is still in alpha phase. So expect some bugs. If you encounter any problems, please create an issue on the <a href="https://github.com/maxoduke/Potion-Cauldron/issues">Issue Tracker</a>.**
