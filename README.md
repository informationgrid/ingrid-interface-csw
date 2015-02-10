CSW Interface
====================

This software is part of the InGrid software package. The CSW interface is an implementation of the standard for exposing a catalogue of geospatial records in XML on the Internet. 

https://en.wikipedia.org/wiki/Catalog_Service_for_the_Web

It indexes all connected InGrid meta data sources regulary and delivers a high performance search.

Features
--------

- harvests meta data from a InGrid data source into a high performance index at a certain schedule
- flexible indexing functionality
- provides a CSW AP ISO 1.0 compatible interface on the harvested data
- add filters for narrowing results to certain data providers or data sources
- GUI for easy administration


Requirements
-------------

- a running InGrid Software System

Installation
------------

Download from https://dev.informationgrid.eu/ingrid-distributions/ingrid-interface-csw/
 
or

build from source with `mvn package assembly:single`.

Execute

```
java -jar ingrid-interface-csw-x.x.x-installer.jar
```

and follow the install instructions.

Obtain further information at http://www.ingrid-oss.eu/ (sorry only in German)


Contribute
----------

- Issue Tracker: https://github.com/informationgrid/ingrid-interface-csw/issues
- Source Code: https://github.com/informationgrid/ingrid-interface-csw
 
### Set up eclipse project

```
mvn eclipse:eclipse
```

and import project into eclipse.

### Debug under eclipse

- execute `mvn install` to expand the base web application
- set up a java application Run Configuration with start class `de.ingrid.interfaces.csw.admin.JettyStarter`
- add the VM argument `-Djetty.webapp=src/main/webapp -Dingrid_home=.` to the Run Configuration
- add `src/main/resources` to class path
- the admin gui starts per default on port 8082, change this with VM argument `-Djetty.port=8083`

Support
-------

If you are having issues, please let us know: info@informationgrid.eu

License
-------

The project is licensed under the EUPL license.
