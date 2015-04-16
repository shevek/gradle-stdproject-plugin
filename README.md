# Introduction

This is a gradle plugin which applies a good "standard" convention
to a Java project. It is primarily intended for my own projects,
but other people or organizations may find it useful.

# Usage

```
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath 'org.anarres.gradle:gradle-stdproject-plugin:1.0.0-SNAPSHOT'
    }
}
```

Then for a multi-module project:
```
apply plugin: 'org.anarres.stdproject'
subprojects { 
    apply plugin: 'org.anarres.stdmodule'
}
```

Or for a single-module project:
```
apply plugin: 'org.anarres.stdproject'
apply plugin: 'org.anarres.stdmodule'
```

For a multi-module project with a gradle plugin:
```
apply plugin: 'org.anarres.stdproject'
subprojects {
	apply plugin: 'org.anarres.stdmodule'
}
project(':myproject-gradle') {
	apply plugin: 'org.anarres.stdplugin'
}
```

