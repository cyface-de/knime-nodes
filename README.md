Cyface KNIME Nodes
==================

Status: [![Run Status](https://api.shippable.com/projects/5b22529108daf207007b4520/badge?branch=master)](https://app.shippable.com/github/cyface-de/knime-nodes) 

These are the KNIME nodes developed and used by the Cyface GmbH. We do provide them without any warranty, but we are happy to share our experience ond get input from the community. If you got an improvement or found a bug please let us know.

Some of the Nodes are work in progress and will not work directly. Please see the following list for a more detailed explanation about the KNIME nodes included.

- **ESRI Shapefile Nodes:** This is a fork of the [Shapefile Extension Nodes](https://www.knime.com/shapefile-extension), with a small bugfix. Without that fix, the nodes did not work for us. Since these nodes seem to be abandoned we rehost them here and might provide further updates as we see fit.
- **Distance:** A node to convert a signal with non equal spaced data points to a signal with equal spacing.
- **Envelope:** A node to calculate the envelope curve to an input data signal.
- **Export:** A node to export data to the Cyface binary format.
- **Map Matching (WIP):** A node to map match a table of coordinates. This is still work in progress and does not work yet.
- **Smooting:** A node for smoothing a data signal.

Releasing a new version
-----------------------

To release a new version of the Cyface KNIME nodes you should use the Tycho versions plugin. Just call:

    mvn tycho-versions:set-version -DnewVersion=X.X.X-XXX

and substitute X.X.X-XXX with your new version. The project is versioned using semantic versioning. A samll fix on version 1.0.0 thus should result in a new version 1.0.1. Extensions to the current API should result in 1.1.0 and breaking changes to the existing API should result in a new version 2.0.0. Snapshot versions on the dev branch can have the SNAPSHOT keyword attached, like for example 1.0.0-SNAPSHOT while true releases are qualifier free like 1.0.0.

**ATTENTION**: Do not use the maven release plugin. This will not work, since there are some Eclipse configuration files, where you need to change the version as well. You may of course do that by hand, but that is tedious and error prone.
