[![Scala CI](https://github.com/htrc/TDM-Lib-FeatureExtractor/actions/workflows/ci.yml/badge.svg)](https://github.com/htrc/TDM-Lib-FeatureExtractor/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/htrc/TDM-Lib-FeatureExtractor/branch/develop/graph/badge.svg?token=ZFB6X3AKGV)](https://codecov.io/gh/htrc/TDM-Lib-FeatureExtractor)
[![GitHub release (latest SemVer including pre-releases)](https://img.shields.io/github/v/release/htrc/TDM-Lib-FeatureExtractor?include_prereleases&sort=semver)](https://github.com/htrc/TDM-Lib-FeatureExtractor/releases/latest)

# TDM-Feature-Extractor

# Build
`sbt "+package"`

then find the result in `target/scala-2.13/` (or `target/scala-2.12/`) folder.

# Publish to HTRC Nexus
`sbt "+publish"`

# Usage
## SBT
`libraryDependencies += "org.hathitrust.htrc" %% "feature-extractor" % VERSION`

## Maven

### Scala 2.13
```
<dependency>
    <groupId>org.hathitrust.htrc</groupId>
    <artifactId>feature-extractor_2.13</artifactId>
    <version>VERSION</version>
</dependency>
```

### Scala 2.12
```
<dependency>
    <groupId>org.hathitrust.htrc</groupId>
    <artifactId>feature-extractor_2.12</artifactId>
    <version>VERSION</version>
</dependency>
```

