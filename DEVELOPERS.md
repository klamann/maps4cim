
# Developers

Here are some useful instructions for developers who want to work with the maps4cim source code.

## Getting the source code

The spurce code can be directly retrieved from github

[github.com/Klamann/maps4cim.git](https://github.com/Klamann/maps4cim.git)

## Building the project

maps4cim uses [Apache Maven](https://maven.apache.org/) as build automation tool. If you have no experience with Maven, you should make yourself familiar with it before you continue. Don't worry, it's easy to use and chances are, it's already part of your IDE.

To build maps4cim, follow these steps:

* Checkout the source code and import the subfolder `maps4cim` as Maven project in your IDE
* Make sure the project settings are configured to use Java 6 or higher
* Run `mvn:install` on the `pom.xml` of maps4cim (core). Wait for Maven to resolve the dependencies (this could take a while).
* maps4cim should now be ready to use. if you run `Launcher.java` without any arguments, you should see some usage instructions on the command line.

To build maps4cim-gui:

* make sure maps4cim has been installed using `mvn:install` (follow instructions above). maps4cim-gui depends on maps4cim and it has to be part of your local repository or it can't be resolved as a dependency.
* Make sure the project settings are configured to use Java 6 or higher
* Run `mvn:package` on the `pom.xml` of maps4cim-gui. This should work faster now, as most of the dependencies were resolved while compiling maps4cim
* Run the resulting JAR from the target-folder :)

## Project structure

maps4cim consists of two projects:

* *maps4cim* - the core module, which handles the map generator and all associated work. CLI only, in combinaton with XML configuration files.
* *maps4cim-gui* - the fancy graphical user interface, which depends on the core module. Does not provide any additional functionality, but makes maps4cim way easier to use.

This description will give a brief overview of the code structure and the most important classes.

### maps4cim (core)

tbd

### GUI

tbd

