FROM java:openjdk-8-jre
ADD target/cpt-sonar.jar /usr/local/bin/cpt-sonar.jar
EXPOSE 10001
CMD ["java", "-jar", "/usr/local/bin/cpt-sonar.jar"]
