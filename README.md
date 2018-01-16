# Universal File Transcoder [![Travis CI](https://travis-ci.org/marco-schmidt/ufxcoder.svg?branch=master)](https://travis-ci.org/marco-schmidt/ufxcoder)

This library (and command line application) detects, checks and transcodes various file formats.
It parses files, decompresses data, creates checksums, validates standard conformance and reports irregularities. 
This helps identifying problematic files among large data sets.
Such files can then be processed in different ways: moved, copied, deleted, renamed, transcoded, in some cases repaired.

Some more properties:
* The project is written in Java and compatible with version 8 (see section [Java Compatibility](#java-compatibility)).
* [Gradle](https://gradle.org/) is the project build tool.
* It relies on libraries [slf4j](https://www.slf4j.org) and [logback](https://logback.qos.ch) as dependencies for logging.
* Unit tests are included, they depend on [JUnit 4](http://junit.org/junit4/).
* All contents are distributed under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0).
* The [maven standard directory layout convention](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html) is followed.
* Code is maintained in a [git repository at GitHub](https://github.com/marco-schmidt/ufxcoder).
* Continuous integration: [Travis CI rebuilds the project on each commit.](https://travis-ci.org/marco-schmidt/ufxcoder)
* Code analysis is done with gradle plugins for [PMD](https://pmd.github.io), [spotbugs](https://spotbugs.github.io) and [checkstyle](http://checkstyle.sourceforge.net).
* Source code is formatted with [spotless](https://github.com/diffplug/spotless) using an [Eclipse XML configuration file](config/eclipse/formatter.xml).
* Eclipse configuration files can be [generated with gradle](https://docs.gradle.org/current/userguide/eclipse_plugin.html).

## Status Quo

As of December 2017, the project is in its early stages.
Only TIFF, DNG, CR2 and JPEG files are being supported.
Transcoding has not been implemented yet, and a lot of checks are missing.
The software is in an alpha stage and unsuitable for a production environment.

## User Guide

This section is a quick tour of how the command line application can be used.

Assuming you have created an installation as described under
Development Setup there should be a directory ```build/installation/bin``` with scripts ```ufxcoder``` (Unix, Mac OS X)
and ```ufxcoder.bat``` (Windows) in them.
In a shell, change to that directory and run that script without additional arguments,
it should print out a help screen:

```
> ./ufxcoder
```

Give it the path to a single file to have that file checked:
```
> ./ufxcoder /Users/jk/Pictures/2017/DSC_4938.JPG
/Users/jk/Pictures/2017/DSC_4938.JPG	JPEG	OK	
Processed 1 file(s) in 0 second(s).
```

This just prints, separated with tabs for easier automated processing, file name, detected format and the check's result (OK, warning, error).
If there is a warning or error, its text will be visible as well:
```
> ./ufxcoder /Users/jk/Pictures/2017/DSC_4939.JPG
/Users/jk/Pictures/2017/DSC_4939.JPG	JPEG	Error Invalid quantization table destination selector 255.	
Processed 1 file(s) in 0 second(s).
```

If you want a directory tree of files checked recursively, just add the path to the directory (you can name multiple files and directories):
```
> ./ufxcoder /Users/jk/Pictures
... one file per line
Processed 2,304 file(s) in 27 second(s).
```

If your directory tree has many different files, chances are that a lot of them are in an unsupported format.
This creates a lot of clutter in the output.
To only examine files with file name extensions typical for supported formats, use switch `-k`:
```
> ./ufxcoder -k /Users/jk/Pictures
... one line per examined file
Processed 1,694 file(s) in 23 second(s).
```

You may only want to learn about problematic files. Use the quiet switch `-q` to remove information about files that were found to be `OK`:
```
> ./ufxcoder -k  -q /Users/jk/Pictures
... one line per problematic file
Processed 1,694 file(s) in 23 second(s).
```

To get more information about those problematic files change the log level to debug with switch ```-l debug```:
```
> ./ufxcoder -k  -q -l debug /Users/jk/Pictures
...
Processed 1,694 file(s) in 23 second(s).
```

Some switches are relevant only if a file is in a certain format. To check if a TIFF file is in conformance with the TIFF baseline specification, use `--tiff:baseline`. Note that this is a valid TIFF file which would have a result of OK with a regular check. It just does not match the more strict baseline requirements.
```
> ./ufxcoder --tiff:baseline 20170805-181749_defl.tif
20170805-181749_defl.tif	TIFF	Error	[1/2] Field not allowed in a baseline TIFF file (317, Predictor). [2/2] Compression type not allowed in a baseline TIFF file (8, Deflate).
Processed 1 file(s) in 0 second(s).
```

## Development Setup

You will need a version 8 JDK and version control management software git.

Create a copy of the project.
In a shell (prompt, terminal) go to a directory where you store software projects and
run this command:
```
> git clone https://github.com/marco-schmidt/ufxcoder.git
```

Alternatively, download an [archive of the current state of the master branch.](https://github.com/marco-schmidt/ufxcoder/archive/master.zip)

The project uses [gradle](https://gradle.org/) as its build tool.
Gradle is included in form of gradlew, the gradle wrapper, a small Java application.
Calling the appropriate wrapper script for your operating system will run
all the default tasks (first line for Unix or Mac OS X, second line for Windows):
```
> ./gradlew
> gradlew
```

If this call is successful you will also have gotten an installation version that you can run like this:

```
> build/install/ufxcoder/bin/ufxcoder
> build\install\ufxcoder\bin\ufxcoder.bat
```

You can also generate configuration files for the [Eclipse IDE](https://www.eclipse.org/):

```
> ./gradlew eclipse
```

In Eclipse, choose `File / Import...`, then `General / Existing Projects into Workspace`.
As root directory pick the main project directory.

## Java Compatibility

Java usually comes in two forms, as a Java Runtime Environment (JRE) and a
Java Development Kit (JDK). A JDK includes a JRE, plus some tools like a compiler.
To simply run a Java application, the JRE is enough, to compile a runnable version
or modify the existing one, a JDK is required.

Both JRE and JDK are available from Oracle or may be already included with your
operating system.

```
java -version
javac -version 
```
These should report back the exact version of the Java in your path.
If java works but not javac, you only have a JRE, not a JDK.

If multiple Java versions are installed on a system, a version 8 JDK must be made available. This is done with the environment variable `JAVA_HOME`. In some cases, variable `PATH` must be set as well.

1) Windows. These variables can be permanently changed with an Environment Variables dialog in the Windows control panel.

    ```
    set JAVA_HOME=C:\Program Files\jdk1.8.0_144
    set PATH=%JAVA_HOME%\bin;%PATH%
    ```

2) Unix. This change can be made permanent in a file like `~/.profile`:

    ```
    export JAVA_HOME=/opt/jdk1.8.0_144
    export PATH=$PATH/bin:$PATH
    ```

3) Mac OS X. A tool java_home returns the newest version within a given major version (if multiple 1.8 JDKs are installed). For a permanent change, a configuration file can be edited as described for Unix above. There is no need to adjust the `PATH` variable.

    ```
    export JAVA_HOME=`/usr/libexec/java_home -v 1.8`
    ```

### Java 8

The project requires at least Java 8. The version is defined in [build.gradle](build.gradle):
```gradle
tasks.withType(JavaCompile) {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = sourceCompatibility
  // more
}
```

### Java 9

The installation version of ufxcoder created with Java 8 can be run with a higher
version Java runtime environment like Java 9 because of Java's commitment to
backward compatibility.

However, building with Java 9 brings out some problems (as of December 2017).

1) The build uses the Gradle spotless plugin to format source code.
Its removeUnusedImports feature does not work because spotless dependency
google-format-java in its latest released version still has
issues with Java 9: https://github.com/diffplug/spotless/issues/83

2) The build also relies on code analysis toolkit PMD. The Gradle PMD plugin does not work
with Java 9 because PMD dependency ASM is not yet Java 9 bytecode-compatible:
https://github.com/gradle/gradle/issues/3519
