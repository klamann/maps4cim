
# Developers

Here are some useful instructions for developers who want to work with the 
maps4cim source code.

## Getting the source code

You can get the source code from github

> [github.com/Klamann/maps4cim.git](https://github.com/Klamann/maps4cim.git)

## Building the project

[![travis-img]][travis] 
[![coverity-img]][coverity]

maps4cim uses [Apache Maven](https://maven.apache.org/) as build automation tool.
If you have no experience with Maven, you should make yourself familiar with it 
before you continue. Don't worry, it's easy to use and chances are, it's already 
part of your IDE.

maps4cim consists of several modules, that are united in a single parent 
`pom.xml`. If your are familiar with Maven: just build the parent pom and
look in the target folders of each project. If you need more detailed
instructions, keep on reading...

### ... with just Maven

* Install Maven through your package manager or get it directly from
  [Apache](https://maven.apache.org/download.cgi "Download Apache Maven")
* Checkout the source code of maps4cim
* Open a terminal in the root folder of the source repo and run `mvn install`
* Maven will resolve all dependencies and build all modules of maps4cim.
  The binaries (jar) can be found in the `target`-folders of each module.
  You will find the GUI-application in `./maps4cim-gui/target/`

### ... in Eclipse

* Install [Maven Integration for Eclipse](http://marketplace.eclipse.org/content/maven-integration-eclipse-juno-and-newer)
  or short *m2e* through the Eclipse Marketplace.
* Checkout the source code of maps4cim
* From *File* -> *Import*, choose *Existing Maven Projects* from the *Maven*
  folder and select the location of the maps4cim source code
* You will get a tree view of one root project and three children. Select all of
  them and continue
* Now you should have four projects in your workspace: the root project maps4cim
  and the children -core, -cli and -gui. Only the children do actually contain
  java source code.
* To build all projects, right click on the *maps4cim*-project -> *Run as* ->
  *Maven install*
* Maven will resolve all dependencies and build all modules of maps4cim.
  The binaries (jar) can be found in the `target`-folders of each module.
  You will find the GUI-application in `maps4cim-gui/target/`

Each module has it's own `pom.xml`, so they can be built individually, but make
sure to `mvn install` at least `maps4cim-core` initially, as gui and cli 
depend on this module.

## Project structure

maps4cim consists of three modules:

* *maps4cim-core*: the core module, which handles the map generator and all
  associated work. No user interface of any kind, can only be accessed via
  API-calls.
* *maps4cim-cli*: the command line interface. Provides some basic commands to
  generate maps, but most settings can only be accessed by providing a xml 
  configuration file.
* *maps4cim-gui*: the fancy graphical user interface. Does not provide any 
  additional functionality over the CLI, but makes maps4cim way easier to use.

All of these modules are combined in a single parent pom, which eases the access
to the individual projects and enables the use of continuous integration
software like Jenkins.

This following description will give a brief overview of the code structure
and the most important classes of each module.

### maps4cim-core

tbd

### maps4cim-cli

tbd

### maps4cim-gui

tbd

## Understanding `*.map`-Files

maps4cim generates map files that can be parsed by Cities in Motion 2.
Unfortunately, the developers of CiM 2 did not intend to give access to map
files for modders, and therefore no information about the format of these 
files is officially available. Still, using a hex editor, some patience and a
lot of time, some of the mysteries were uncovered...

A detailed, yet incomplete analysis of the Cities in Motion 2 map file format
can be found in the [MapFileFormat.md](https://github.com/Klamann/maps4cim/blob/master/docs/MapFileFormat.md).

## Get involved

Have an idea on how to improve maps4cim? You've already started writing code?
Great! Send a pull request, if you have results you think are worth sharing,
and feel free to contact me in the [forums][ForumEN] or via email:
<sebastian-straub@gmx.net>.


[travis]: https://travis-ci.org/Klamann/maps4cim
[travis-img]: https://img.shields.io/travis/Klamann/maps4cim.svg
[coverity]: https://scan.coverity.com/projects/klamann-maps4cim
[coverity-img]: https://img.shields.io/coverity/scan/6533.svg
[ForumEN]: http://www.cimexchange.com/topic/2204-maps4cim-a-real-world-map-generator-for-cim-2/ "Support Thread in the cimexchange-forum (english)"
