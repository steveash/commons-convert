# commons-convert
This is a fork of the unreleased sandbox project: [Apache Commons Convert](https://commons.apache.org/sandbox/commons-convert/index.html)

My code forked theirs since that sandbox looked dead, and I made the following changes:
 * Requires Java 1.6
 * Uses unchecked exceptions over checked exceptions
 * Minor API improvements for useability for certain use cases (see below)
 * Minor performance improvements (concurrent collections over synchronized collections)

If you want to use my mirror that I've released as a 0.90.0 version then use the maven coordinates:

```xml
<dependency>
  <groupId>com.github.steveash.commons</groupId>
  <artifactId>commons-convert</artifactId>
  <version>0.90.0</version>
</dependency>
```