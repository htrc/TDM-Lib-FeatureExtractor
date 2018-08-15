# TDM-Feature-Extractor

# Build
`sbt "+package"`

then find the result in `target/scala-2.12/` and `target/scala-2.11/` folders.

# Publish to HTRC Nexus
`sbt "+publish"`

# Usage
## SBT
`libraryDependencies += "org.hathitrust.htrc" %% "tdm-feature-extractor" % "2.0"`

## Maven

### Scala 2.12
```
<dependency>
    <groupId>org.hathitrust.htrc</groupId>
    <artifactId>tdm-feature-extractor_2.12</artifactId>
    <version>2.0</version>
</dependency>
```

### Scala 2.11
```
<dependency>
    <groupId>org.hathitrust.htrc</groupId>
    <artifactId>tdm-feature-extractor_2.11</artifactId>
    <version>2.0</version>
</dependency>
```

