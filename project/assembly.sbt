// import sbtassembly.AssemblyPlugin.autoImport._
// import sbtassembly.MergeStrategy

// ThisBuild / assemblyMergeStrategy := {
//   case PathList("META-INF", xs @ _*) =>
//     xs.map(_.toLowerCase) match {
//       // drop metadata/signature files we don’t care about
//       case Seq("manifest.mf") | Seq("index.list") | Seq("dependencies") |
//            Seq("license") | Seq("notice") | Seq("readme") |
//            Seq("mailcap") | Seq("services") =>
//         MergeStrategy.discard
//       case _ => MergeStrategy.first
//     }

//   // Java 9+ module-info (conflicts across many libs)
//   case "module-info.class" => MergeStrategy.discard

//   // Netty duplicates
//   case "META-INF/io.netty.versions.properties" => MergeStrategy.first

//   // Jackson service loader files
//   case "META-INF/services/com.fasterxml.jackson.core.JsonFactory" => MergeStrategy.first
//   case "META-INF/services/com.fasterxml.jackson.core.ObjectCodec" => MergeStrategy.first

//   // Logback & SLF4J service loaders
//   case "META-INF/services/ch.qos.logback.classic.spi.Configurator" => MergeStrategy.first
//   case "META-INF/services/org.slf4j.spi.SLF4JServiceProvider"      => MergeStrategy.first

//   // Auth0 / dotenv “versions/9/module-info.class”
//   case PathList("META-INF", "versions", _ @ _*) => MergeStrategy.first

//   // Default fallback
//   case _ => MergeStrategy.first
// }
