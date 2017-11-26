import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  lazy val spakCore = "org.apache.spark" %% "spark-core" % "2.1.1.2.6.2.3-1" % "provided" exclude("org.apache.zookeeper", "zookeeper")
  lazy val spakSQL = "org.apache.spark" %% "spark-sql" % "2.1.1.2.6.2.3-1" % "provided" exclude("org.apache.zookeeper", "zookeeper")
  lazy val spakStreaming = "org.apache.spark" %% "spark-streaming" % "2.1.1.2.6.2.3-1" % "provided" exclude("org.apache.zookeeper", "zookeeper")
  lazy val spakMlib = "org.apache.spark" %%  "spark-mllib" % "2.1.1.2.6.2.3-1" % "provided" exclude("org.apache.zookeeper", "zookeeper")
}
