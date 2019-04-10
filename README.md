Cyface KNIME Nodes
==================

[![Run Status](https://api.shippable.com/projects/5b22529108daf207007b4520/badge?branch=master)](https://app.shippable.com/github/cyface-de/knime-nodes)
[![Coverage Badge](https://api.shippable.com/projects/5b22529108daf207007b4520/coverageBadge?branch=master)](https://app.shippable.com/github/cyface-de/knime-nodes)

These are the KNIME nodes developed and used by the Cyface GmbH. We do provide them without any warranty, but we are happy to share our experience ond get input from the community. If you got an improvement or found a bug please let us know.

Some of the Nodes are work in progress and will not work directly. Please see the following list for a more detailed explanation about the KNIME nodes included.

- **Distance:** A node to convert a signal with non equal spaced data points to a signal with equal spacing.
- **Envelope:** A node to calculate the envelope curve to an input data signal.
- **Read:** A node to read input in Cyface binary format.
- **Export:** A node to export data to the Cyface binary format.
- **Map Matching (WIP):** A node to map match a table of coordinates. This is still work in progress and does not work yet.
- **Smooting:** A node for smoothing a data signal.

Installation 
------------

Requirements:
 
* KNIME, https://www.knime.org 

Steps to get Cyface KNIME Nodes inside KNIME: 

1. Goto Help > Install new software ... menu 
2. Press add button 
3. Fill text fields with `http://download.nodepit.com/cyface/3.6` 
4. Select --all sites-- in work with pulldown 
5. Select Cyface KNIME Nodes 
6. Install software & restart (for now an "Unsigned Content" warning can popup during the installation, you can safely ignore this) 

Development 
-----------

Development requirements:
 
* Eclipse, https://eclipse.org 
* Maven, https://maven.apache.org 

Steps to get development environment setup: 

1. Download, install and run [Eclipse Oxygen](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/oxygenr) 
2. Clone this Git repository 
3. Import contents into Eclipse using `File → Import... → Maven → Existing Maven Projects` 
4. Open `targetplatform/de.cyface.knime.targetplatform.target` in Eclipse and select `Set as Target Platform` 

During import the Tycho Eclipse providers have to be be installed. If this is not done automatically by Eclipse, perform the following steps: 

1. Goto Eclipse Preferences and browse to `Maven → Discovery → Open Catalog` 
2. Install Tycho Integrations 

### Build 

The following command builds the entire project: 

``` 
mvn clean package 
```

### Test 

The following command builds the entire project, creates an update site, runs unit tests and test workflows: 

``` 
mvn clean verify 
```

Releasing a new version
-----------------------

To release a new version of the Cyface KNIME nodes you should use the Tycho versions plugin. Just call:

    mvn tycho-versions:set-version -DnewVersion=X.X.X-XXX

and substitute X.X.X-XXX with your new version. The project is versioned using semantic versioning. A samll fix on version 1.0.0 thus should result in a new version 1.0.1. Extensions to the current API should result in 1.1.0 and breaking changes to the existing API should result in a new version 2.0.0. Snapshot versions on the dev branch can have the SNAPSHOT keyword attached, like for example 1.0.0-SNAPSHOT while true releases are qualifier free like 1.0.0.

**ATTENTION**: Do not use the maven release plugin. This will not work, since there are some Eclipse configuration files, where you need to change the version as well. You may of course do that by hand, but that is tedious and error prone.

Licensing
---------

Copyright 2018 Cyface GmbH
 
This file is part of the Cyface KNIME Nodes.

The Cyface KNIME Nodes is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
  
The Cyface KNIME Nodes is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with the Cyface KNIME Nodes.  If not, see <http://www.gnu.org/licenses/>.
