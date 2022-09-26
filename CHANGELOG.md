# Changelog

This is the changelog for the Cyface Nodes Project.
It gives an overview of the most prominent changes in each release.
Minor fixes, changes “under the hood” and refactoring changes are not listed here.
We follow the [Semantic Versioning](https://semver.org/spec/v2.0.0.html) scheme and the guidelines from ["Keep a Changelog"](https://keepachangelog.com/en/1.0.0/) as close as possible.

The Cyface Nodes are open sourced under GPLv3.
The source code is available on [Github](https://github.com/cyface-de/knime-nodes).

## [2.5.2] - 2022-09-26
### Changed
* Move nodes to the NodePit category
* Rename nodes to Cyface Nodes”

## [2.5.1] - 2021-03-12
### Changed
* Update build and deployment pipelines

## [2.5.0] - 2019-10-21
### Added
* Add events binary format support to binary reader

## [2.4.1] - 2019-09-06
### Fixed
* Smoothing node now creates a valid output also for small inputs

### Changed
* Feature description was extended

## [2.4.0] - 2019-06-25
### Info
* Background changes

## [2.3.1] - 2019-05-24
### Fixed
* Accuracy from accelerations was not read correctly

## [2.3.0] - 2019-04-15
### Added
* Binary format reader is capable of reading individual sensor files now


## [2.2.0] - 2019-04-10
### Changed
* All nodes are cancelable now and report progress correctly

### Added
* Reader node for the Cyface binary format


## [2.1.3] - 2018-09-14
### Fixed
* Logo was not appearing correctly


## [2.1.2] - 2018-09-14
### Fixed
* Tests have been fixed


## [2.1.1] - 2018-09-14
### Fixed
* Moved logo to correct folder

### Added
* Additional tests

## [2.1.0] - 2018-09-14
### Changed
* Cyface logo updated to current version


## [2.0.0] - 2018-08-15
### Fixed
* Updated for KNIME 3.6

## [1.4.1] - 2018-07-10
### Added
* Test code and workflows


## [1.4.0] - 2018-07-06
### Removed
* Shapefile nodes from the project. They are not from us and thus removed from the project. We might provide the fixed version in the future

### Fixed
* Missing value are correctly handled by envelope node now

### Added
* Input data is now pushed through to output ports


## [1.3.1] - 2018-06-29
### Fixed
* Naming in manifest file


## [1.3.0] - 2018-06-29
### Added
* Explanations on how to build to readme file
* Icons for all nodes

### Removed
* Unusable Map-Matching node from project. This might become readded in a future release


## [1.2.1] - 2018-06-27
### Fixed
* Changing the selection of append and replace in the smoothing node does work now

### Added
* Explanations on how to release new version of the Cyface Nodes


## [1.2.0] - 2018-06-24
### Info
* Initial release. Previous version where internal
