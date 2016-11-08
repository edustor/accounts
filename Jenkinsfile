docker.withRegistry("https://registry.wutiarn.ru", "registry.wutiarn.ru") {
    checkout scm
    image = docker.build("registry.wutiarn.ru/edustor/accounts")
    image.push()
}