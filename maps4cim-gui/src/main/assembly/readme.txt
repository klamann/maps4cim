
# maps4cim

a real world map generator for CiM 2

Get the latest updates from
http://www.nx42.de/projects/maps4cim/


## Discussion & Support

For more information, usage guides, technical support, praise and criticism,
please visit the forums:

* en: http://www.cimexchange.com/topic/2204-beta-maps4cim-a-real-world-map-generator-for-cim-2/
* de: http://www.citiesinmotion.net/index.php?page=Thread&postID=31558


## Release / Downloads

You can get the latest binary release from one of the following mirrors:

* en: http://www.cimexchange.com/files/file/694-maps4cim/
* de: http://www.citiesinmotion.net/index.php?page=DatabaseItem&id=455


## Core Features

* Create realistic maps based on real parts of the world, with data from these
  free (as in free beer and in free speech) data sources:
  - SRTM: Elevations of the earth's surface with a resolution of about 90x90 
    meters per data point. The data comes directly from the servers of the
    United States Geological Survey (many thanks!)
  - OpenStreetMap: Free map data from which the ground textures are generated 
    that will be shown on the resulting map. The required data for the selected 
    map sections is downloaded from the Overpass-Servers (with best regards 
    to the operators!)
* Generate elevations from grayscale heightmaps
  (with support for 16bit graphics)
* Project arbitrary pictues on the ground, e.g. historical maps, the colormap
  that belongs to your heightmap or some lolcats, just for the heck of it ;)
* Choose between the european and american building set on map creation and
  convert existing maps from european to american and vice versa.


## Instructions

### Prerequisites

* maps4cim runs on Windows, Mac, Linux and any other operating system for which
  a Java Runtime is available
* Make sure you have a Java Runtime version 6 or higher installed:
  https://java.com/download/
* Mac users: Don't use your built-in "Java app" or something like that,
  it will cause you trouble, even if maps4cim starts.


### Installation

1. Unzip the downloaded file `maps4cim.zip`
2. If you are on Windows, start `maps4cim.exe`. On other operating systems,
   launch `maps4cim.jar`. If you run into memory issues, use the provided
   launch skript `maps4cim.sh`.
3. Profit!

If you want to remove all user data that was stored by maps4cim, open maps4cim's
settings (menu `Tools` → `Settings`) and hit the `Uninstall`-Button.


### Quick Start

Launch maps4cim - you will be presented with the main window.
First, make yourself familiar with the map; navigation works like any other map
service on the web. Right click on the map to select a location.

You may use the settings panel on the right to adjust you configuration, but for
now, just leave everything as is and hit the `Render`-Button on the bottom
right. Select a location on your hard drive and off we go!

maps4cim will download all required source data on the fly and the map will be
rendered for you. After 1-2 minutes, the flashing progress bar turns solid green
and your map is ready. Move your map to 
`C:\Users\MYNAME\AppData\LocalLow\Colossal Order\Cities in Motion 2\Maps`
(Windows) or `/Users/MYNAME/Library/Application Support/Colossal Order/` (Mac)
and start Cities in Motion 2. You can now edit your map in the map editor.

There are a lot more features to explore, just have a look around. For more
information on how to use maps4cim, use the built-in documentation and
visit the forums (see links below). Have fun :)


## Get involved

maps4cim is free software and you can get the source code from github

> https://github.com/Klamann/maps4cim.git

Please note that this is still an early source code release, with important
parts of the code in an undocumented and sometimes rather dirty state,
but it should be well enough to work with.

If you want to contribute new features, don't hesitate to do so!
Fork the project and write your code, and if you like what you've done and
think it may be useful for others, create a pull request.

For some hints on how to work with the code, see [Deveopers.md].
More documentation can be found in the [docs] folder.


## Changelog

1.0.0

* New data sources:
  - Grayscale heightmaps as source for elevations
  - Arbitrary images as source for ground textures
  - Custom OSM XML Files as source for ground textures
* Full Support for the European Cities DLC.
  The building style of existing maps can be changed in the Metadata-Editor
* Performance of OpenStreetMap-Downloads improved, the hard download limits
  were removed
* Windows-Executable (.exe-file) - note: Java is still required!
* Program Update Notifications
* Cache Janitor: removes deprecated and broken files from the cache
* Drastically improved error handling
* Useful new options in the settings menu (log file, updates, cache janitor, ...)
* Many small improvements and lots of bugfixes, see [CHANGELOG.md]

0.9.3.1 beta

* This is a bugfix release, it changes the default Overpass server to
  overpass-api.de, because the previous server caused trouble, and improves
  error handling for faulty OSM XML data
* Lots of awesome features are in the pipeline for the 1.0 release!

0.9.3 beta

* Map header format analyzed: Can now write correct file name, preview image and
  creation date
* Metadata-Editor introduced: Change the preview picture and some other hidden 
  information about your maps!
* Usability-Improvements in the GUI


## License

maps4cim is free software, distributed under the terms of the 
Apache License, Version 2.0

    maps4cim - a real world map generator for CiM 2
    Copyright 2014 Sebastian Straub
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

