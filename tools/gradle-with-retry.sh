#!/usr/bin/env bash
set -uo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."
log_file=$(mktemp)
trap 'rm -f "$log_file"' EXIT

for attempt in 1 2 3; do
    # A fresh process is necessary: the failed descriptor scan is cached in memory.
    ./gradlew --no-daemon "$@" 2>&1 | tee "$log_file"
    result=${PIPESTATUS[0]}
    if (( result == 0 )); then
        exit 0
    fi

    # JetBrains/intellij-platform-gradle-plugin#2192: a closed JAR filesystem can
    # leave an incomplete bundled-plugin index. Never retry test/compiler failures.
    if (( attempt == 3 )) \
        || ! grep -q 'java.nio.file.ClosedFileSystemException' "$log_file" \
        || ! grep -q "Could not find bundled plugin with ID: 'com.intellij.java'" "$log_file" \
        || ! grep -q "Could not determine the dependencies of task ':compileJava'" "$log_file"; then
        exit "$result"
    fi

    echo "Retrying IntelliJ bundled-plugin indexing after a closed JAR filesystem (attempt $attempt/3)." >&2
    if [[ -d .intellijPlatform ]]; then
        find .intellijPlatform -type f -path '*/layoutIndex/*.json' -delete
    fi
done
