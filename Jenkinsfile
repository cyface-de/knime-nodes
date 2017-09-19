#!groovy
node {
	def exception = null
	try {
	stage('Checkout') {
            // get source code
            checkout scm
        }

	stage('Build') {
            // Since there is no parent POM we need to go to the subdirectory.
            dir('de.cyface.knime') {
                // We need to create the Eclipse build directory, since it is referenced in build.properties. It will however stay empty if tycho is used during the following steps.
                sh "mkdir bin"
		// Requires the Pipeline Maven Plugin
		withMaven(maven: 'M3') {
			sh "mvn clean install"
		}
            }
	}
        stage('Code Quality') {
            // Since there is no parent POM we need to go to the subdirectory.
            dir('de.cyface.knime') {
                parallel(
                    'pmd': {
                        // static code analysis
                        withMaven(maven: 'M3') {
                           sh "mvn pmd:pmd pmd:cpd"
                        }
                    },
                    'checkstyle': {
                        withMaven(maven: 'M3') {
                           sh "mvn checkstyle:checkstyle"
                        }
                    },
                    'findbugs': {
                        withMaven(maven: 'M3') {
                           sh "mvn findbugs:findbugs"
                        }
                    }
                )
            }
        }
        stage('Publish Metrics to Sonarqube') {
            // Since there is no parent POM we need to go to the subdirectory.
            dir('de.cyface.knime') {
                // requires SonarQube Scanner 2.8+
                def scannerHome = tool 'SonarQubeScanner2.8';
                withSonarQubeEnv('Local SonarQube') {
                    sh "${scannerHome}/bin/sonar-scanner"
                }
            }
        }
	} catch(e) {
		exception = e
	}

	stage('Send notification') {
		if(exception!=null) {
		    echo "Caught Exception ${exception}"

        	    String recipient = 'klemens.muthmann@gmail.com'

	        	mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
        		     body: "It appears that ${env.BUILD_URL} is failing, you should do something about that!\n\n" + exception,
		             to: recipient,
                	     replyTo: recipient,
        		     from: 'noreply@cyface.de'

		        error "Failing build because of ${exception}"
		} else {
			echo "Build Successful"
			
			String recipient = 'klemens.muthmann@gmail.com'

			mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) successful",
			     body: "Build ${env.BUILD_URL} was successful.",
			     to: recipient,
			     replyTo: recipient,
			     from: 'noreply@cyface.de'
		}
	}
}
