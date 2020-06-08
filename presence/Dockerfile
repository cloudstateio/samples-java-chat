FROM adoptopenjdk/openjdk11:debian

WORKDIR /opt
COPY ./target/presence-0.1-SNAPSHOT.jar .
EXPOSE 8080
ENV HOST 0.0.0.0
ENV PORT 8080
CMD java -jar presence-0.1-SNAPSHOT.jar