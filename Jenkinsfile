#!groovy
node {
	def exception = null
	try {
	stage('Checkout') {
            // get source code
            checkout scm
        }

	stage('Build') {
		sh "mvn clean install"
	}
	// TODO this is a temporary fix until the Sonarqube plugin has been adapted to pipelines: https://github.com/jenkinsci/pipeline-plugin/blob/master/COMPATIBILITY.md
        // Requires the Credentials Binding plugin
        stage('Publish Metrics to Sonarqube') {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'sonar-creds', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                echo "Accessing sonarqube with username ${env.USERNAME} and password ${env.PASSWORD}."
                sh "/opt/sonar_scanner/bin/sonar-scanner -e -Dsonar.host.url=http://sonarqube:9000 -Dsonar.login=${env.USERNAME} -Dsonar.password=${env.PASSWORD} -Dsonar.projectKey=knime-nodes -Dsonar.projectName=knime-nodes -Dsonar.projectVersion=1.0.0-SNAPSHOT -Dsonar.sources=src -Dsonar.binaries=target/classes -Dsonar.language=java -Dsonar.sourceEncoding=UTF-8 -Dsonar.java.source=1.8"
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
