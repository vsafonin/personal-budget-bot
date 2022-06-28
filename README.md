# Personal budget bot
This is a telegram bot for making the process of creating a new transactions in [Personal budget](https://github.com/vsafonin/personal-budget) easier

# Requirments
1. Java jre 17
2. Mysql server

# Run on your PC
 For start this app on your pc, you have multiple options:
 ## Run via maven:
  - Install jre 17 on your pc.
  - install mysql server and create a database "personal_budget_bot"
  - set MYSQL_USER and MYSQL_PASSWORD parameters on ENV or you can edit src/main/resources/application.properties
  - set Telegram parametrs NAME_TG_BOT and TG_TOKEN on ENV or you can edit src/main/resources/application.properties
  - set Personal budget parametrs BASE_URL and BASE_PORT and BASE_SCHEME on ENV or you can edit  src/main/resources/application.properties
  - open the console and run:
    ```bash
	./mvnw spring-boot:run
    ```

 ## Run via java -jar

  - folow the steps 1-5 in [Run via maven](https://github.com/vsafonin/personal-budget-bot#run-via-maven)

  - open the console and run:
  ```bash
  ./mvn package -DskipTests
  ```
  - run in Console (if you want to change the app name - edit pom.xml file):
  ```bash
   java -jar java -jar target/personalAccounterBot-0.0.1.jar
  ```
 ## Run via docker see [personal-budget](https://github.com/vsafonin/personal-budget#run-via-docker-compose)
