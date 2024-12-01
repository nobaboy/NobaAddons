# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## 0.1.0-Alpha.9.1 - 2024-11-30

### Added

- `/noba ping` command with the new ping system - celeste
- Mouse Reduce - celeste

### Changed

- The mod will now check ping every 10 seconds instead of once a minute - celeste
- Switched ping to use the vanilla ping query packet instead of the Hypixel Mod API - celeste
- You can now mute the notification sound for Inquisitor and Sea Creature Alerts - nobaboy

### Fixed

- Pet Slot Info not displaying Golden Dragon level properly - nobaboy
- Subcommands not registering their aliases - celeste
- Enchant names for Enchant Slot Info because some ids don't match the enchant name - nobaboy
- Sea Creature Alert not having a notification sound - nobaboy

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