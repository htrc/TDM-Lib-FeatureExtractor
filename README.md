# TDM-Feature-Extractor

# Build
`sbt "+package"`

then find the result in `target/scala-2.13/` (or `target/scala-2.12/`) folder.

# Publish to HTRC Nexus
`sbt "+publish"`

# Usage
## SBT
`libraryDependencies += "org.hathitrust.htrc" %% "feature-extractor" % "3.0"`

## Maven

### Scala 2.13
```
<dependency>
    <groupId>org.hathitrust.htrc</groupId>
    <artifactId>feature-extractor_2.13</artifactId>
    <version>3.0</version>
</dependency>
```

### Scala 2.12
```
<dependency>
    <groupId>org.hathitrust.htrc</groupId>
    <artifactId>feature-extractor_2.12</artifactId>
    <version>3.0</version>
</dependency>
```

