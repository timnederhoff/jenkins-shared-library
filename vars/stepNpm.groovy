def install() {
    container.npm {
        sh INSTALL_COMMAND
    }
}

def linting() {
    container.npm {
        sh LINT_COMMAND
    }
}

def unitTest() {
    container.npm {
        sh TEST_COMMAND
        step([$class: 'JUnitResultArchiver', testResults: TEST_RESULTS_PATH])
        publishHTML(target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : 'coverage',
                reportFiles          : 'index.html',
                reportName           : 'Karma coverage report'
        ])
    }
}

def build() {
    container.npm {
        sh BUILD_COMMAND
        sh "sed -i -e 's/LOCAL/${VERSION}/g' dist/config/version.json"
        sh "sed -i -e 's/0000000000000000000000000000000000000000/${GIT_COMMIT}/g' dist/config/version.json"
        sh "cd dist && tar zcf ../${ARCHIVE} ."
    }
}

def e2e() {
    container.e2eTestMachine {
        try {
            sh E2E_COMMAND
        } catch (e) {
            throw e
        } finally {
            publishHTML(target:
                    [
                            allowMissing         : true,
                            alwaysLinkToLastBuild: true,
                            keepAll              : true,
                            reportDir            : "target/testreport",
                            reportFiles          : 'index.html',
                            reportName           : "E2E tests (local)"
                    ])
        }
    }
}

def pack() {
    container.npm {
        sh "npm run pack ${VERSION}"
    }
}

def publish() {
    container.npm {
        sh "npm run publish ${VERSION}"
    }
}