docker.withRegistry("https://registry.wutiarn.ru", "registry.wutiarn.ru") {
    git credentialsId: 'BitBucket ssh', url: 'git@bitbucket.org:edustor/accounts.git'
    sh "ls -lah"
    image = docker.build("registry.wutiarn.ru/edustor/accounts")
    image.push()
}