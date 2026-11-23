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
BAD = re.compile(r"NoClassDefFoundError|NoSuchMethodError|NoSuchFieldError|LinkageError|ClassNotFoundException|"
                 r"IncompatibleClassChangeError|AbstractMethodError|Plugin to blame: Couchbase")


def newest_log(since):
    logs = [p for p in glob.glob(f"{ROOT}/.intellijPlatform/sandbox/*/*/log_e2eIde263/idea.log") if os.path.getmtime(p) >= since]
    return max(logs, key=os.path.getmtime) if logs else None


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--timeout", type=int, default=240)
    timeout = ap.parse_args().timeout
    # The sandbox keeps idea.log between runs; start from a clean one so old errors (or an old "ready" line) never count.
    for stale in glob.glob(f"{ROOT}/.intellijPlatform/sandbox/*/*/log_e2eIde263/idea.log*"):
        os.remove(stale)
    started = time.time()
    proc = subprocess.Popen(["./gradlew", "e2eIde263"], cwd=ROOT, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                            text=True, start_new_session=True)
    ready = False
    try:
        while time.time() - started < timeout and proc.poll() is None:
            log = newest_log(started)
            if log and READY in open(log, errors="replace").read():
                ready = True
                time.sleep(15)  # let background start-up work (listeners, tool downloads) run and fail if it will
                break
            time.sleep(2)
    finally:
        if proc.poll() is None:
            os.killpg(proc.pid, signal.SIGTERM)
            try:
                proc.wait(30)
            except subprocess.TimeoutExpired:
                os.killpg(proc.pid, signal.SIGKILL)
    log = newest_log(started)
    if not log:
        print("FAIL: the IDE produced no log"); return 1
    lines = open(log, errors="replace").read().splitlines()
    problems = [f"{i + 1}: {l[:240]}" for i, l in enumerate(lines)
                if (" SEVERE " in l or " ERROR " in l or BAD.search(l)) and "Couchbase" in "\n".join(lines[max(0, i - 2):i + 12])]
    problems += [f"{i + 1}: {l[:240]}" for i, l in enumerate(lines) if BAD.search(l) and "com.couchbase" in l]
    problems = sorted(set(problems), key=lambda s: int(s.split(":")[0]))
    if not ready:
        print(f"FAIL: the Couchbase window was never built ('{READY}' not in {log})")
    if problems:
        print("FAIL: plugin errors in the IDE log:"); print("\n".join(problems[:30]))
    if ready and not problems:
        print("PASS: Couchbase window built on IntelliJ IDEA 2026.3 with no plugin errors"); return 0
    return 1


sys.exit(main())
