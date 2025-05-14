# Event Organizing Web Application

## Setting up the application

### step 1: the database

To run this application, you will need a running mySql database server with a database schema having the same properties as defined in the application.properties file.

### step 2: the API

In the folder of the API (eowa) run the following commands:

 - mvn package
 - java -jar target/eowa-0.0.1-SNAPSHOT.jar

Java 21 is required.
 
### step 3: the web app

In the folder of the webapp (eowa/eowa_frontend) run the following commands:

 - ng build
 - http-server dist/eowa_frontend -p 4200

Or you can use "ng serve".
