
# Changelog

### 0.9.2 beta

* Improved handling of missing terrain data
* Proxy Settings available (via Edit -> Settings)
* minor bugfixes & improvements

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

* No rivers / water above 0m
* No reliefs above +61° und below -60° latitude, some small gaps in between

## Known Issues

* Overlap at -180°, 180° -> not possible, Overpass rejects requests
* Broken Polygons (mostly when relations are involved)
* Missing support for multi-polygons
* Interpolation of gaps in SRTM dataset sometimes creates ugly results
* Incomplete color support


## Ideas for Future Releases

* HeightScale: auto
* Switch Renderer to support complex relation types and more
* Load custom OSM XML Files
* Load custom image files (ground texture & elevation)
* Do not write File output immediately -> temporaray result & interfaces for later changes
* Relief-Changeset based on OpenStreetMap data (e.g. for rivers)
* Analyze Prefix: Name, Date, Map-thumb, ...
* Support for some Relation types within OSM data
  - multipolygon (inner/outer)
* Improved interpolation of missing relief data
* Parallelize (has some potential ;))

### TODO-List

* gui: restore relief/texture enabled/disabled
* Global log-level switch (verbose, default, off would be nice -> warnings are always shown!)
* Make XML schema-aware!
* Cleanup code
* Write unit tests, where appropriate

