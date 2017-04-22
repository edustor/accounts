FROM openjdk:8-jdk

ADD build/dist/edustor-accounts.jar .

HEALTHCHECK CMD curl -f http://localhost:8080/version
CMD java -jar edustor-accounts.jar