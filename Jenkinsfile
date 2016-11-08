node {
    stage "Build"
    docker.withRegistry("https://registry.wutiarn.ru", "registry.wutiarn.ru") {
        checkout scm
        image = docker.build("edustor/accounts")
        stage "Push"
        image.push()
    }

    stage "Deploy"
    docker.image("wutiarn/rancher-deployer").inside {
        withCredentials([[$class          : 'UsernamePasswordMultiBinding', credentialsId: "api.rancher.wutiarn.ru",
                          usernameVariable: 'ACCESS_KEY', passwordVariable: 'SECRET_KEY']]) {
            env.RANCHER_ACCESS_KEY = ACCESS_KEY
            env.RANCHER_SECRET_KEY = SECRET_KEY
        }

        env.RANCHER_SERVICE_NAME = "edustorAccounts"
        env.RANCHER_STACK_ID = "1st13"
        env.RANCHER_STACK_NAME = "edustor"
        env.RANCHER_URL = "http://hs.wutiarn.ru:8080/v1/projects/1a5"

        sh "/root/upgrade.sh"
    }
}