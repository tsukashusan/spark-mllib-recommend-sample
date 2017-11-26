import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  lazy val spakCore = "org.apache.spark" % "spark-core_2.11" % "2.1.1.2.6.2.3-1" % "provided" exclude("org.apache.zookeeper", "zookeeper")
  lazy val spakSQL = "org.apache.spark" % "spark-sql_2.11" % "2.1.1.2.6.2.3-1" % "provided" exclude("org.apache.zookeeper", "zookeeper")
  lazy val spakStreaming = "org.apache.spark" % "spark-streaming_2.11" % "2.1.1.2.6.2.3-1" % "provided" exclude("org.apache.zookeeper", "zookeeper")
  lazy val spakMlib = "org.apache.spark" % "spark-mllib_2.11" % "2.1.1.2.6.2.3-1" % "provided" exclude("org.apache.zookeeper", "zookeeper")
}
