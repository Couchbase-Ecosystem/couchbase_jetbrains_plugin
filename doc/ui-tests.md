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
| `CollectionSearchDialogUiTest` | Debounced filtering, keyboard selection, no matches, clearing and retrying, Escape cancellation |
| `DocumentAndCollectionDialogUiTest` | Empty and oversized document IDs, invalid collection names, retrying validation |
| `DocumentsCouchbaseUiTest` | Missing document errors, opening an existing document, creating and opening a new document, preserving existing content, searching actual server collections |

All tests run sequentially because they share a mouse and keyboard. Dialogs are
closed after each test. The database suite restores the prior active cluster and
closes document editors after each test.

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
