# App README

- [ ] TODO Replace or update this README with instructions relevant to your application

## Build Pipeline with GitHub Actions

This project has an automated pipeline configured with GitHub Actions that performs the following steps:

1. Runs automatically whenever there is a **push** to the `main` branch.
2. Sets up **Java 21** in the environment.
3. Executes the command `mvn clean package` to generate the `.jar` file.
4. Publishes the `.jar` as a workflow artifact, available for download in the **Actions** tab on GitHub.
5. (Optional) Copies the `.jar` to the root of the repository, allowing direct access through the web interface.

Snippet from the `build.yml` file:

```yaml
- name: Build with Maven
  run: mvn clean package

- name: Copy JAR to root (optional)
  run: cp target/*.jar .

- name: Upload JAR as artifact
  uses: actions/upload-artifact@v3
  with:
    name: my-app-jar
    path: '*.jar'
```

## Project Structure

The sources of your App have the following structure:

```
src
├── main/frontend
│   └── themes
│       └── default
│           ├── styles.css
│           └── theme.json
├── main/java
│   └── [application package]
│       ├── base
│       │   └── ui
│       │       ├── component
│       │       │   └── ViewToolbar.java
│       │       ├── MainErrorHandler.java
│       │       └── MainLayout.java
│       ├── examplefeature
│       │   ├── ui
│       │   │   └── TaskListView.java
│       │   ├── Task.java
│       │   ├── TaskRepository.java
│       │   └── TaskService.java                
│       └── Application.java       
└── test/java
    └── [application package]
        └── examplefeature
           └── TaskServiceTest.java                 
```

The main entry point into the application is `Application.java`. This class contains the `main()` method that start up 
the Spring Boot application.

The skeleton follows a *feature-based package structure*, organizing code by *functional units* rather than traditional 
architectural layers. It includes two feature packages: `base` and `examplefeature`.

* The `base` package contains classes meant for reuse across different features, either through composition or 
  inheritance. You can use them as-is, tweak them to your needs, or remove them.
* The `examplefeature` package is an example feature package that demonstrates the structure. It represents a 
  *self-contained unit of functionality*, including UI components, business logic, data access, and an integration test.
  Once you create your own features, *you'll remove this package*.

The `src/main/frontend` directory contains an empty theme called `default`, based on the Lumo theme. It is activated in
the `Application` class, using the `@Theme` annotation.

## Starting in Development Mode

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

## Building for Production

To build the application in production mode, run:

```bash
./mvnw -Pproduction package
```

To build a Docker image, run:

```bash
docker build -t my-application:latest .
```

If you use commercial components, pass the license key as a build secret:

```bash
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .
```

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
App implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.

## CI/CD Pipeline (GitHub Actions)

This project includes a GitHub Actions workflow that automates the build and publication of the executable JAR file. On every push to the main branch, the workflow:

- Sets up Java 21
- Runs `mvn clean package` to build the project
- Copies the generated JAR to the project root
- Publishes the JAR as a downloadable artifact in the Actions tab

**Workflow excerpt:**
```yaml
name: Build and Publish JAR
on:
  push:
    branches:
      - main
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4
      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
      - name: Build with Maven
        run: mvn clean package
      - name: Copy JAR to project root
        run: cp target/*.jar .
      - name: Upload JAR artifact
        uses: actions/upload-artifact@v4
        with:
          name: ToDoApp-jar
          path: '*.jar'
```

After each push, you can download the JAR from the Actions tab or find it in the project root on GitHub.
