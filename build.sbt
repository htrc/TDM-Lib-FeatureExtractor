import com.typesafe.sbt.{GitBranchPrompt, GitVersioning}

showCurrentGitBranch

git.useGitDescribe := true

lazy val commonSettings = Seq(
  organization := "tdm",
  organizationName := "Text and Data Mining (TDM) initiative involving HathiTrust/HTRC, JSTOR, and Portico",
  scalaVersion := "2.12.9",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:postfixOps",
    "-language:implicitConversions"
  ),
  externalResolvers := Seq(
    Resolver.defaultLocal,
    Resolver.mavenLocal,
    "HTRC Nexus Repository" at "http://nexus.htrc.illinois.edu/content/groups/public",
  ),
  packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
    ("Git-Sha", git.gitHeadCommit.value.getOrElse("N/A")),
    ("Git-Branch", git.gitCurrentBranch.value),
    ("Git-Version", git.gitDescribedVersion.value.getOrElse("N/A")),
    ("Git-Dirty", git.gitUncommittedChanges.value.toString),
    ("Build-Date", new java.util.Date().toString)
  ),
//  wartremoverErrors ++= Warts.unsafe.diff(Seq(
//    Wart.DefaultArguments,
//    Wart.NonUnitStatements,
//    Wart.Any,
//    Wart.TryPartial
//  )),
  publishTo := {
    val nexus = "https://nexus.htrc.illinois.edu/"
    if (isSnapshot.value)
      Some("HTRC Snapshots Repository" at nexus + "content/repositories/snapshots")
    else
      Some("HTRC Releases Repository"  at nexus + "content/repositories/releases")
  },
  // force to run 'test' before 'package' and 'publish' tasks
  publish := (publish dependsOn Test / test).value,
  Keys.`package` := (Compile / Keys.`package` dependsOn Test / test).value
)

lazy val `feature-extractor` = (project in file("."))
  .enablePlugins(GitVersioning, GitBranchPrompt)
  .settings(commonSettings)
  .settings(
    name := "feature-extractor",
    description := "Extracts a set of features (such as ngram counts, POS tags, etc.) from a " +
      "corpus for aiding in conducting 'distant-reading' (aka non-consumptive) research",
    licenses += "Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0"),
    libraryDependencies ++= Seq(
      "org.hathitrust.htrc"           %% "data-model"           % "1.5",
      "org.hathitrust.htrc"           %% "scala-utils"          % "2.8",
      "edu.stanford.nlp"              %  "stanford-corenlp"     % "3.9.2",
      "edu.stanford.nlp"              %  "stanford-corenlp"     % "3.9.2"
        classifier "models"
        classifier "models-arabic"
        classifier "models-chinese"
        classifier "models-english"
        classifier "models-french"
        classifier "models-german"
        classifier "models-spanish",
      "com.optimaize.languagedetector" % "language-detector"    % "0.6",
      "org.slf4j"                     %  "slf4j-api"            % "1.7.28",
      "commons-codec"                 %  "commons-codec"        % "1.13",
      "org.scalacheck"                %% "scalacheck"           % "1.14.0"      % Test,
      "org.scalatest"                 %% "scalatest"            % "3.0.8"       % Test
    ),
    crossScalaVersions := Seq("2.12.9", "2.11.12")
  )
