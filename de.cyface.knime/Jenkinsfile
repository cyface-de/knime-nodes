#!groovy
node {
	def exception = null
	try {
	stage('Checkout') {
            // get source code
            checkout scm
        }

	stage('Build') {
		// Requires the Pipeline Maven Plugin
		withMaven(maven: 'M3') {
			sh "mvn clean install"
		}
	}
        stage('Code Quality') {
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
        stage('Publish Metrics to Sonarqube') {
            // requires SonarQube Scanner 2.8+
            def scannerHome = tool 'SonarQubeScanner2.8';
            withSonarQubeEnv('Local SonarQube') {
                sh "${scannerHome}/bin/sonar-scanner"
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
