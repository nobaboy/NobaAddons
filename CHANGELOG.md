# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## Unreleased

### Fixed

- MayorAPI and PartyAPI not working due to internal changes in Alpha 11 - celeste

## 0.1.0-Alpha.11 - 2024-12-26

### Added

- Attribute Shard and Rancher's Boots Speed Slot Infos - nobaboy
- Armor Glint Tweaks - celeste
- Trophy Fish slot info in Odger menu - celeste
- Fishing Bobber Timer - celeste
- Trophy Fish chat message counts - celeste
  - This requires that you open Odger's menu at least once for accurate counts
- Ping On Burrow Find - nobaboy
- `/noba refill` any item from sacks - celeste
- Support for 1.21.4 - celeste
- Option to make Sea Creature Alert also notify when catching a Carrot King (since not many people fish for it) - celeste
- Option to make Sea Creature Alert also send a message in party chat - celeste
- Added a fix for Skyblocker's Correct Transparent Skin Pixels feature on 1.21.1 and 1.21.3 - celeste
- Update notifications for new versions - celeste

### Changed

- The mod will now pull some data from a repository - celeste
  - This is a fairly large internal change; if something doesn't load properly, please try `/noba repo update` (and report it in the Discord)
- Most mod message strings are now translatable - celeste
  - This does not include server messages - the mod still requires that your Hypixel language is set to English to function properly
- Redesigned keybind screen and moved info boxes to their own custom screen - nobaboy
- Keybinds now allows use of mouse buttons (not including the primary Left, Right, and Middle mouse buttons) - nobaboy
- `/noba refillpearls` has been changed to `/noba refill pearls`, along with adding `superboom` and `leaps` - celeste
- Mod chat message prefix & colors - celeste
- Enchanted Book Slot Info now uses repo data instead of getting the name from the id - nobaboy

### Fixed

- Temporary Waypoints not getting parsed in all chat because of player emblems - nobaboy
- MayorAPI not resetting once an election ends due to a typo - nobaboy
- PetAPI resetting current pet upon clicking any item other than pets in the pets menu - nobaboy
- PetAPI thinking you don't have a pet spawned until opening the pets menu at least once per session - celeste
- Announce Rare Drops announcing the same item more than once if it is moved around your inventory or when swapping islands - nobaboy

## 0.1.0-Alpha.10 - 2024-12-02

### Added

- `/noba ping` command with the new ping system - celeste
- Option to reduce mouse sensitivity in the Garden - celeste
- Reindrake sound filters - celeste
- Automatically unlock mouse on teleport - nobaboy
- Sea Creature Spawn Message chat filter - nobaboy

### Changed

- The mod will now check ping every 10 seconds instead of once a minute - celeste
- Switched ping to use the vanilla ping query packet instead of the Hypixel Mod API - celeste
- You can now mute the notification sound for Inquisitor and Sea Creature Alerts - nobaboy

### Fixed

- Pet Slot Info not displaying Golden Dragon level properly - nobaboy
- Subcommands not registering their aliases - celeste
- Some enchant names being wrong in Enchant Slot Info because the id doesn't match the enchant's name - nobaboy
- Sea Creature Alert not having a notification sound - nobaboy
- Sound filters - celeste

## 0.1.0-Alpha.9 - 2024-11-25

### Added

- Mythological Ritual Helper - nobaboy
- Slot Info - nobaboy
- Sea Creature Alert - nobaboy
- Mouse Lock - nobaboy
- Gone With The Wind Sound Filter - nobaboy
- Cancel Item Update Animation - celeste

### Changed

- PartyAPI now relies on the Hypixel Mod API - celeste

### Fixed

- Text rendered in world space now correctly shows through blocks, though the shadow is slightly buggy and there isn't much I can do about it - nobaboy

## 0.1.0-Alpha.8.1 - 2024-11-17

### Fixed

- PartyAPI getting the party list more than once when you swap worlds before it processes the list - nobaboy
- A crash caused by item position on 1.21 - celeste
- Thunder Sparks are actually visible in lava now - nobaboy

## 0.1.0-Alpha.8 - 2024-11-17

### Added

- Command Keybinds - celeste
- Remove Front-Facing Camera - celeste
- Arm Swing Animation Tweaks - celeste
- First Person Rendering - celeste
- Sound Filters - nobaboy

### Changed

- Set a minimum scale for world rendered text - nobaboy

### Fixed

- Thunder Sparks are now properly visible when they're in lava - nobaboy
- SkyBlock item stars are stored under `upgrade_level` not `dungeon_item_level` - nobaboy

## 0.1.0-Alpha.7 - 2024-11-11

### Fixed

- PartyAPI never resetting `gettingList` resulting in any party related message being slightly broken

## 0.1.0-Alpha.6 - 2024-11-10

- Initial public release
