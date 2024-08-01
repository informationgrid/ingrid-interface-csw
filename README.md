CSW Interface
====================

This software is part of the InGrid software package. The CSW interface is an implementation of the standard for exposing a catalogue of geospatial records in XML on the Internet. 

https://en.wikipedia.org/wiki/Catalog_Service_for_the_Web

It indexes all connected InGrid metadata sources regularly and delivers a high performance search.

Features
--------

- harvests metadata from a InGrid data source into a high performance index at a certain schedule
- flexible indexing functionality
- provides a CSW AP ISO 1.0 compatible interface on the harvested data
- add filters for narrowing results to certain data providers or data sources
- GUI for easy administration


Requirements
-------------

- a running InGrid Software System

Installation
------------

Download from https://distributions.informationgrid.eu/ingrid-interface-csw/
 
or

build from source with `mvn clean package`.

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
 
### Setup Eclipse project

* import project as Maven-Project
* right click on project and select Maven -> Select Maven Profiles ... (Ctrl+Alt+P)
* choose profile "development"
* run "mvn compile" from Commandline (unpacks base-webapp) 
* run de.ingrid.interfaces.csw.admin.JettyStarter as Java Application
* in browser call "http://localhost:10101" with login "admin/admin"

### Setup IntelliJ IDEA project

* choose action "Add Maven Projects" and select pom.xml
* in Maven panel expand "Profiles" and make sure "development" is checked
* run de.ingrid.interfaces.csw.admin.JettyStarter
* in browser call "http://localhost:10101" with login "admin/admin"

Support
-------

If you are having issues, please let us know: info@informationgrid.eu

License
-------

The project is licensed under the EUPL license.
