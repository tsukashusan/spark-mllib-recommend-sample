package example

import org.apache.log4j.{Level, Logger}

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.recommendation.ALS

case class Rating(userId: Int, movieId: Int, rating: Float, timestamp: Long)

object RecommendationExample extends Greeting with App {
    val rootLogger = Logger.getRootLogger().setLevel(Level.ERROR)
    println(greeting)
    sparkmlTest()

    def sparkmlTest() : Unit = {
        val spark = SparkSession.builder.appName("CollaborativeFilteringExample").getOrCreate()
        //spark.sparkContext.setLogLevel("OFF")
        import spark.implicits._
        val ratings = spark.read.options(Map("header" ->"true",
                                            "inferSchema" -> "true"))
                                 .csv("/example/data/ml-latest/ratings.csv")
        val Array(training, test) = ratings.randomSplit(Array(0.8, 0.2))
        training.printSchema()
        training.show(5)
        // Build the recommendation model using ALS on the training data
        val als = new ALS()
          .setMaxIter(5)
          .setRegParam(0.01)
          .setUserCol("userId")
          .setItemCol("movieId")
          .setRatingCol("rating")
        val model = als.fit(training)
        
        // Evaluate the model by computing the RMSE on the test data
        val predictions = model.transform(test).na.drop(Array("prediction"))
        println(s"☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆") 
        predictions.printSchema()
        predictions.show(5)
        println(s"☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆") 
        val evaluator = new RegressionEvaluator()
          .setMetricName("rmse")
          .setLabelCol("rating")
          .setPredictionCol("prediction")
        val rmse = evaluator.evaluate(predictions)
        println(rmse)
        println(s"★★★★★★★★★★★★★★★★★★★Root-mean-square error = $rmse ★★★★★★★★★★★★★★★★★★★")
        spark.stop()
    }
}
trait Greeting {
  lazy val greeting: String = "hello"
}
