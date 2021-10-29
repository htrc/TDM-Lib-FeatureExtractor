showCurrentGitBranch

git.useGitDescribe := true

lazy val commonSettings = Seq(
  organization := "org.hathitrust.htrc",
  organizationName := "HathiTrust Research Center",
  organizationHomepage := Some(url("https://www.hathitrust.org/htrc")),
  scalaVersion := "2.13.6",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:postfixOps",
    "-language:implicitConversions"
  ),
  resolvers ++= Seq(
    Resolver.mavenLocal,
    "HTRC Nexus Repository" at "https://nexus.htrc.illinois.edu/repository/maven-public"
  ),
  externalResolvers := Resolver.combineDefaultResolvers(resolvers.value.toVector, mavenCentral = false),
  Compile / packageBin / packageOptions += Package.ManifestAttributes(
    ("Git-Sha", git.gitHeadCommit.value.getOrElse("N/A")),
    ("Git-Branch", git.gitCurrentBranch.value),
    ("Git-Version", git.gitDescribedVersion.value.getOrElse("N/A")),
    ("Git-Dirty", git.gitUncommittedChanges.value.toString),
    ("Build-Date", new java.util.Date().toString)
  )
)

lazy val wartRemoverSettings = Seq(
  Compile / compile / wartremoverWarnings ++= Warts.unsafe.diff(Seq(
    Wart.DefaultArguments,
    Wart.NonUnitStatements,
    Wart.StringPlusAny,
    Wart.Return,
    Wart.Null
  ))
)

lazy val publishSettings = Seq(
  publishTo := {
    val nexus = "https://nexus.htrc.illinois.edu/"
    if (isSnapshot.value)
      Some("HTRC Snapshots Repository" at nexus + "repository/snapshots")
    else
      Some("HTRC Releases Repository"  at nexus + "repository/releases")
  },
  // force to run 'test' before 'package' and 'publish' tasks
  publish := (publish dependsOn Test / test).value,
  Keys.`package` := (Compile / Keys.`package` dependsOn Test / test).value
)

lazy val ammoniteSettings = Seq(
  libraryDependencies +=
    {
      val version = scalaBinaryVersion.value match {
        case "2.10" => "1.0.3"
        case _ ⇒  "2.4.0-23-76673f7f"
      }
      "com.lihaoyi" % "ammonite" % version % Test cross CrossVersion.full
    },
  Test / sourceGenerators += Def.task {
    val file = (Test / sourceManaged).value / "amm.scala"
    IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
    Seq(file)
  }.taskValue,
  connectInput := true,
  outputStrategy := Some(StdoutOutput)
)

lazy val `feature-extractor` = (project in file("."))
  .enablePlugins(GitVersioning, GitBranchPrompt)
  .settings(commonSettings)
  .settings(wartRemoverSettings)
  .settings(publishSettings)
  .settings(ammoniteSettings)
  .settings(
    name := "feature-extractor",
    description := "Extracts a set of features (such as ngram counts, POS tags, etc.) from a " +
      "corpus for aiding in conducting 'distant-reading' (aka non-consumptive) research",
    licenses += "Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0"),
    libraryDependencies ++= Seq(
      "org.hathitrust.htrc"           %% "data-model"               % "2.13",
      "org.hathitrust.htrc"           %% "scala-utils"              % "2.13",
      "org.scala-lang.modules"        %% "scala-collection-compat"  % "2.5.0",
      "edu.stanford.nlp"              %  "stanford-corenlp"         % "4.3.1",
      "edu.stanford.nlp"              %  "stanford-corenlp"         % "4.3.1"
        classifier "models"
        classifier "models-arabic"
        classifier "models-chinese"
        classifier "models-english"
        classifier "models-french"
        classifier "models-german"
        classifier "models-spanish",
      "com.optimaize.languagedetector" % "language-detector"        % "0.6",
      "org.slf4j"                     %  "slf4j-api"                % "1.7.32",
      "commons-codec"                 %  "commons-codec"            % "1.15",
      "org.slf4j"                     %  "slf4j-simple"             % "1.7.32"  % Test,
      "org.scalacheck"                %% "scalacheck"               % "1.15.4"  % Test,
      "org.scalatest"                 %% "scalatest"                % "3.2.10"  % Test,
      "org.scalatestplus"             %% "scalacheck-1-15"          % "3.2.9.0" % Test
    ),
    ThisBuild / versionScheme := Some("semver-spec"),
    crossScalaVersions := Seq("2.13.6", "2.12.15")
  )
