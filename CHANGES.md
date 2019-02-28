# jHDF Change Log

Please note this project is still in pre-release development.

## v0.3.2
- Fix bug when fixed size string datasets contain strings of exactly that size.
- Fix bug where >1D fixed size datasets could not be read
- Add more JavaDoc
- Minor refactoring

## v0.3.1
- Add support for String datasets
- Remove Dataset.getDataBuffer - Not all datasets can reasonably support accessing the backing buffer
- Dataset.getMaxSize now always returns a result previously returned Optional if no max size was in the file now it returns the dataset size if no max size is present.
- Remove dependency on org.slf4j.slf4j-simple, now just depends on slf4j-api
- Update SLF4J to 1.8.0-beta4
- Update to Gradle 5.2.1 and Gradle plugins

## v0.3.0
- First release to support reading chunked datasets. (note: v1.8 files only)
- Initial support for compressed datasets, GZIP only at the moment.

## Pre 0.3.0
Lots of initial development towards being able to read HDF5 files in pure Java. See Git history if your interested.