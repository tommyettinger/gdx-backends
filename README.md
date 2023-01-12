## Backends for libGDX, easy to build

 - Do you need to change the behavior of one or more of the libGDX backends for your own (nefarious?) purposes?
 - Or do you just want **GWT 2.10.0** support before libGDX has it officially?

This is where this repo comes in.

If you need to change build-in behaviour, but don't manage to get the complete libGDX repo to build, don't want to 
build your very own version or don't want to use snapshot versions, this repo is what you need. Check my own additions
to see what else is changed.

... but you're probably here for GWT 2.10.0, which is available with a backend here.

Your options for using this repo depend on whether you want to make changes to it:

### How to build (I want to make changes or control the process)

* Clone this repo
* Checkout the revision you need (next paragraph)
* Type `gradlew install`
* Change your project's backend dependency to the one you wish

### How to use as a dependency (I want to use a build without making changes)

In case you don't want to change something here yourself, but just want to use some additions, you can either use the
1.1100.0 stable release (featuring GWT 2.10.0) from the standard Maven Central repository, or use a Jitpack dependency.
The latest stable release is documented near the bottom of the README.md file.
If you choose JitPack, you might need to add JitPack as a repo to your project (current gdx-setup and
gdx-liftoff both do this step for you already):
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

All of these PRs have been merged as of 1.9.12.

### 1.910.1

Use MrStahlfelge's repo, with these JitPack dependencies for GWT:
```groovy
      implementation 'com.github.MrStahlfelge.gdx-backends:gdx-backend-gwt:master-SNAPSHOT'
      implementation 'com.github.MrStahlfelge.gdx-backends:gdx-backend-gwt:master-SNAPSHOT:sources'
```
Own additions:
* GWT: Fix for getPeripheralAvailable reporting accelerometer present on desktop. [Pending PR](https://github.com/libgdx/libgdx/pull/5758)
* GWT: Pulled feature policy implementation by @SimonIT. [Pending PR](https://github.com/libgdx/libgdx/pull/5784)
* GWT: GWT preferences NPE fix by @SimonIT. [Pending PR](https://github.com/libgdx/libgdx/pull/5838)

All of these PRs have been merged as of 1.9.12.

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

This supports GWT 2.9.0 (see the GWT 2.9.0 section below).

## For use with 1.9.11

### 1.911.2

Checkout the tag `v1.911.2` of this repo to use this version, or use one of the following sets of dependencies for GWT:

Maven Central (uses the repository `mavenCentral()`, which most projects already have):
```groovy
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.911.2'
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.911.2:sources'
```

JitPack (needs the JitPack repository given above):
```groovy
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.911.2'
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.911.2:sources'
```

If Maven Central is down, you can switch to JitPack, or vice versa; the files should be equivalent.

(There was a 1.911.1 release, but it had unexpected incompatibility with GWT.)

This supports GWT 2.9.0 (see the GWT 2.9.0 section below).

## For use with 1.9.12

Version 1.9.12 of libGDX merges all of MrStahlfelge's additions to the GWT backend, so the only reason to use this
unofficial backend is GWT 2.9.0 support.

### 1.912.0

Checkout the tag `v1.912.0` of this repo to use this version, or use one of the following sets of dependencies for GWT:

Maven Central (uses the repository `mavenCentral()`, which most projects already have):
```groovy
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.912.0'
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.912.0:sources'
```

JitPack (needs the JitPack repository given above):
```groovy
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.912.0'
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.912.0:sources'
```

This supports GWT 2.9.0 (see the GWT 2.9.0 section below).

## For use with 1.10.0

Version 1.10.0 of libGDX already has all of MrStahlfelge's additions to the GWT backend merged, so the only reason to
use this unofficial backend is GWT 2.9.0 support.

### 1.100.0

Checkout the tag `v1.100.0` of this repo to use this version, or use one of the following sets of dependencies for GWT:

Maven Central (uses the repository `mavenCentral()`, which most projects already have):
```groovy
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.100.0'
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.100.0:sources'
```

JitPack (needs the JitPack repository given above):
```groovy
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.100.0'
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.100.0:sources'
```

This supports GWT 2.9.0 (see the GWT 2.9.0 section below).

### 1.100.1

Checkout the tag `v1.100.1` of this repo to use this version, or use one of the following sets of dependencies for GWT:

Maven Central (uses the repository `mavenCentral()`, which most projects already have):
```groovy
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.100.1'
      implementation 'com.github.tommyettinger:gdx-backend-gwt:1.100.1:sources'
```

JitPack (needs the JitPack repository given above):
```groovy
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.100.1'
      implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.100.1:sources'
```

This version fixes some GWT-specific bugs in recently-changed files, like ScreenUtils. The now-recommended
`ScreenUtils.clear()` method didn't work in v1.100.0, for example, but does in v1.100.1 .

This supports GWT 2.9.0 (see the GWT 2.9.0 section below).

### 1.1100.0

Checkout the tag `v1.1100.0` of this repo to use this version, or use one of the following sets of dependencies for GWT:

Maven Central (uses the repository `mavenCentral()`, which most projects already have):
```groovy
    implementation "com.github.tommyettinger:gdx-backend-gwt:1.1100.0"
    implementation "com.github.tommyettinger:gdx-backend-gwt:1.1100.0:sources"
    implementation "com.google.jsinterop:jsinterop-annotations:2.0.0:sources"
```

JitPack (needs the JitPack repository given above):
```groovy
    implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.1100.0'
    implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:v1.1100.0:sources'
    implementation "com.google.jsinterop:jsinterop-annotations:2.0.0:sources"
```

The dependency on `jsinterop-annotations` is new, and was probably sometimes needed by GWT 2.9.0 but is definitely
needed by GWT 2.10.0 .

The version here is slightly different because the old `1.912.0` was getting sorted as more recent than `1.110.0`, and
also because `1.110.0` could refer to libGDX 1.1.10, subversion 0, if it existed.

This supports GWT 2.10.0 (see the GWT 2.9.0 section below, which still applies).

### 1.1100.1-SNAPSHOT

Checkout the `master` branch, or use the following JitPack dependencies for GWT:

```groovy
    implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:master-SNAPSHOT'
    implementation 'com.github.tommyettinger.gdx-backends:gdx-backend-gwt:master-SNAPSHOT:sources'
    implementation "com.google.jsinterop:jsinterop-annotations:2.0.0:sources"
```

The dependency on `jsinterop-annotations` is new, and was probably sometimes needed by GWT 2.9.0 but is definitely
needed by GWT 2.10.0 .

The version here is slightly different because the old `1.912.0` was getting sorted as more recent than `1.110.0`, and
also because `1.110.0` could refer to libGDX 1.1.10, subversion 0, if it existed.

This supports GWT 2.10.0 (see the GWT 2.9.0 section below, which still applies).

## GWT 2.9.0/2.10.0 Support

Most of these versions have been updated to use GWT 2.9.0; this benefits from some changes in users' build.gradle files, 
but should still work without changes as long as no other GWT versions are in use. The master version and 1.1100.0 use
GWT 2.10.0 .

However, upgrading to GWT 2.9.0 or 2.10.0 needs a few changes if you want to take full advantage of the new Java 11 features
it permits. **It also requires any dependency on GWT 2.8.2 to be changed to the newer GWT version, 2.9.0 or 2.10.0.** If
you use GWT 2.9.0 or 2.10.0 anywhere, you have to use it everywhere; that's why the backend needs to be changed and not
just your code.
- [ ] Change language level to 11 in core, html, and anywhere else that you want to use features
  like `var`: where sourceCompatibility is set in build.gradle files, change it to `sourceCompatibility = 11`
- [ ] Change GWT's source level to 11 as a special requirement: inside the `gwt` block in the html/build.gradle
  file, add this line after the compiler settings: `sourceLevel = 1.11`
- [ ] Make sure your Gradle and/or IDE settings are configured to use JDK 11 or newer. This isn't done automatically.
- [ ] The version for your GWT Gradle plugin may or may not matter, but so far this has been tested mostly
  on the most recent version, `org.wisepersist:gwt-gradle-plugin:1.1.18` ;
  [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff) stores the plugin version in `gwtPluginVersion` in
  `gradle.properties`, so you can change it there to `1.1.18` if you use that project generator.


## Future work

### GWT
- [x] Move resizable browser window support into the backend, no template hassle any more
- [ ] Electron extensions
