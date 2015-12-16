# Cheat Sheet

If you have a project built with gradle-stdproject-plugin, then you
will want to know the following, all of which comply with gradle
standard commandlines.

## Prerequisites

Install Java (JDK) but do not install a system-wide gradle.

## Developing

To compile all code and test classes (fast):

	./gradlew --daemon testClasses

## Testing

To run a single test in module moduleName, showing output to the
screen:

	./gradlew --daemon :moduleName:test -Dtest.single=TestClassName

To run all tests in module moduleName:

	./gradlew --daemon :moduleName:test

To run all tests:

	./gradlew --daemon test

## Building and Releasing

To clean build and create all reports:

	./gradlew --daemon clean build coberturaReport

To publish the build to Maven Central and github-pages:

	./gradlew --daemon uploadArchives publishGhPages

Or to install to Maven local only:

	./gradlew --daemon install

## Other useful tricks:

To build all artifacts without testing (faster):

	./gradlew --daemon assemble

To build the JAR file for a single module:

	./gradlew --daemon :moduleName:jar

