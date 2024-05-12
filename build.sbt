showCurrentGitBranch

inThisBuild(Seq(
  organization := "org.hathitrust.htrc",
  organizationName := "HathiTrust Research Center",
  organizationHomepage := Some(url("https://www.hathitrust.org/htrc")),
  scalaVersion := "2.13.14",
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
  ),
  dynverSonatypeSnapshots := true,
  versionScheme := Some("semver-spec"),
  crossScalaVersions := Seq("2.13.14", "2.12.18")
))

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
  Keys.`package` := (Compile / Keys.`package` dependsOn Test / test).value,
  credentials += Credentials(
    "Sonatype Nexus Repository Manager", // realm
    "nexus.htrc.illinois.edu", // host
    "drhtrc", // user
    sys.env.getOrElse("HTRC_NEXUS_DRHTRC_PWD", "abc123") // password
  )
)

lazy val ammoniteSettings = Seq(
  libraryDependencies +=
    {
      val version = scalaBinaryVersion.value match {
        case "2.10" => "1.0.3"
        case "2.11" => "1.6.7"
        case _ ⇒  "3.0.0-M1-24-26133e66"
      }
      "com.lihaoyi" % "ammonite" % version % Test cross CrossVersion.full
    },
  Test / sourceGenerators += Def.task {
    val file = (Test / sourceManaged).value / "amm.scala"
    IO.write(file, """object amm extends App { ammonite.AmmoniteMain.main(args) }""")
    Seq(file)
  }.taskValue,
  connectInput := true,
  outputStrategy := Some(StdoutOutput)
)

lazy val `feature-extractor` = (project in file("."))
  .enablePlugins(GitBranchPrompt)
  .settings(publishSettings)
  .settings(ammoniteSettings)
  .settings(
    name := "feature-extractor",
    description := "Extracts a set of features (such as ngram counts, POS tags, etc.) from a " +
      "corpus for aiding in conducting 'distant-reading' (aka non-consumptive) research",
    licenses += "Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0"),
    libraryDependencies ++= Seq(
      "org.hathitrust.htrc"           %% "data-model"               % "2.14.1",
      "org.hathitrust.htrc"           %% "scala-utils"              % "2.14.4",
      "org.scala-lang.modules"        %% "scala-collection-compat"  % "2.12.0",
      "edu.stanford.nlp"              %  "stanford-corenlp"         % "4.5.7",
      "edu.stanford.nlp"              %  "stanford-corenlp"         % "4.5.7"
        classifier "models"
        classifier "models-arabic"
        classifier "models-chinese"
        classifier "models-english"
        classifier "models-french"
        classifier "models-german"
        classifier "models-spanish",
      "com.optimaize.languagedetector" % "language-detector"        % "0.6",
      "org.slf4j"                     %  "slf4j-api"                % "2.0.12",
      "commons-codec"                 %  "commons-codec"            % "1.17.0",
      "org.slf4j"                     %  "slf4j-simple"             % "2.0.13"    % Test,
      "org.scalacheck"                %% "scalacheck"               % "1.18.0"    % Test,
      "org.scalatest"                 %% "scalatest"                % "3.2.18"    % Test,
      "org.scalatestplus"             %% "scalacheck-1-15"          % "3.2.11.0"  % Test
    ),
    dependencyOverrides ++= Seq(
      "junit" % "junit" % "4.13.2"  // to fix Snyk security report
    )
  )
