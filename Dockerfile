FROM java:8-jdk

ADD . /code/src

WORKDIR /code/src
RUN ./gradlew build && rm -r /root/.gradle && mv build/dist/edustor-accounts.jar /code/

WORKDIR /code
CMD java -jar edustor-accounts.jar