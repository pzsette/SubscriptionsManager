# SubscriptionsManager

![Java CI with Maven](https://github.com/pzsette/SubscriptionsManager/workflows/Java%20CI%20with%20Maven/badge.svg) 

[![Coverage Status](https://coveralls.io/repos/github/pzsette/SubscriptionsManager/badge.svg?branch=master)](https://coveralls.io/github/pzsette/SubscriptionsManager?branch=master)

A simple Java appplication to keep track and have an overview of subcriptions and their costs. Built using test driven development (TDD) with Java and Maven. It can be used through a GUI or a CLI interface.

## Requirements

* Java (Tested on Java 13, 11, 9, 8)
* Docker
* Maven

## Arguments

Argument | Description
---------|-------------
--mongo-host | MongoDB host address. Default value: `localhost`
--mongo-port | MongoDB host port. Default value: `27017`
--db-name | Database name. Default value: `subscriptionsmanager`
--db-collection | Collection name. Default value: `subscriptions`
--ui  | User interfaces options (`gui`, `cli`). Default value: `gui`

## Test

You can run all the tests for this app cloning the repo and executig the command: 

`mvn clean verify`

## Run the app

Before using the app run MongoDB in a container, specifing port, with the command:

`docker run -p 27017:27017 --rm mongo:4.2.3`

The build the JAR package through the command 

`comand per JAR` or alteratively you can download the JAR directly from here

Start the app with

`Java -jar -target/subscriptionsmanager-0.0.1-SNAPSHOT-jar-with-dependencies.jar [arguments]` 
