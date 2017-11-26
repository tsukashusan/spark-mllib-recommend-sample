package example

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Dataset
import org.apache.spark.rdd.RDD

import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel
import org.apache.spark.mllib.recommendation.Rating

object RecommendationExample extends Greeting with App {
  sparkLoadModel()
  def sparkCreteModel(): Unit = {
    val spark = SparkSession.builder.appName("CollaborativeFilteringExample").getOrCreate()
    import spark.implicits._
    //val sc = spark.sparkContext

    // $example on$
    // Load and parse the data
    // val data = sc.textFile("/example/data/ml-latest/input/ratings.csv")
    //val ratings = data.map(_ match { case Array(user, item, rate, timestamp) =>
    //  Rating(user.toInt, item.toInt, rate.toDouble)
    //})
    // Build the recommendation model using ALS
    val ratings: RDD[Rating] = getRatings(spark, "/example/data/ml-latest/input/ratings.csv")
    val rank = 10
    val numIterations = 10
    val model: MatrixFactorizationModel = ALS.train(ratings, rank, numIterations, 0.01)

    // Evaluate the model on rating data
    val usersProducts = ratings.map { case Rating(user, product, rate) =>
      (user, product)
    }
    val predictions =
      model.predict(usersProducts).map { case Rating(user, product, rate) =>
        ((user, product), rate)
      }
    val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>
      ((user, product), rate)
    }.join(predictions)
    val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
      val err = (r1 - r2)
      err * err
    }.mean()
    println("Mean Squared Error = " + MSE)

    // Save and load model
    val sc: SparkContext = spark.sparkContext
    model.save(sc, "/example/data/ml-latest/output/myCollaborativeFilter")
    //val sameModel = MatrixFactorizationModel.load(sc, "/example/data/ml-latest/output/myCollaborativeFilter")
    // $example off$

    spark.stop()
  }

  def sparkLoadModel(): Unit = {
    val spark = SparkSession.builder.appName("CollaborativeFilteringExample").getOrCreate()
    import spark.implicits._
    val ratings: RDD[Rating] = getRatings(spark, "/example/data/ml-latest/input/ratings.csv")
    val sc: SparkContext = spark.sparkContext
    val model: MatrixFactorizationModel = MatrixFactorizationModel.load(sc, "/example/data/ml-latest/output/myCollaborativeFilter")


    // Evaluate the model on rating data
    val usersProducts = ratings.map { case Rating(user, product, rate) =>
      (user, product)
    }
    val predictions =
      model.predict(usersProducts).map { case Rating(user, product, rate) =>
        ((user, product), rate)
      }
    val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>
      ((user, product), rate)
    }.join(predictions)
    val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
      val err = (r1 - r2)
      err * err
    }.mean()
    println("Mean Squared Error = " + MSE)
    // $example off$

    spark.stop()
  }

  def getRatings(spark: SparkSession, csvPath: String): RDD[Rating] = {
    val df: DataFrame = spark.read.option("header","true").csv(csvPath)
    import spark.implicits._
    val ds: Dataset[Rating] = df.map(row => {
      val user: Int = row.getString(0).toInt
      val item: Int  = row.getString(1).toInt
      val rate: Double = row.getString(2).toDouble
      Rating(user, item, rate)
    })
    val ratings: RDD[Rating] = ds.rdd
    return ratings
  }
}

trait Greeting {
  lazy val greeting: String = "hello"
}
