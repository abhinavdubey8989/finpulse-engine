# About
This project is the backend app for expense-tracking system.
- Uses Java 21.0.8
- Uses Hibernate ORM core version 7.2.0.Final
- Uses Spring boot version v4.0.1

# Java version management
- Used `jenv` for java multiple version management locally
- Below are useful commands

```
# check jenv version
jenv --version

# get list of java version on local machine
jenv versions

# switch to a particular java version
jenv local 21


# check java version for this project
java -version
```


# Adding new dependency
- Mention/add the dependency in build.gradle
- Download the dependencies using : `./gradlew dependencies`


# Run the project locally
- Run : `./gradlew clean build`
- This deletes all previous build output and then rebuild everything from scratch, including tests. This is the most reliable way to verify your project builds correctly.
- 