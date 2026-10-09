# Games Planet live data
All URLs are HTTPS or relative to https://gamesplanet13.github.io/GamesPlanetTvApp/.

`data/app.json` accepts `brand` and `core_defaults` (`system` → installed RetroArch core filename stem). A manually chosen core always wins. Schema and other fields are descriptive; polling is fixed at Android's 15-minute minimum.

`data/games.json` is an array. Each item has `id`, `title`, `system`, `name`, `relative` (safe local path), `url`, optional `cover`, `size`, `revision` and `sha256`. For CUE games include `files: [{name, url, revision, sha256}]` for every referenced track. Tracks must stay beneath the CUE's folder. Revision changes invalidate the cached game. Git tree blob SHA in `revision` is a version identifier, not a SHA-256 checksum.

`themes/index.json` is an array of `{id, title, url, sha256, size}`. Put ZIPs inside `themes/`; indexes are generated automatically. ZIPs must contain a theme.xml and its referenced assets. Downloads are SHA-256 verified and extracted with path traversal and size checks. Downloading does not automatically replace the active theme. Select it afterwards in Settings → Live themes. A fresh copy is imported on each download; previous copies remain until app data cleanup.

The app silently caches valid manifests. Network errors retain old data. Local USB catalog and controller mappings remain separate. No APK update is needed for data changes within this schema. App code/emulator/core updates still need installation.

Actions enumerate supported top-level folders (case-insensitive), ignore placeholder files/media, read gamelist.xml names and covers, hide CUE tracks, and reject clearly mismatched archive extensions. Use correct system folders. Large files must be served as actual binaries, not Git LFS pointer text; use an HTTPS hosting URL manually if Pages cannot serve a game.

Periodical background work is subject to Android scheduling; foreground polling applies cached changes after input has been idle. Video is not recreated during data sync. Downloads triggered by selecting a game/theme show progress because that is a requested action.
