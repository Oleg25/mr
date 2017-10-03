FROM openjdk:8-jre-alpine

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    citek_SLEEP=0 \
    JAVA_OPTS=""

ADD *.war /app.war

EXPOSE 8080
CMD echo "The application will start in ${citek_SLEEP}s..." && \
    sleep ${citek_SLEEP} && \
    java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.war
