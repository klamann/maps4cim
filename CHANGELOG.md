
# Changelog

### 0.9.1 beta

* Colorful log messages
* Improved error handling
* Default save location for Windows set to CiM 2 map folder
* Data source for texture and relief can be selected now
* GUI arrangement improved, relief and texture download can be disabled

### 0.9 beta

* Initial release
* so many features... just launch the program :)

# Bugs and Features

## Known Limitations

* No rivers
* No reliefs above +61° und below -60° latitude

## Known Issues

* Overlap at -180°, 180° -> not possible, Overpass rejects requests
* Broken Polygons (mostly when relations are involved)
* Missing support for multi-polygons
* Interpolation of gaps in SRTM dataset sometimes create ugly results
* Incomplete color support


## Planned Features

* Proxy support
* HeightScale: auto
* Do not write File output immediately -> temporaray result & interfaces for later changes
* Relief-Changeset based on OpenStreetMap data (e.g. for rivers)
* Analyze Prefix: Name, Date, Map-thumb, ...
* Support for some Relation types within OSM data
  - multipolygon (inner/outer)
* Improved interpolation of missing relief data
* Parallelize stuff

### TODO-List

* overlap with nulls...
* gui: restore relief/texture enabled/disabled
v Error Handling Messages
  v SocketTimeoutException
v Default Save folder
v Don't render elevation and texture option
v 8km map size notes (tooltip, usage text)
v remove debug output (e.g. target)
v Null-Tile Handling
v SRTM extremes
v Texture presets
* Global log-level switch (verbose, default, off would be nice -> warnings are always shown!)
* Make XML schema-aware!
* Cleanup code
* Write unit tests, where appropriate


### To test

v SRTM: combination at extreme places (e.g. 0,0,0,0)
v SRTM: combination of many tiles (e.g. 3x3)
v Adjusted Overpass-Filter (working as expected?)
v circle rendering
v GUI


