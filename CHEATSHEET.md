# Cheat Sheet

If you have a project built with gradle-stdproject-plugin, then you
will want to know the following, all of which comply with gradle
standard commandlines.

## Prerequisites

Install Java (JDK) but do not install a system-wide gradle.

## Building and Releasing

To build and test:

	./gradlew --daemon build

To clean build and create all reports.

	./gradlew --daemon clean build coberturaReport

To publish the build to Maven Central and github-pages:

	./gradlew --daemon uploadArchives publishGhPages

Or to install to Maven local only:

	./gradlew --daemon install

## Developing

To compile all code and test classes (fast):

	./gradlew --daemon testClasses

To build all artifacts without testing (medium):

	./gradlew --daemon assemble

## Testing

To run all tests:

	./gradlew --daemon test

To run all tests in module moduleName:

	./gradlew --daemon :moduleName:test

To run a single test in module moduleName:

	./gradlew --daemon :moduleName:test -Dtest.single=TestClassName

