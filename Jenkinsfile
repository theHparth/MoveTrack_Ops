pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Hello') {
            steps {
                sh 'echo Jenkins can run a pipeline from this repo'
            }
        }
    }
}