#!/usr/bin/env python3
"""Allocate real X11 and Wayland windows on isolated displays, without desktop input."""
import os
from pathlib import Path
import shutil
import signal
import subprocess
import sys
import tempfile
import time
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent.parent


def keep_results(destination):
    shutil.copytree(ROOT / "build/reports/tests/criticalGuiSmokeTest", destination, dirs_exist_ok=True)
    shutil.copytree(ROOT / "build/test-results/criticalGuiSmokeTest", destination / "junit", dirs_exist_ok=True)
    suites = [ET.parse(path).getroot() for path in (destination / "junit").glob("TEST-*.xml")]
    if (not sum(int(suite.get("tests", "0")) for suite in suites)
            or any(int(suite.get(field, "0")) for suite in suites for field in ("skipped", "failures", "errors"))):
        raise RuntimeError(f"Critical GUI tests must complete without failures or skips: {destination}")


def run():
    os.chdir(ROOT)
    env = os.environ.copy()
    if not shutil.which("xvfb-run") or not shutil.which("weston"):
        raise SystemExit("Install xvfb and weston first (Ubuntu: sudo apt-get install xvfb weston).")
    reports = ROOT / "build/reports/critical-gui"
    reports.mkdir(parents=True, exist_ok=True)
    x11 = env.copy()
    x11.pop("WAYLAND_DISPLAY", None)
    x11.update(XDG_SESSION_TYPE="x11", GDK_BACKEND="x11")
    subprocess.run(["xvfb-run", "-a", "-s", "-screen 0 1600x1000x24", "bash", "tools/gradle-with-retry.sh",
                    "criticalGuiSmokeTest", "-PguiToolkit=XToolkit", *sys.argv[1:]], env=x11, check=True)
    keep_results(reports / "x11")
    with tempfile.TemporaryDirectory(prefix="cb-gui-wayland-") as runtime:
        os.chmod(runtime, 0o700)
        wayland = env.copy()
        wayland.pop("DISPLAY", None)
        wayland.update(XDG_RUNTIME_DIR=runtime, WAYLAND_DISPLAY="wayland-critical",
                       XDG_SESSION_TYPE="wayland", GDK_BACKEND="wayland")
        config = Path(runtime) / "weston.ini"
        shell_config = "[shell]\nlocking=false\npanel-position=none\nbackground-color=0xff202020\n"
        # Also allow Weston extracted under a local prefix, without installing it.
        shell_client = Path(shutil.which("weston")).resolve().parent.parent / "libexec/weston-desktop-shell"
        if shell_client.exists():
            shell_config += f"client={shell_client}\n"
        shell_config += "[input-method]\npath=\n"
        config.write_text(shell_config)
        with (reports / "weston.log").open("w") as log:
            # The X11 backend supplies a complete private seat (keyboard/pointer),
            # unlike a headless backend's synthetic seat on some Weston versions.
            # Only Weston receives the Xvfb DISPLAY. JBR uses native Wayland peers.
            compositor = subprocess.Popen(["xvfb-run", "-a", "-s", "-screen 0 1600x1000x24",
                                           "weston", "--backend=x11-backend.so", "--shell=desktop-shell.so",
                                           "--use-pixman", "--width=1600", "--height=1000", "--idle-time=0",
                                           f"--config={config}", "--socket=wayland-critical"], env=wayland,
                                          stdout=log, stderr=subprocess.STDOUT, start_new_session=True)
            try:
                deadline = time.monotonic() + 10
                while not (Path(runtime) / "wayland-critical").exists():
                    if compositor.poll() is not None or time.monotonic() > deadline:
                        raise RuntimeError(f"Weston failed to start. See {reports / 'weston.log'}")
                    time.sleep(0.05)
                subprocess.run(["bash", "tools/gradle-with-retry.sh", "criticalGuiSmokeTest", "-PguiToolkit=WLToolkit", *sys.argv[1:]],
                               env=wayland, check=True)
                keep_results(reports / "wayland")
                if compositor.poll() is not None:
                    raise RuntimeError(f"Weston exited unexpectedly: {compositor.returncode}")
            finally:
                try:
                    os.killpg(compositor.pid, signal.SIGTERM)
                except ProcessLookupError:
                    pass
                try:
                    compositor.wait(timeout=5)
                except subprocess.TimeoutExpired:
                    os.killpg(compositor.pid, signal.SIGKILL)
                    compositor.wait()


if __name__ == "__main__":
    run()
