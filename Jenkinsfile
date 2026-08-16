pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh 'docker build -t shipment-ingestion services/shipment-ingestion'
            }
        }
        stage('Trivy Scan') {
            steps {
                sh 'docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v movetrack_ops_jenkins_home:/report aquasec/trivy:latest image -f json -o /report/trivy-report.json --exit-code 1 --severity HIGH,CRITICAL shipment-ingestion || echo "SEVERE FINDINGS DETECTED"'
                sh 'docker cp $(docker ps -aq -f name=jenkins):/var/jenkins_home/trivy-report.json "$WORKSPACE"/trivy-report.json || true'
            }
        }
        stage('Report Findings') {
            steps {
                sh 'pip install requests --quiet --break-system-packages || pip install requests --quiet'
                sh 'python3 scripts/report_findings.py || true'
            }
        }
        stage('ZAP Baseline Scan') {
            steps {
                sh 'docker run --rm --network movetrack_ops_default -v movetrack_ops_jenkins_home:/zap/wrk/:rw -t zaproxy/zap-stable zap-baseline.py -t http://dashboard:80 -r zap-report.html || true'
                sh 'cp /var/jenkins_home/zap-report.html "$WORKSPACE"/zap-report.html || true'
            }
        }
        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: '*-report.html', allowEmptyArchive: true
            }
        }
    }
}