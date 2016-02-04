# Updatifier

Updatifier is a simple Sponge plugin that allows other plugins to easily check if they have an update available.

###Server Administrators

To install Updatifier on your Sponge server, simply move the JAR into your `mods` folder. Plugin developers need to add support for Updatifier to their plugins in order for Updatifier to check for updates to that plugin. Updatifier is not required, meaning if you download a plugin that has support for Updatifier, and you do not have Updatifier installed, no errors will be thrown to the console.

###Plugin Developers

It is extremely simple to add Updatifier support into your plugin. There are two simple steps to follow:

1. Add Updatifier as a Maven or Gradle dependency.
2. Annotate your main plugin class and add Updatifier as an `after` dependency.

#####Adding Updatifier as a Maven/Gradle Dependency

Updatifier uses [**JitPack**](https://jitpack.io/) to allow you to add Updatifier as a Maven/Gradle dependency. To get started you need to add JitPack as a repository in your pom.xml:

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

Next you will want to add Updatifer as a dependency:

```xml
<dependency>
    <groupId>com.github.Flibio</groupId>
    <artifactId>EconomyLite</artifactId>
    <version>RELEASE</version>
</dependency>
```

The steps are the essentially the same for Gradle, with just a few formatting changes.

#####Annotating Your Main Plugin Class

In order for Updatifier to check for updates to your plugin, it needs to know some information about your plugin. To do that, you must annotate your main plugin class with `@Updatifier`, similar to how you annotate for Sponge with `@Plugin`. You also must add Updatifier as an `after` dependency. An example can be seen below.

```java
@Updatifier(repoName = "EconomyLite", repoOwner = "Flibio", version = "v1.1.0")
@Plugin(id = "EconomyLite", name = "EconomyLite", version = "1.1.0", dependencies = "after: Updatifier")
public class MyClass {

}
```

Updatifier is not required, meaning if you add support for Updatifier, and a server owner does not have Updatifier installed, no errors will be thrown to the console. The Updatifier annotation will simply be ignored.