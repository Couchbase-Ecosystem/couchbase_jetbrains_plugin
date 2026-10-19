# UI tests

The `uiTest` task opens the plugin's real Swing dialogs in an IntelliJ test
application. AssertJ Swing drives native mouse and keyboard input. These tests
exercise dialog behavior, asynchronous search, and document editor integration;
they do not launch a complete IDE or test tool window registration and navigation.
The existing tests in `src/test` continue to run with `./gradlew test`.

## Run locally

Use Java 21 and a graphical desktop. Start Docker, then run:

```sh
./gradlew uiTest
```

On Linux, use Xvfb to avoid moving the mouse or opening dialogs on your desktop:

```sh
bash tools/run-ui-tests.sh
```

Install Xvfb first if necessary (`sudo apt-get install xvfb` on Ubuntu). The script
also clears inherited Wayland settings so Java's Robot uses the virtual display
for screenshots and does not request access to your desktop through a sharing portal.

The Docker tests use Testcontainers to start a temporary
`couchbase/server:enterprise-8.0.2` instance with a dedicated `ui_tests` bucket.
They use dynamically assigned ports and test-only credentials, and disconnect
the SDK and remove the container after the class finishes. No manually configured
cluster or Capella credentials are needed. The first run may need to download the
IDE, dependencies, and Docker image.

Run only the tests that do not need Docker:

```sh
bash tools/run-ui-tests.sh -PuiTestsWithoutDocker=true
```

Run a single class or method using Gradle's usual filter:

```sh
bash tools/run-ui-tests.sh --tests '*DocumentsCouchbaseUiTest'
```

## Coverage

| Test class | Behavior |
| --- | --- |
| `ConnectionDialogUiTest` | Required fields on Save and Test Connection, retry after validation, automatic SSL selection, Cancel |
| `CollectionSearchDialogUiTest` | Lazy loading on first typing, one fetch while editing, debounced filtering, keyboard selection, no matches, clearing and retrying, Escape cancellation |
| `DocumentAndCollectionDialogUiTest` | Empty and oversized document IDs, invalid collection names, retrying validation |
| `DocumentsCouchbaseUiTest` | Missing document errors, opening an existing document, creating and opening a new document, preserving existing content, searching actual server collections |
| `QueryExecutionUiTest` | Execute dispatches SDK work off the EDT; UI timer events continue while a query is pending; the production Cancel action cancels the SDK future and restores Execute |
| `CertificateCouchbaseUiTest` | Password/certificate UI switching, failed keystore password and retry, live certificate SDK and explorer REST authentication |
| `CriticalGuiSmokeTest` | Native window allocation for New Connection, certificate mode, advanced settings, open/create document, create collection, collection search, explorer context menu, and the SQL++ editor |

All tests run sequentially because they share a mouse and keyboard. Dialogs are
closed after each test. The database suite restores the prior active cluster and
closes document editors after each test.

The query responsiveness test holds an SDK request pending, checks that UI timer
events keep running, and clicks Cancel to verify cancellation. Document tests save
through the actual editor, preserve UTF-8 and expiration, reject stale CAS writes,
and exercise conflict cancellation and explicit overwrite.
They also cover reload from the server, deleted-document recreation, invalid JSON,
read-only connections, switching connections before saving, and listing `_default`.

To include live client-certificate tests:

```sh
python3 tools/start-certificate-test-cluster.py
bash tools/run-ui-tests.sh -PcertificateDemoTests=true
```

These tests use the separate local certificate demo cluster, exercise the actual
connection dialog (including wrong-password recovery), and verify SDK and explorer
REST operations without database password authentication. Dialog screenshots are
saved as `build/reports/uiTest/artifacts/connection-password.png` and
`connection-certificate.png`. Without the property, these two demo tests are
skipped. The regular Docker document tests still run automatically.

## Native GUI regression gate

On Linux, install `xvfb` and `weston`, then run:

```sh
python3 tools/run-critical-gui-tests.py
```

This opens the same critical windows with both `XToolkit` and `WLToolkit`. It
asserts which toolkit actually loaded and allocates production native window
peers. The Wayland checks use a private Weston compositor hosted on another
private Xvfb display; they do not use the desktop sharing portal or inject input
into your desktop. This catches platform-specific failures such as the AWT
`ScrollPanePeer` cast in New Connection that X11-only tests missed.

The gate rejects skipped tests and a compositor exit. Both backend reports and
JUnit results are retained under `build/reports/critical-gui/{x11,wayland}/`.

The CI workflow runs the Docker/certificate UI suite and both native GUI gates.
These checks cover the central server connection, explorer, query, and document
flows. They do not establish compatibility with every OS, IDE build, optional
Couchbase Lite native library, external CLI, or JCEF chart; those still need
their own integration coverage. The tests use an IntelliJ test application,
with real peers, rather than a complete IDE process.

## Results and failures

- HTML report: `build/reports/tests/uiTest/index.html`
- JUnit XML: `build/test-results/uiTest/`
- Screenshots captured on test failures: `build/reports/uiTest/artifacts/`
- IntelliJ test logs: `build/ui-test-sandbox/` under the UI task's `log` directory

The `UI tests` GitHub Actions workflow runs the suite with Xvfb and Docker and
uploads reports, screenshots, and IDE logs even when tests fail.

The test harness builds on the
[IntelliJ Platform testing extension](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-testing-extension.html)
and [AssertJ Swing fixtures](https://assertj-swing.readthedocs.io/en/latest/assertj-swing-getting-started/).
