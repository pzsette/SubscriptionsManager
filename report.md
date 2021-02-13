# Subscriptions Manager

## Introduction

This project is a simple application built using the Test-Driven Development (**TDD**) methodology together with Build Automation and Continuous Integration service. This application allows a user to store informations about differents subscriptions and track the monthly spending for all of them.

## Application features

The applications itself is quite simple, the features are:

* Add a new subscription to the database. Every subscription has an id, a name a price and the relative repetition.
* Remove a subscription to the database
* Compute and show the monthly spending overall for all the subscription.

The user can interact with the application in two different ways: 

* **GUI** (Graphic User Interface)
* **CLI** (Command Line Interface)

## Used technologies

Here below are listed all the technlogies and tools used to make this application.

### Development tools

* **Operating Sistem**: macOS Catalina 10.15.7
* **IDE**: Eclipse 4.7.0
* **Programming language**: Java 13

### Version control

* **Git**: distributed version-control system for tracking changes in every kind of file.
* **GitHub**: provider for software development and version control using git.

### Database

* **MongoDB**: NoSql and document-oriented database program.
* **Docker**: tool designed to make it easier to create, deploy, and run applications by using containers .

### Build Automation

* **Maven**: build automation tool used primarily for Java projects.

### Testing

* **JUnit**: open source Unit Testing Framework for JAVA.
* **AssertJ**: provides a rich set of assertions to improves test code. readability
* **AssertJSwign**: Java library that provides a fluent interface for functional Swing UI testing.
* **Mockito**: framework which can be used in conjunction with JUnit. Mockito allows to create and configure mock objects.
* **Testcontainer**:  Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases.

### Code Quality

* **SonarQube**: platform for continuous inspection of code quality to perform static analysis of code to detect bugs, code smells, and security vulnerabilities.
* **SonarCloud**: SonarQube instance in the cloud. It offers free continuous code quality analyses and also decorates pull requests on GitHub.

## Implementation

The whole application is based on the **MVC** Model-View-Controller) patter. To be precise we used the MVP a slightly variant of the MVC. 

### Model

This class represents a subcription instance. It has four attributes: id, name, price and repetition. The repetition field is a ```String``` and can assume three values: ```"Weekly"```, ```"Monthly"```, ```"Annual"```. This class contains all the getter/setter and the mothods ```equals```, ```toString```, ```equals```.

### Controller

The Controller acts as a bridge between the view and the model. It retrieves data from the repository (the model) and formats it into for dispaly in the view. The ```SubscriptionController``` class obviously has a reference to a View object and to a Repository object. It has three methods to:

* Load alla elements into the view from the repository.
* Update the view when a new Subscrition in added.
* Update the view when a Subscription in delete.

### Repository

The repository interface provides methods for simple operations on the database.

The ```SubscriptionMongoRepository``` class implents this interface to use a MongoDB database. The constructor takes a ```MongoCLient``` object, the database and the repository name.

To avoid having to manually convert Subscription objects into Mongo documents it was added the POJOs (Plain Old Java Object) support via the PojoCodec, wich allows for direct serialization of POJOs to and from BSON.

Automatic POJO support can be provided by building ```PojoCodecProvider``` by calling ```builder.build()``` then it can be combined with an existing ```CodecRegistry``` to create a new registry that will also support the registered POJOs.
After creating the codec registry, the model type must be passed to the ```getCollection``` method in ```MongoDatabase```, and the registry can then be added using the ```withCodecRegistry``` modifier of ```MongoCollection```.

```
public SubscriptionMongoRepository(MongoClient client, String dbName, String collectionName) {
	
	CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
			fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	
	collection = client
			.getDatabase(dbName)
			.getCollection(collectionName, Subscription.class)
			.withCodecRegistry(pojoCodecRegistry);		
}
```	
### View
The generic view interface provides methods for loading and deleting methods and showing error messages. This application offers to the user to choose between two different UIs.

#### GUI
The GUI view is build using the Swing framework. Most of the work was done through the Window Builder tool. 

<img src="screenshots/sub_gui.png" height=300/>

The ```updateAmountLabel()```
method is responsable for updating the spending label whenever necessary using the static ```computeSpending(List<Subscription>)``` method of the ```SubscriptionSpending``` class.
 
#### CLI

<img src="screenshots/sub_cli.png" height=145/>

Since there are no tools for testing CLI-based application it was necessry to find a workaround to do that. All the functios uses  ```Syste.out``` to print in the console, so we can capture that output changing it with a different ```PrintStream```. In particular if change ```System.out``` with ```ByteArrayOutputStream```, the we can capture the output as a ```String```. Using **Dependancy Injection** we can create a different ```SubscriptionViewCLI``` instance depending if we're testing or executing the app.

```
public class SubscriptionViewCLI implements SubscriptionView {
	
	private List<Subscription> listSubscriptions; 
	
	private Scanner scanner;
	private PrintStream output;
	
	public SubscriptionViewCLI(PrintStream output) {
		this.output = output;
		listSubscriptions = new ArrayList<>();
	}
	
	/* ... */
}
```

The function ``` runApp()``` set scanner attribute, show the initial menu and then depending on the user's choice call the appropriate function.

``` 
public void runView() {
	scanner = new Scanner(System.in);
	output.println("SUBSCRIPTIONS MANAGER");
	boolean exit = false;
	while (!exit) {
		showOptions();
		int choice = forceDigitChoice(1, 5);
		switch(choice) {
		case 1:
			showSubscriptions();
			break;
		case 2:
			showSpending();
			break;
		case 3:	
			addSubscription();
			break;
		case 4:
			deleteSubscription();
			break;
		case 5:
			exit = true;
			break;
		default:
			break;
		}
	}
	output.println("Goodbye!");
}
``` 

The functions ```forceDoubleChoice()``` and ```forceDigitChoice(int low, int high)``` ensure that the user chooses only allowed values.
	





## Testing

### Unit tests

### Integrations tests

### End To End tests

### Code coverage

### Mutation testing

### Continous integration

```
name: Java CI with Maven

on: [push]

jobs:
  build:

    runs-on: ubuntu-18.04

    name: Test on Java13

    steps:
    - uses: actions/checkout@v2
      with:
        fetch-depth: 0
    - name: Set up JDK 13
      uses: actions/setup-java@v1
      with:
        java-version: 13
    - name: Cache Maven packages
      uses: actions/cache@v2
      with:
        path: ~/.sonar/cache
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
    - name: Run with Maven
      run: |
          xvfb-run --server-args="-screen 0, 1280x720x24" -a \
          mvn -B -f pom.xml clean verify \
          $ENABLED_PROFILES $ADDITIONAL_MAVEN_ARGS \
          -D repoToken=$COVERALLS_TOKEN \
          -D sonar.host.url=$SONAR_URL \
          -D sonar.organization=$SONAR_ORGANIZATION \
          -D sonar.projectKey=$SONAR_PROJECT
      env:
          ENABLED_PROFILES: -P jacoco,pit-mutation
          ADDITIONAL_MAVEN_ARGS: coveralls:report sonar:sonar
          COVERALLS_TOKEN: ${{secrets.COVERALLS_TOKEN}}
          SONAR_URL: https://sonarcloud.io
          SONAR_ORGANIZATION: pzsette777
          SONAR_PROJECT: pzsette_SubscriptionsManager
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

  build-on-pr-merge:
    if: startsWith(github.event.head_commit.message, 'Merge pull request')

    runs-on: ubuntu-18.04
    strategy:
      matrix:
        java: [8, 9, 11]
    name: Test on older Java versions

    steps:
    - uses: actions/checkout@v2
      with:
        fetch-depth: 0
    - name: Set up Java${{matrix.java}}
      uses: actions/setup-java@v1
      with:
        java-version: ${{matrix.java}}
    - name: Cache Maven packages
      uses: actions/cache@v2
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2

    - name: Build with Maven
      run: |
          xvfb-run --server-args="-screen 0, 1280x720x24" -a \
          mvn -f pom.xml -Dmaven.compiler.source=${{matrix.java}} -Dmaven.compiler.target=${{matrix.java}} clean verify $ACTIVE_PROFILES
      env:
        ACTIVE_PROFILES: -Pjacoco,mutation-testing

```

## Code quality

#### SonarCube

#### SonarCloud

## Execution
