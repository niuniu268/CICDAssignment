FROM openjdk:11
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN java --version
CMD ["java", "-jar", "CICDAssignment-0.0.1-SNAPSHOT.jar"]
