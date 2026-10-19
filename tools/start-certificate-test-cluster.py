#!/usr/bin/env python3
"""Start an isolated, persistent local Couchbase cluster for certificate-auth testing.

Uses only loopback ports. Leaves existing containers and their data intact.
Generated test keys stay in ignored build/certificate-demo (permissions 0700).
"""
import base64
import json
import os
from pathlib import Path
import subprocess
import time
import urllib.request

ROOT = Path(__file__).resolve().parents[1]
CERTS = ROOT / "build/certificate-demo"
CONTAINER = "couchbase-plugin-certificate"
ADMIN = "Administrator:password"
CERTS.mkdir(parents=True, exist_ok=True)
CERTS.chmod(0o700)


def run(*args):
    result = subprocess.run(args, text=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    if result.returncode:
        raise RuntimeError(f"{args[0]} failed: {result.stderr or result.stdout}")
    return result.stdout


def request(path, data=None, method=None, content_type="application/json"):
    headers = {"Authorization": "Basic " + base64.b64encode(ADMIN.encode()).decode(), "Content-Type": content_type}
    req = urllib.request.Request("http://127.0.0.1:18080" + path,
                                 data.encode() if data is not None else None, headers, method=method)
    with urllib.request.urlopen(req, timeout=15) as response:
        return response.read().decode()


def main():
    # Reuse this helper's container on subsequent runs; never replace someone else's container.
    existing = subprocess.run(["docker", "inspect", CONTAINER], text=True, stdout=subprocess.PIPE, stderr=subprocess.DEVNULL)
    if existing.returncode == 0:
        info = json.loads(existing.stdout)[0]
        if info["Config"].get("Labels", {}).get("com.couchbase.intellij.demo") != "certificate":
            raise RuntimeError("A container with this name already exists and was not created by this helper.")
        run("docker", "start", CONTAINER)
    else:
        run("docker", "run", "-d", "--name", CONTAINER, "--label", "com.couchbase.intellij.demo=certificate",
            "-p", "127.0.0.1:18080:8091", "-p", "127.0.0.1:18091-18096:18091-18096",
            "-p", "127.0.0.1:11207:11207", "-v", "couchbase-plugin-certificate-data:/opt/couchbase/var",
            "couchbase/server:enterprise-8.0.2")
    print("Waiting for certificate test cluster...", flush=True)
    for _ in range(90):
        try:
            request("/pools")
            break
        except Exception:
            time.sleep(1)
    else:
        raise RuntimeError("Couchbase did not start within 90 seconds.")
    pools = json.loads(request("/pools"))
    if not pools.get("pools"):
        run("docker", "exec", CONTAINER, "/opt/couchbase/bin/couchbase-cli", "node-init", "-c", "127.0.0.1",
            "--node-init-hostname", "127.0.0.1")
        run("docker", "exec", CONTAINER, "/opt/couchbase/bin/couchbase-cli", "cluster-init", "-c", "127.0.0.1",
            "--cluster-username", "Administrator", "--cluster-password", "password", "--services", "data,index,query",
            "--cluster-ramsize", "1024", "--cluster-index-ramsize", "512", "--index-storage-setting", "default")
    if not (CERTS / ".fixture-version-2").exists():
        run("openssl", "req", "-x509", "-newkey", "rsa:2048", "-nodes", "-sha256", "-days", "30",
            "-keyout", str(CERTS / "ca.key"), "-out", str(CERTS / "ca.pem"), "-subj", "/CN=Couchbase Plugin Test CA",
            "-addext", "basicConstraints=critical,CA:TRUE", "-addext", "keyUsage=critical,keyCertSign,cRLSign")
        for name, subject, usage in [("server", "localhost", "serverAuth,clientAuth"), ("client", "certificate_user", "clientAuth")]:
            run("openssl", "req", "-new", "-newkey", "rsa:2048", "-nodes", "-keyout", str(CERTS / f"{name}.key"),
                "-out", str(CERTS / f"{name}.csr"), "-subj", f"/CN={subject}")
            extensions = "basicConstraints=CA:FALSE\nsubjectKeyIdentifier=hash\nauthorityKeyIdentifier=keyid,issuer:always\nkeyUsage=digitalSignature,keyEncipherment\nextendedKeyUsage=" + usage + "\n"
            if name == "server":
                extensions += "subjectAltName=DNS:localhost,IP:127.0.0.1\n"
            (CERTS / f"{name}.ext").write_text(extensions)
            run("openssl", "x509", "-req", "-sha256", "-days", "30", "-CA", str(CERTS / "ca.pem"),
                "-CAkey", str(CERTS / "ca.key"), "-CAcreateserial", "-in", str(CERTS / f"{name}.csr"),
                "-out", str(CERTS / f"{name}.pem"), "-extfile", str(CERTS / f"{name}.ext"))
        run("openssl", "pkcs12", "-export", "-name", "certificate_user", "-in", str(CERTS / "client.pem"),
            "-inkey", str(CERTS / "client.key"), "-certfile", str(CERTS / "ca.pem"), "-out", str(CERTS / "client.p12"),
            "-passout", "pass:changeit")
        (CERTS / ".fixture-version-2").touch()
        for path in CERTS.iterdir():
            path.chmod(0o600)
    print("Configuring trusted CA and client-certificate authentication...", flush=True)
    inbox = "/opt/couchbase/var/lib/couchbase/inbox"
    run("docker", "exec", CONTAINER, "mkdir", "-p", inbox + "/CA")
    for source, target in [("ca.pem", "CA/ca.pem"), ("server.pem", "chain.pem"), ("server.key", "pkey.key")]:
        run("docker", "cp", str(CERTS / source), CONTAINER + ":" + inbox + "/" + target)
    run("docker", "exec", CONTAINER, "chown", "-R", "couchbase:couchbase", inbox)
    # CA loading must originate on the node, rather than through Docker port forwarding.
    run("docker", "exec", CONTAINER, "curl", "--fail", "--silent", "-u", ADMIN, "-X", "POST",
        "http://127.0.0.1:8091/node/controller/loadTrustedCAs")
    request("/node/controller/reloadCertificate", "", "POST")
    request("/settings/rbac/users/local/certificate_user", "roles=admin&password=unused-local-test-password", "PUT", "application/x-www-form-urlencoded")
    request("/settings/clientCertAuth", json.dumps({"state": "enable", "prefixes": [{"path": "subject.cn", "prefix": "", "delimiter": ""}]}), "POST")
    buckets = json.loads(request("/pools/default/buckets"))
    if not any(bucket["name"] == "certificate_test" for bucket in buckets):
        request("/pools/default/buckets", "name=certificate_test&ramQuotaMB=256&replicaNumber=0&bucketType=couchbase", "POST", "application/x-www-form-urlencoded")
    print("Ready: couchbases://localhost | bucket: certificate_test")
    print("Client keystore:", CERTS / "client.p12")
    print("Keystore password: changeit (local test fixture only)")
    print("Server CA:", CERTS / "ca.pem")


if __name__ == "__main__":
    main()
