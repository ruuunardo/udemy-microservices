#start
FROM amazoncorretto:21.0.4-alpine3.19

#information who maintains the image
MAINTAINER runardo.teamr

#add application file to image
COPY target/loans-0.0.1-SNAPSHOT.jar loans-0.0.1-SNAPSHOT.jar

#execute the application
ENTRYPOINT ["java", "-jar", "loans-0.0.1-SNAPSHOT.jar"]

