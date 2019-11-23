# TDM-Feature-Extractor

# Build
`sbt "+package"`

then find the result in `target/scala-2.12/` and `target/scala-2.11/` folders.

# Publish to HTRC Nexus
`sbt "+publish"`

# Usage
## SBT
`libraryDependencies += "tdm" %% "feature-extractor" % "2.6.1"`

## Maven

### Scala 2.12
```
<dependency>
    <groupId>tdm</groupId>
    <artifactId>feature-extractor_2.12</artifactId>
    <version>2.6.1</version>
</dependency>
```

### Scala 2.11
```
<dependency>
    <groupId>tdm</groupId>
    <artifactId>feature-extractor_2.11</artifactId>
    <version>2.6.1</version>
</dependency>
```

