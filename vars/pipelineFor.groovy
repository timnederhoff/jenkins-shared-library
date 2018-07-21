import groovy.transform.Field
import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

@Field Map config = [
        PROJECT_NAME            : null,
        DOCKERHOST              : 'dockerhost',
        INSTALL_COMMAND         : 'npm install',
        LINT_COMMAND            : 'npm run lint',
        TEST_COMMAND            : 'npm run test:ci',
        BUILD_COMMAND           : 'npm run build',
        E2E_COMMAND             : 'npm run e2e:stubbed',
]

def angularApplication(Map m = [:]) {
    runWithCleanWorkspace(m) {

        conditionalStage('Install') {
            stepNpm.install()
        }

        conditionalStage('Linting') {
            stepNpm.linting()
        }

        conditionalStage('Unit Test') {
            stepNpm.unitTest()
        }

        conditionalStage('Build') {
            stepNpm.build()
        }

        conditionalStage('E2E') {
            stepNpm.e2e()
        }

        if (GIT_BRANCH.endsWith('master')) {
            conditionalStage('Release') {
                stepMaven.nexusDeployment()
            }

            conditionalStage('Deployment') {
                stepGeneral.deployToDev()
            }
        }

    }
}

def springApplication(Map m = [:]) {
    // TODO: make pipeline stages and steps
}

def runWithCleanWorkspace(Map m, Closure body) {
    config << m
    properties([[$class: 'GitLabConnectionProperty', gitLabConnection: '<my-gitlab-connection>']])
    node(config.DOCKERHOST) {

        config.VERSION = "$MAJOR_VERSION_NUMBER.$BUILD_NUMBER"

        currentBuild.displayName = config.VERSION

        conditionalStage('Clean Checkout') {
            // Clean workspace
            deleteDir()

            // Checkout and assign scm vars to config
            config << checkout(scm)
        }

        // put all config variables to the environment variables for this build
        withEnv(config.collect { key, value -> return key + '=' + value }) {
            ansiColor('xterm') {
                gitlabCommitStatus {
                    body()
                }
            }
        }
    }
}

def conditionalStage(String stageName, Closure body) {
    stage(stageName) {
        if (config.containsKey(stageName)) {
            if (!config[stageName]) {
                echo "Steps in '${stageName}' stage are skipped because it is disabled. Enable it by removing '${stageName}: false' from the configuration"
                Utils.markStageSkippedForConditional(stageName)
            } else if (config[stageName].toUpperCase() == 'DEBUG') {
                withEnv(['DEBUG_STAGE=true']) {
                    body()
                }
            }
        } else {
            body()
        }
    }
}