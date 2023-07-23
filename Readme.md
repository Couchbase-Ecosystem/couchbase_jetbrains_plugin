# Couchbase Jetbrains Plugin

Welcome to the official Couchbase plugin for the Jetbrains Platform

The Couchbase JetBrains plugin provides support for Couchbase in JetBrains IDEs.

This means that you can use your favorite IDE to develop Couchbase applications, complete with code completion, syntax highlighting, and debugging support.

# Installation
To install the plugin just search for Couchbase inside the plugins market place or go to https://plugins.jetbrains.com/plugin/22131-couchbase

# Quick Tour

### Add Cluster Connection
To connect to a Couchbase Cluster, follow these steps:
1. Click on the "Add" icon.
2. Complete all the required fields with the necessary details.
3. Check "Use Secure Connection" if you are connecting to Capella or if your connection is using HTTPS.
4. Finally, click on the "Connect" button to establish a connection to the cluster.

<img src="src/main/resources/assets/gifs/AddConnection.gif" height="80%" width="80%" alt="Add Connection to Cluster" />

> **_NOTE:_** If you inform the "Default Bucket" field anc click on "Test Connection" we will run the SDK Doctor utility, which helps you to identify potential problems with your connection.

### Connect, Disconnect or Delete a Cluster Connection
Right-click on the connection to open the context menu. From the context menu, you will be able to choose between options to Connect, Disconnect or Delete the Cluster.

<img src="src/main/resources/assets/gifs/ConnectDisconnectCluster.gif" height="80%" width="80%" alt="Connect/Disconnect to Cluster" />

### Interact with Buckets and Scopes
Click on the bucket to see the list of Scopes associated with that bucket. Open the context menu on bucket to undertake actions such as creating a new Scope, refreshing existing Scopes, or obtaining administrative information about the Bucket.

<img src="src/main/resources/assets/gifs/InteractWithBucketsScopes.gif" height="80%" width="80%" alt="Interact with Buckets and Scopes" />

### Interact with Collections and Indexes
Click on Scope to list Collections and Indexes. Open context menu on Collection directory to create a new Collection or to refresh Collections.

<img src="src/main/resources/assets/gifs/InteractWithCollectionIndexes.gif" height="80%" width="80%" alt="Interact with Collections and Indexes" />


### Interact with Documents
1. Click on the desired Collection to list all the documents that have been stored within it.
2. To open a specific document, click on it to view its details and make any necessary changes.
3. If you need to create a new document or search for an existing one, you can open the context menu by right-clicking on the Collection directory.
4. Once you have made any necessary changes to a document, you can save it using the "Ctrl + S" or "Cmd + S" shortcut keys.

<img src="src/main/resources/assets/gifs/InteractWithDocuments.gif" height="80%" width="80%" alt="Interact with Documents" />

> **_NOTE:_** If the document has already changed in the server by the time you modify and save it, we will show a message alerting you of that and asking if you want to keep your version or the server's one.

### Plugin Log
The Couchbase JetBrains plugin offers a convenient logging feature that allows developers to easily log important information, debug messages, and error reports directly from the plugin interface. This feature streamlines the development process and helps developers track and analyze the plugin's behavior effectively.

<img src="src/main/resources/assets/screenshots/Logging.png" height="80%" width="80%" alt="Logging" />

### Exporting the results of a SQL++ query

You can also export the results of a query to a file in either CSV or JSON format. This functionality simplifies data analysis and sharing, allowing developers to easily save query results for further processing or to share data with other team members.

<img src="src/main/resources/assets/screenshots/QueryExport.png" height="80%" width="80%" alt="Exporting the results of a N1QL query" />

### Autocomplete for SQL++ queries
With autocomplete, the Couchbase JetBrains plugin provides suggestions for SQL++ keywords and functions as developers write queries. This intelligent feature enhances coding efficiency by offering relevant options for completing SQL++ statements, helping users write accurate and syntactically correct queries while working with Couchbase databases.

<img src="src/main/resources/assets/screenshots/Autocomplete.png" height="80%" width="80%" alt="Autocomplete for N1QL queries" />

### View Document Metadata

Right-click on a document and access its metadata. The metadata is read-only, and any changes to it won't be persisted. This functionality provides developers with a convenient way to inspect and modify document metadata during the development process without impacting the actual data stored in the Couchbase database.

<img src="src/main/resources/assets/screenshots/DocumentMetadata.png" height="80%" width="80%" alt="View Document Metadata" />

### Connection Color

You can set custom colors for each Connection. This functionality proves beneficial when distinguishing between different environments, such as Production and Development, to prevent accidental changes to critical data. Users can easily pick colors using a color picker, and the chosen color is then displayed as a small line in the tree or workbench, providing a visual indicator of the active connection. If you switch to connect to another cluster that doesn't have a color set, the interface reverts to its default appearance. This feature streamlines development workflows and helps users maintain clarity and organization within the Couchbase plugin.

<!-- 3 images -->
<img src="src/main/resources/assets/screenshots/ConnectionColor1.png" height="80%" width="80%" alt="Connection Color" />

<img src="src/main/resources/assets/screenshots/ConnectionColor2.png" height="80%" width="80%" alt="Connection Color" />

<img src="src/main/resources/assets/screenshots/ConnectionColor3.png" height="80%" width="80%" alt="Connection Color" />

### Cluster Connection Statistics
By right-clicking on an active connection, developers can easily access and analyze statistics from the bucket and cluster, providing valuable insights into the performance and health of their Couchbase databases. This feature empowers users to make informed decisions and optimize their database configurations for better overall efficiency and reliability.

<!-- 4 images: 1 top, 3 side by side -->

<img src="src/main/resources/assets/screenshots/ClusterConnectionOverview.png" height="80%" width="80%" alt="Cluster Connection Statistics" />

<img src="src/main/resources/assets/screenshots/ClusterConnectionStatistics1.png" height="80%" width="80%" alt="Cluster Connection Statistics" />

<img src="src/main/resources/assets/screenshots/ClusterConnectionStatistics2.png" height="80%" width="80%" alt="Cluster Connection Statistics" />

<img src="src/main/resources/assets/screenshots/ClusterConnectionStatistics3.png" height="80%" width="80%" alt="Cluster Connection Statistics" />


### View Index Statistics

The latest enhancement in the Couchbase JetBrains plugin enables users to access statistics for each index effortlessly. By simply right-clicking on a specific index and selecting "View Stats," developers can gain valuable insights into the index's performance and usage. This feature streamlines the monitoring and optimization of indexes, helping users fine-tune their Couchbase database for improved query performance and overall efficiency.

<img src="src/main/resources/assets/screenshots/IndexStatistics1.png" height="80%" width="80%" alt="View Index Statistics" />
<img src="src/main/resources/assets/screenshots/IndexStatistics2.png" height="80%" width="80%" alt="View Index Statistics" />

### Selective Query Execution

With the updated Couchbase JetBrains plugin, users gain the ability to select a specific query for execution when dealing with multiple queries. This functionality proves invaluable when working with complex tasks involving multiple queries, allowing developers to choose and run a particular query with ease. This feature enhances productivity and simplifies the query execution process, ensuring that developers can focus on the specific task at hand without any unnecessary steps or confusion.

<img src="src/main/resources/assets/screenshots/SelectiveQueryExecution.png" height="80%" width="80%" alt="Selective Query Execution" />


### Read-Only Mode
If you are accessing sensible environments and are afraid of changing any data by mistake, the plugin also offers a read-only mode. Once activated, an eye icon appears next to the connection's name, indicating that the plugin is in read-only mode. In this mode, any actions that might modify the server, such as creating documents, adding scopes, collections, indexes, importing data, etc., are hidden to prevent accidental changes to the database.

Furthermore, the plugin performs validation checks, and if a user attempts to run a query with mutation operations while in read-only mode, a notification is triggered to alert the user about the read-only status. This read-only mode provides an additional layer of security and peace of mind for developers, ensuring that sensitive data and critical configurations remain protected from accidental alterations while using the plugin.

<!-- 4 images -->
<img src="src/main/resources/assets/screenshots/ReadOnlyMode1.png" height="80%" width="80%" alt="Read-Only Mode" />
<img src="src/main/resources/assets/screenshots/ReadOnlyMode2.png" height="80%" width="80%" alt="Read-Only Mode" />
<img src="src/main/resources/assets/screenshots/ReadOnlyMode3.png" height="80%" width="80%" alt="Read-Only Mode" />
<img src="src/main/resources/assets/screenshots/ReadOnlyMode4.png" height="80%" width="80%" alt="Read-Only Mode" />

## License
Apache Software License Version 2.  See individual files for details.

## Support
This project is not officially supported by Couchbase, but it is actively maintained by Couchbase Employees. So if you find any bugs/issues or would like to suggest anything, feel free to open an issue here on github.
