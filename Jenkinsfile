pipeline {
    agent any

    environment {
        VERSION = readMavenPom().getVersion()
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '5'))
    }

    stages {
        // normal build if it's not the master branch and not the support branch, except if it's a SNAPSHOT-version
        stage('Build-SNAPSHOT') {
            when {
                not { branch 'master' }
                not { 
                    allOf {
                        branch 'support/*'
                        expression { return !VERSION.endsWith("-SNAPSHOT") }
                    }
                }
            }
            steps {
                withMaven(
                    // Maven installation declared in the Jenkins "Global Tool Configuration"
                    maven: 'Maven3',
                    // Maven settings.xml file defined with the Jenkins Config File Provider Plugin
                    // Maven settings and global settings can also be defined in Jenkins Global Tools Configuration
                    mavenSettingsConfig: '2529f595-4ac5-44c6-8b4f-f79b5c3f4bae'
                ) {

                    echo "Project version: $VERSION"

                    // Run the maven build
                    sh 'mvn clean deploy -PrequireSnapshotVersion,docker,docker-$GIT_BRANCH -Dmaven.test.failure.ignore=true'

                } // withMaven will discover the generated Maven artifacts, JUnit Surefire & FailSafe & FindBugs reports...
            }
        }
        // release build if it's the master or the support branch and is not a SNAPSHOT version
        stage ('Build-Release') {
            when {
                anyOf { branch 'master'; branch 'support/*' }
                expression { return !VERSION.endsWith("-SNAPSHOT") }
            }
            steps {
                withMaven(
                    maven: 'Maven3',
                    mavenSettingsConfig: '2529f595-4ac5-44c6-8b4f-f79b5c3f4bae'
                ) {
                    echo "Release: $VERSION"
                    // check license
                    // check is release version
                    // deploy to distribution
                    // send release email
                    sh 'mvn clean deploy -Pdocker,release'
                }
            }
        }
        stage ('SonarQube Analysis'){
            steps {
                withMaven(
                    maven: 'Maven3',
                    mavenSettingsConfig: '2529f595-4ac5-44c6-8b4f-f79b5c3f4bae'
                ) {
                    withSonarQubeEnv('Wemove SonarQube') {
                        sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.4.0.905:sonar'
                    }
                }
            }
        }
    }
    post {
        changed {
            // send Email with Jenkins' default configuration
            script { 
                emailext (
                    body: '${DEFAULT_CONTENT}',
                    subject: '${DEFAULT_SUBJECT}',
                    to: '${DEFAULT_RECIPIENTS}')
            }
        }
    }
}