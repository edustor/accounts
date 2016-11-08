docker.withRegistry("https://registry.wutiarn.ru", "registry.wutiarn.ru") {
    sh "ls -lah"
    image = docker.build("registry.wutiarn.ru/edustor/accounts")
    image.push()
}