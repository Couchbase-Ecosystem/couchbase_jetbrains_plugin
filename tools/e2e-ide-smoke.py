#!/usr/bin/env python3
"""End-to-end smoke test: run the packaged plugin inside IntelliJ IDEA 2026.3 and check it works.

Opens tools/e2e/fixture-project with the Couchbase tool window showing, waits for the window to be built, shuts the
IDE down and fails if the IDE log holds any error raised by the plugin. Unit tests cannot catch this class of failure
(e.g. a library the IDE stopped providing), because they run with the plugin's libraries on the test classpath.

Usage: tools/e2e-ide-smoke.py [--timeout SECONDS]
"""
import argparse, glob, os, re, signal, subprocess, sys, time

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
READY = "Couchbase explorer window created"
# An idea.log entry starts with a timestamp; the lines up to the next one (stack trace, "Plugin to blame") belong to it.
ENTRY = re.compile(r"^\d{4}-\d{2}-\d{2} ")
OURS = re.compile(r"com\.couchbase\.|plugin to blame:\s*couchbase", re.IGNORECASE)
GRADLE_LOG = os.path.join(ROOT, "build", "e2e-ide-smoke-gradle.log")
BAD = re.compile(r"NoClassDefFoundError|NoSuchMethodError|NoSuchFieldError|LinkageError|ClassNotFoundException|"
                 r"IncompatibleClassChangeError|AbstractMethodError|Plugin to blame: Couchbase")


def newest_log(since):
    logs = [p for p in glob.glob(f"{ROOT}/.intellijPlatform/sandbox/*/*/log_e2eIde263/idea.log") if os.path.getmtime(p) >= since]
    return max(logs, key=os.path.getmtime) if logs else None


def entry(lines, i):
    """The log entry that line i belongs to: back to the entry's timestamp line, forward to the next entry."""
    start = i
    while start > 0 and not ENTRY.match(lines[start]):
        start -= 1
    end = i + 1
    while end < len(lines) and not ENTRY.match(lines[end]):
        end += 1
    return "\n".join(lines[start:end])


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--timeout", type=int, default=240)
    timeout = ap.parse_args().timeout
    # The sandbox keeps idea.log between runs; start from a clean one so old errors (or an old "ready" line) never count.
    for stale in glob.glob(f"{ROOT}/.intellijPlatform/sandbox/*/*/log_e2eIde263/idea.log*"):
        os.remove(stale)
    started = time.time()
    # Gradle's output goes to a file: a pipe nobody reads fills up and blocks Gradle (and the IDE with it).
    os.makedirs(os.path.dirname(GRADLE_LOG), exist_ok=True)
    gradle_out = open(GRADLE_LOG, "w")
    proc = subprocess.Popen(["./gradlew", "e2eIde263"], cwd=ROOT, stdout=gradle_out, stderr=subprocess.STDOUT,
                            start_new_session=True)
    ready = alive = False
    try:
        while time.time() - started < timeout and proc.poll() is None:
            log = newest_log(started)
            if log and READY in open(log, errors="replace").read():
                ready = True
                time.sleep(15)  # let background start-up work (listeners, tool downloads) run and fail if it will
                alive = proc.poll() is None  # an IDE that died during those 15s did not pass
                break
            time.sleep(2)
    finally:
        if proc.poll() is None:
            os.killpg(proc.pid, signal.SIGTERM)
            try:
                proc.wait(30)
            except subprocess.TimeoutExpired:
                os.killpg(proc.pid, signal.SIGKILL)
        gradle_out.close()
    log = newest_log(started)
    if not log:
        print("FAIL: the IDE produced no log"); return 1
    lines = open(log, errors="replace").read().splitlines()
    problems = [f"{i + 1}: {l[:240]}" for i, l in enumerate(lines)
                if (" SEVERE " in l or " ERROR " in l or BAD.search(l)) and OURS.search(entry(lines, i))]
    problems = sorted(set(problems), key=lambda s: int(s.split(":")[0]))
    if not ready:
        print(f"FAIL: the Couchbase window was never built ('{READY}' not in {log})")
    elif not alive:
        print(f"FAIL: the IDE exited during the start-up wait (Gradle output: {GRADLE_LOG})")
    if problems:
        print("FAIL: plugin errors in the IDE log:"); print("\n".join(problems[:30]))
    if ready and alive and not problems:
        print("PASS: Couchbase window built on IntelliJ IDEA 2026.3 with no plugin errors"); return 0
    return 1


sys.exit(main())
