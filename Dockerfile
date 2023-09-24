FROM openjdk:11
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN java --version
CMD ["java", "--version"]
