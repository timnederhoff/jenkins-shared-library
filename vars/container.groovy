def maven(Closure body) {
    docker.image('maven:3.5-jdk-8').inside() {
        checkDebugMode()
        body()
    }
}

def npm(Closure body) {
    docker.image('node:8.11').inside() {
        checkDebugMode()
        body()
    }
}

def checkDebugMode() {
    if (env.DEBUG_STAGE) {
        input 'Debug mode active for this stage:\nexecution is paused before steps.'
    }
}
