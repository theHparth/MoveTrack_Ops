import json

import requests

COMPLIANCE_URL = "http://compliance-service:8010/findings"


def main():
    with open("trivy-report.json") as f:
        report = json.load(f)

    count = 0
    for result in report.get("Results", []):
        target = result.get("Target", "unknown")
        for vuln in result.get("Vulnerabilities") or []:
            finding = {
                "source": "trivy",
                "severity": vuln.get("Severity", "UNKNOWN"),
                "title": vuln.get("VulnerabilityID", "unknown"),
                "description": vuln.get("Title", ""),
                "target": target,
            }
            requests.post(COMPLIANCE_URL, json=finding, timeout=5)
            count += 1

    print(f"reported {count} findings")


if __name__ == "__main__":
    main()