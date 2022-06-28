# Personal budget bot
This is telegram bot for make easer process create new transactions in [Personal budget](https://github.com/vsafonin/personal-budget)

# Requirments
 1. Java jre 17

# Run in your PC
 For start this app in your pac, you have multiple options:
 ## Run via maven:
  - Install jre 17 in your pc.

  - install mysql and create database "personal_budget_bot"
  - set MYSQL_USER and MYSQL_PASSWORD in ENV or edit src/main/resources/application.properties
  - set Telegram parametrs NAME_TG_BOT and TG_TOKEN in ENV or edit src/main/resources/application.properties
  - set Personal budget parametrs BASE_URL and BASE_PORT and BASE_SCHEME in ENV or edit  src/main/resources/application.properties
  - open console and run:
    ```bash
	./mvnw spring-boot:run
    ```

 ## Run via java -jar

  - folow the instructions 1-5 in "Run via maven"

  - open Console and run:
  ```bash
  ./mvn package -DskipTests
  ```
  - run in Console (if you want change app name - edit pom.xml file):
  ```bash
   java -jar java -jar target/personalAccounterBot-0.0.1.jar
  ```
 ## Run via docker see [personal-budget](https://github.com/vsafonin/personal-budget#run-via-docker-compose)
