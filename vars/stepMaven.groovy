def updateApplicationVersion() {
    container.maven {
        sh "mvn -B versions:set -DnewVersion=${VERSION}"
    }
}

def nexusDeployment() {
    container.mavenAndNpm {
        sh """
        mvn -B -V deploy:deploy-file \\
            -Durl=$NEXUS_URL \\
            -DrepositoryId=nexus \\
            -DgroupId=$GROUP_ID \\
            -DartifactId=$PROJECT_NAME \\
            -Dversion=$VERSION \\
            -Dpackaging=tar.gz \\
            -Dfile=$ARCHIVE
        """
    }
}
