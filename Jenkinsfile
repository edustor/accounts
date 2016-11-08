//docker.withRegistry("https://registry.wutiarn.ru", "registry.wutiarn.ru") {
//    checkout scm
//    image = docker.build("registry.wutiarn.ru/edustor/accounts")
//    image.push()
//}

docker.image("wutiarn/rancher-deployer").inside {
    withCredentials([[$class          : 'UsernamePasswordMultiBinding', credentialsId: env.RANCHER_API_CREDENTIALS,
                      usernameVariable: 'ACCESS_KEY', passwordVariable: 'SECRET_KEY']]) {
        env.RANCHER_ACCESS_KEY = ACCESS_KEY
        env.RANCHER_SECRET_KEY = SECRET_KEY
    }
    sh "env"
    sh "/root/upgrade.sh"
}