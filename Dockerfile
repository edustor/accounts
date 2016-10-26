FROM java:8-jdk

ADD . /code/src

WORKDIR /code/src
RUN ./gradlew build && rm -r /root/.gradle && mv build/dist/edustor-accounts.jar /code/

WORKDIR /code
CMD java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar edustor-accounts.jar