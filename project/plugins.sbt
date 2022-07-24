logLevel := Level.Warn

addSbtPlugin("com.typesafe.sbt"       % "sbt-git"             % "1.0.2")
addSbtPlugin("com.github.sbt"         % "sbt-native-packager" % "1.9.7")
addSbtPlugin("com.eed3si9n"           % "sbt-assembly"        % "1.1.0")
addSbtPlugin("org.wartremover"        % "sbt-wartremover"     % "2.4.16")
addSbtPlugin("com.github.sbt"         % "sbt-pgp"             % "2.1.2")
addSbtPlugin("com.dwijnand"           % "sbt-dynver"          % "4.1.1")
addSbtPlugin("org.scoverage"          % "sbt-scoverage"       % "2.0.0")
