
# Changelog

### 1.0.0 (2014-05-15)

* New data sources
  - Grayscale heightmaps as source for elevations
  - Arbitrary images as source for ground textures
  - Custom OSM XML Files as source for ground textures
  - Trivial sources: static height offset, single ground texture
* European Cities
  - Full Support for European Cities DLC, select european or american building
    style before rendering
  - Building style of existing maps can be changed in the Metadata-Editor
* Other features
  - Improved processing of Overpass-Queries and gzip compression while downloading.
    Allows larger maps and faster requests of OpenStreetMap data
  - Therefore the hard map size limits were removed, the "ultra" preset can now
    be used for maps with up to 32x32 km size, for areas with low data density
	even more.
  - For Windows users: Provided a Windows-Executable (.exe-file). Should be used
    instead of the .jar, as some useful JVM-parameters are passed with it.
  - Update-Check as background task or on user request. Can be disabled in the
    settings. Will NOT run as a system service, only when maps4cim is started!
  - Cache Janitor, removes deprecated and broken files from the cache
  - Config validation: Get detailed reports about errors in your configuration.
* User Interface Improvements
  - The user interface has been adjusted to the various new options
  - Detailed preview of grayscale heightmaps with adjustable lower/upper bound,
	statistical data and boxplots visualizing heights in the resulting map
  - Texture mixer with live preview
  - Various new options in the settings menu: Open maps4cim's application
    folder, cache folder or log file, uninstall maps4cim completely, change the 
	update policy, clear certain elements from the cache, configure and run the
	cache janitor
  - A fancy splash screen is displayed while maps4cim is starting
  - Invalid values are detected in most of the form fields
  - Quick start guide and tooltips for most every component
* Minor Changes
  - Height offset "auto" now implemented & selected by default
  - Syntax highlighting in XML tab
  - File chooser now asks for comfirmation before overwriting
  - Some glitches with the map selection were fixed
  - Maps are written to temp folder and are only moved if the generator
	finished without critical errors
  - Correct OpenStreetMap attribution in the map panel
  - ImageJ library added, replaces self-made sloppy interpolation algorithms and
	adds support for 16 and 32 bit heightmaps from a variety of image formats
  - SRTM cache bug fixed: Broken downloads will not be cached anymore

### 0.9.3.1 beta (2014-04-22)

* This is a bugfix release, it changes the default Overpass server to
  overpass-api.de, because the previous server caused trouble, and improves
  error handling for faulty OSM XML data
* Lots of awesome features are in the pipeline for the 1.0 release!

### 0.9.3 beta  (2013-11-27)

* Map header format analyzed: Can now write correct file name, preview image and
  creation date
* Metadata-Editor introduced: Change the preview picture and some other hidden 
  information about your maps!
* Usability-Improvements in the GUI

### 0.9.2 beta  (2013-10-17)

* Improved handling of missing terrain data
* Proxy Settings available (via Edit -> Settings)
* minor bugfixes & improvements

### 0.9.1 beta (2013-07-04)

* Colorful log messages
* Improved error handling
* Default save location for Windows set to CiM 2 map folder
* Data source for texture and relief can be selected now
* GUI arrangement improved, relief and texture download can be disabled

### 0.9 beta (2013-06-26)

* Initial release
* so many features... just launch the program :)


# Bugs and Features

## Known Limitations

* No rivers / lakes / any kind of water above 0m (restriction by game engine)
* No relief above +61° und below -60° latitude, some small gaps in between
* No maps with a size other than 8x8km are supported by the game engine

## Known Issues

* OSM data
  - Overlap at -180°, 180° longitude -> Overpass servers reject requests
  - Broken Polygons (mostly when relations are involved)
  - Missing support for multi-polygons
* Interpolation of large gaps in SRTM dataset may cause ugly reliefs 
  (mainly near SRTM boundries (+/-60° lon) and in the mountains)

## Roadmap

### Planned Features for 1.1

* Improved interpolation of missing relief data
* OSM XML: Add support for multipolygons
* Heightmap: colored preview images

release schedule: "when it's done"

## Ideas for Future Releases

* OSM XML: Add support for complex relation types, e.g. multipolygons
* Rotate map source rectangle
* Relief-Changeset based on OpenStreetMap data (e.g. for rivers)

## TODO-List

### 1.0 stable

* done

### 1.1 testing

* xml config: IDREF relation from entity to color
* selection inaccurate for large extents
* Select Overpass Server
* store paths for config open/save, etc.
* Notification API
* gui: map zoom buttons, OSM attribution
* user input validation
* Finish HeightmapWindow
* Texture sources
  - single: update color chooser
  - OSM/file: custom detail selector
  - Custom Image: color mapping
* Global log-level switch for GUI-console output (verbose, default, off & show stacktrace
  option would be nice -> warnings/errors are always shown!)
* Make XML schema-aware!
* Cleanup code
* Write unit tests, where appropriate
