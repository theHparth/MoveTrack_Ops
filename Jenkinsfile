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
                sh 'docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:latest image --exit-code 1 --severity HIGH,CRITICAL shipment-ingestion'
            }
        }
        stage('ZAP Baseline Scan') {
            steps {
                sh 'docker run --rm --network movetrack_ops_default -v movetrack_ops_jenkins_home:/zap/wrk/:rw -t zaproxy/zap-stable zap-baseline.py -t http://dashboard:80 -r zap-report.html || true'
                sh 'cp /var/jenkins_home/zap-report.html "$WORKSPACE"/zap-report.html || true'
            }
        }
    }
}