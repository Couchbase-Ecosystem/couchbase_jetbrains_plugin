"""Check that the CI workaround recovers indexing failures without hiding test failures."""
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest


class GradleRetryTest(unittest.TestCase):
    def run_fixture(self, first_output, first_status, subsequent_status=0, repeat_output=False):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "tools").mkdir()
            shutil.copyfile(Path(__file__).with_name("gradle-with-retry.sh"), root / "tools/retry.sh")
            index = root / ".intellijPlatform/layoutIndex/platform.json"
            index.parent.mkdir(parents=True)
            index.write_text("{}")
            (root / "first-output").write_text(first_output)
            gradle = root / "gradlew"
            gradle.write_text(f'''#!/usr/bin/env bash
echo "$*" >> calls
if [[ $(wc -l < calls) == 1 ]]; then
    cat first-output
    exit {first_status}
fi
{'cat first-output' if repeat_output else ''}
exit {subsequent_status}
''')
            gradle.chmod(0o755)
            result = subprocess.run(["bash", "tools/retry.sh", "test", "--console=plain"],
                                    cwd=root, capture_output=True, text=True)
            return result.returncode, (root / "calls").read_text().splitlines(), index.exists()

    INDEX_FAILURE = """java.nio.file.ClosedFileSystemException
Could not determine the dependencies of task ':compileJava'.
Could not find bundled plugin with ID: 'com.intellij.java'.
"""

    def test_recovers_indexing_failure_with_a_fresh_process_and_index(self):
        status, calls, index_exists = self.run_fixture(self.INDEX_FAILURE, 1)
        self.assertEqual(status, 0)
        self.assertEqual(calls, ["--no-daemon test --console=plain"] * 2)
        self.assertFalse(index_exists)

    def test_does_not_retry_a_test_failure(self):
        status, calls, index_exists = self.run_fixture("SomeTest > test FAILED", 1)
        self.assertEqual(status, 1)
        self.assertEqual(len(calls), 1)
        self.assertTrue(index_exists)

    def test_does_not_retry_a_deterministic_missing_dependency(self):
        status, calls, index_exists = self.run_fixture(
            "Could not find bundled plugin with ID: 'com.intellij.java'.", 1)
        self.assertEqual(status, 1)
        self.assertEqual(len(calls), 1)
        self.assertTrue(index_exists)

    def test_preserves_failure_status_when_retry_fails(self):
        status, calls, _ = self.run_fixture(self.INDEX_FAILURE, 1, subsequent_status=7)
        self.assertEqual(status, 7)
        self.assertEqual(len(calls), 2)

    def test_stops_after_three_indexing_failures(self):
        status, calls, _ = self.run_fixture(self.INDEX_FAILURE, 1, subsequent_status=1, repeat_output=True)
        self.assertEqual(status, 1)
        self.assertEqual(len(calls), 3)


if __name__ == "__main__":
    unittest.main()
