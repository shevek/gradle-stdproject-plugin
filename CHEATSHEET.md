# Cheat Sheet

If you have a project built with gradle-stdproject-plugin, then you
will want to know the following, all of which comply with gradle
standard commandlines.

## Building

To build and test:

	./gradlew --daemon build

To clean build and publish all artifacts:

	./gradlew --daemon clean build coberturaReport uploadArchives publishGhPages

## Developing

To compile only (fast):

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

