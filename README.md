# Introduction

This is a suite of gradle plugins which apply a good "standard"
convention to a Java project, and is intended to replace the majority
of the "boring" bits of build.gradle. It integrates (at least)
the following:

* findbugs
* animalsniffer (be.insaneprogramming.gradle.animalsniffer)
* license checking and download (license)
* version update checking (com.github.ben-manes.versions)
* github and github-pages (org.ajoberstar.github-pages)
* maven (com.bmuschko.nexus)
* nexus (com.bmuschko.nexus)
* gradle plugin metadata generation
* bintray (com.jfrog.bintray)

It is primarily intended to ease the maintenance of my own numerous
projects, but other people or organizations may find it useful.

# Usage

Apply the plugin like this:
```
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath 'org.anarres.gradle:gradle-stdproject-plugin:1.0.0'
    }
}
```

Then for a multi-module project:
```
apply plugin: 'org.anarres.stdproject'
subprojects { 
    apply plugin: 'org.anarres.stdmodule'
	group = 'org.myorg.plugin'
	stdmodule {
		description "This is the description of my project."
		author {
			id 'me'
			name 'My Name'
			email 'my@email.com'
		}
		author {
			id 'you'
			name 'Your Name'
			email 'you@email.com'
		}
		license 'Apache-2.0'
		license {
			name 'My Custom License'
			url 'http://www.myorg.org/license.html'
		}
		// ...
	}
}
```

Or for a single-module project:
```
apply plugin: 'org.anarres.stdproject'
apply plugin: 'org.anarres.stdmodule'
stdmodule {
	// ...
}
```

For a multi-module project with a gradle plugin:
```
apply plugin: 'org.anarres.stdproject'
subprojects {
	apply plugin: 'org.anarres.stdmodule'
	stdmodule {
		// ...
	}
}
project(':myproject-gradle') {
	apply plugin: 'org.anarres.stdplugin'
	stdplugin {
		implementation "org.myorg.myplugin" "org.myorg.myplugin.MyPlugin"
		tags "my", "plugin", "is", "cool"
	}
}
```

