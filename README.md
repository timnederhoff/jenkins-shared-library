# jenkins-shared-library
Jenkins shared library containing extra functions/vars to make a pipeline easier to maintain and setup

example:
```groovy
@Library('jenkins-shared-library') _

pipelineFor([
        PROJECT_NAME            : 'My Project',
        DOCKERHOST              : 'dockerhost',
        INSTALL_COMMAND         : 'npm install',
        LINT_COMMAND            : 'npm run lint',
        TEST_COMMAND            : 'npm run test:ci',
        BUILD_COMMAND           : 'npm run build',
        E2E_COMMAND             : 'npm run e2e',
])
```
