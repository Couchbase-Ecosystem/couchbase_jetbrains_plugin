# How to Generate the "tools.zip" file

* In your 7.2 couchbase installation, copy the folder *bin* and *lib* and past them somewhere
* Remove the following files from the bin folder:
  * cbimport
  * cbexport
  * prometheus
  * backupmgr
  * cbdocloader
  * cbft
  * saslauthd-port
  * cbq-engine
  * indexer
  * cbdocloader
  * cbriftdump
  * cbft
  * cbft-bleve
  * cbindex
  * projector
  * memcached
  * cbas
  * forest_dbdump
  * couch_view*
  * goxdcr
* Remove the following files from the lib folder:
  * cbas
  * fts
* After that, select both the bin and lib folders and add it to a zip.
  * Make sure that when you unzip the file, 2 folders are extracted: "bin" and "lib". (it should not have a parent folder)

