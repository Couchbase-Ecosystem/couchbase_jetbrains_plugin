#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."

if ! command -v xvfb-run >/dev/null 2>&1; then
    echo "Background UI tests require xvfb-run (on Ubuntu: sudo apt-get install xvfb)." >&2
    exit 1
fi

# JBR's Robot can use the desktop capture portal if it inherits a Wayland session,
# even when DISPLAY points at Xvfb. Keep both input and screenshots on virtual X11.
exec env -u WAYLAND_DISPLAY XDG_SESSION_TYPE=x11 GDK_BACKEND=x11 \
    xvfb-run -a -s '-screen 0 1600x1000x24' bash tools/gradle-with-retry.sh uiTest "$@"
