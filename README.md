## Backends for libGDX, easy to build

Since we have few regular libGDX releases anymore, it is a problem to fix or extend backend behaviour that can't be overridden.
While there's always a way to work around bugs in the core project, this is often not possible in the backends.

This is where this repo comes in.

If you need to change build-in behaviour, but don't manage to get the complete libGDX repo to build, don't want to 
build your very own version or don't want to use snapshot versions, this repo is what you need. Check my own additions
to see what else is changed.

... but you're probably here for GWT 2.9.0, which is available with a backend here.

Your options for using this repo depend on whether you want to make changes to it:

### How to build (I want to make changes or control the process)

* Clone this repo
* Checkout the revision you need (next paragraph)
* Type `gradlew install`
* Change your project's backend dependency to the one you wish

### How to use as a dependency (I want to use a build without making changes)

In case you don't want to change something here yourself, but just want to use some of the additions, you can either use
the 1.910.2 stable release (featuring GWT 2.9.0) from the standard Maven Central repository, or use a Jitpack
dependency. If you choose JitPack, don't forget to add Jitpack as a repo to your project:
```groovy
    allprojects {
	    repositories {
		    ...
		    maven { url 'https://jitpack.io' }
	    }
    }
```

## For use with libGDX 1.9.10 core

### 1.910.0

Checkout branch [release/1.910.0](https://github.com/MrStahlfelge/gdx-backends/tree/release/1.910.0) of
MrStahlfelge's repo to use this version, or use the following JitPack dependencies for GWT:
```groovy
       implementation 'com.github.MrStahlfelge.gdx-backends:gdx-backend-gwt:1.910.0'
       implementation 'com.github.MrStahlfelge.gdx-backends:gdx-backend-gwt:1.910.0:sources'
```
Own additions
* GWT: Switched to WebAudio, fixes sounds for mobiles too. [Original PR by @barkholt](https://github.com/libgdx/libgdx/pull/4220). See [current PR](https://github.com/libgdx/libgdx/pull/5659) for more information.
* GWT: Faster bootstrap process by lazy loading assets. See [current PR](https://github.com/libgdx/libgdx/pull/5677) for more information.
* GWT: Fixed density problems on mobile with new config setting. See [current PR](https://github.com/libgdx/libgdx/pull/5691)

### 1.910.1

Use MrStahlfelge's repo, with these JitPack dependencies for GWT:
```groovy
      implementation 'com.github.MrStahlfelge.gdx-backends:gdx-backend-gwt:master-SNAPSHOT'
      implementation 'com.github.MrStahlfelge.gdx-backends:gdx-backend-gwt:master-SNAPSHOT:sources'
```
Own additions:
* GWT: Fix for getPeriphalAvailable reporting accelerometer present on desktop. [Pending PR](https://github.com/libgdx/libgdx/pull/5758)
* GWT: Pulled feature policy implementation by @SimonIT. [Pending PR](https://github.com/libgdx/libgdx/pull/5784)
* GWT: GWT preferences NPE fix by @SimonIT. [Pending PR](https://github.com/libgdx/libgdx/pull/5838)

### 1.910.2

Checkout the tag `v1.910.2` of this repo to use this version, or use one of the following sets of dependencies for GWT:

Maven Central (uses the repository `mavenCentral()`, which most projects already have):
```groovy
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.910.2'
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.910.2:sources'
```

JitPack (needs the JitPack repository given above):
```groovy
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.910.2'
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.910.2:sources'
```

If Maven Central is down, you can switch to JitPack, or vice versa; the files should be equivalent.

Own additions:
* GWT: Update to GWT 2.9.0; this benefits from some changes in users' build.gradle files.

Upgrading to GWT 2.9.0 needs a few changes if you want to take full advantage of the new Java 11 features
it permits. **It also requires any dependency on GWT 2.8.2 to be changed to 2.9.0.** If you use GWT 2.9.0 anywhere, you
have to use it everywhere; that's why the backend needs to be changed and not just your code.
- [ ] Change language level to 11 in core, html, and anywhere else that you want to use features
like `var`: where sourceCompatibility is set in build.gradle files, change it to `sourceCompatibility = 11`
- [ ] Change GWT's source level to 11 as a special requirement: inside the `gwt` block in the html/build.gradle
file, add this line after the compiler settings: `sourceLevel = 1.11`
- [ ] Make sure your Gradle and/or IDE settings are configured to use JDK 11 or newer.
- [ ] The version for your GWT Gradle plugin may or may not matter, but so far this has been tested only
on the most recent version, `org.wisepersist:gwt-gradle-plugin:1.0.13` ;
[gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff) stores the plugin version in `gwtPluginVersion` in
`gradle.properties`, so you can change it there to `1.0.13` if you use that project generator.

## For use with 1.9.11

### 1.911.1

Checkout the tag `v1.911.1` of this repo to use this version, or use one of the following sets of dependencies for GWT:

Maven Central (uses the repository `mavenCentral()`, which most projects already have):
```groovy
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.911.1'
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.911.1:sources'
```

JitPack (needs the JitPack repository given above):
```groovy
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.911.1'
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.911.1:sources'
```

If Maven Central is down, you can switch to JitPack, or vice versa; the files should be equivalent.

Own additions:
* GWT: Update to GWT 2.9.0; this benefits from some changes in users' build.gradle files.

Upgrading to GWT 2.9.0 needs a few changes if you want to take full advantage of the new Java 11 features
it permits. **It also requires any dependency on GWT 2.8.2 to be changed to 2.9.0.** If you use GWT 2.9.0 anywhere, you
have to use it everywhere; that's why the backend needs to be changed and not just your code.
- [ ] Change language level to 11 in core, html, and anywhere else that you want to use features
like `var`: where sourceCompatibility is set in build.gradle files, change it to `sourceCompatibility = 11`
- [ ] Change GWT's source level to 11 as a special requirement: inside the `gwt` block in the html/build.gradle
file, add this line after the compiler settings: `sourceLevel = 1.11`
- [ ] Make sure your Gradle and/or IDE settings are configured to use JDK 11 or newer.
- [ ] The version for your GWT Gradle plugin may or may not matter, but so far this has been tested only
on the most recent version, `org.wisepersist:gwt-gradle-plugin:1.0.13` ;
[gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff) stores the plugin version in `gwtPluginVersion` in
`gradle.properties`, so you can change it there to `1.0.13` if you use that project generator.


## For use with 1.9.12-SNAPSHOT

### 1.912.0

Checkout the branch `master` of this repo to use this version, or use the following JitPack dependencies for GWT:
```groovy
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:master-SNAPSHOT'
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:master-SNAPSHOT:sources'
```
Own additions:
* GWT: Update to GWT 2.9.0; this benefits from some changes in users' build.gradle files.

Upgrading to GWT 2.9.0 needs a few changes if you want to take full advantage of the new Java 11 features
it permits. **It also requires any dependency on GWT 2.8.2 to be changed to 2.9.0.** If you use GWT 2.9.0 anywhere, you
have to use it everywhere; that's why the backend needs to be changed and not just your code.
- [ ] Change language level to 11 in core, html, and anywhere else that you want to use features
like `var`: where sourceCompatibility is set in build.gradle files, change it to `sourceCompatibility = 11`
- [ ] Change GWT's source level to 11 as a special requirement: inside the `gwt` block in the html/build.gradle
file, add this line after the compiler settings: `sourceLevel = 1.11`
- [ ] Make sure your Gradle and/or IDE settings are configured to use JDK 11 or newer.
- [ ] The version for your GWT Gradle plugin may or may not matter, but so far this has been tested only
on the most recent version, `org.wisepersist:gwt-gradle-plugin:1.0.13` ;
[gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff) stores the plugin version in `gwtPluginVersion` in
`gradle.properties`, so you can change it there to `1.0.13` if you use that project generator.

## Future work

### GWT
- [ ] Move resizable browser window support into the backend, no template hassle any more
- [ ] Electron extensions
