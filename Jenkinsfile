docker.withRegistry("registry.wutiarn.ru", "registry.wutiarn.ru")

image = docker.image("registry.wutiarn.ru/edustor/accounts")
docker.build(image)
image.push("latest")