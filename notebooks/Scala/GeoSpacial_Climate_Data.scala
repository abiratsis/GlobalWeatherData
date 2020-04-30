// Databricks notebook source
// MAGIC %md ## Update used data sources

// COMMAND ----------

// MAGIC %md #### Update /csse_covid_19_data/ repo

// COMMAND ----------

// MAGIC %sh git -C /dbfs/FileStore/data/csse_covid_19_data/ pull

// COMMAND ----------

// MAGIC %md #### Get word cities/countries from https://simplemaps.com/data/world-cities

// COMMAND ----------

// MAGIC %sh mkdir -p /dbfs/FileStore/data/world/

// COMMAND ----------

// MAGIC %sh wget -P "/dbfs/FileStore/data/world/" -N "https://simplemaps.com/static/data/world-cities/basic/simplemaps_worldcities_basicv1.6.zip"

// COMMAND ----------

// MAGIC %sh unzip -o "/dbfs/FileStore/data/world/simplemaps_worldcities_basicv1.6.zip" -d "/dbfs/FileStore/data/world/"

// COMMAND ----------

// MAGIC %md ## Use GeoSpark to join geospacial datasets

// COMMAND ----------

// MAGIC %md #### Include GeoSpark dependencies

// COMMAND ----------

 /** 
* Make sure to add the next config to your cluster:
*
* spark.kryo.registrator org.datasyslab.geospark.serde.GeoSparkKryoRegistrator
* spark.serializer org.apache.spark.serializer.KryoSerializer
**/
import org.datasyslab.geosparksql.utils.GeoSparkSQLRegistrator
import org.apache.spark.sql.functions._

GeoSparkSQLRegistrator.registerAll(spark)

// COMMAND ----------

// MAGIC %md #### Get world countries/cities from https://simplemaps.com/data/world-cities

// COMMAND ----------

val world_path = "/FileStore/data/world/worldcities.csv"

// load world data 
val world_df = spark.read
                  .option("header", "true")
                  .csv(world_path)
                  .filter($"lng".isNotNull)
                  .withColumn("lng", $"lng".cast("Decimal(24,20)"))
                  .withColumn("lat", $"lat".cast("Decimal(24,20)"))
                  .withColumn("geom", expr("ST_Point(lng, lat)"))
                  .select("city_ascii", "geom", "country", "iso3")

world_df.createOrReplaceTempView("world_tbl")

// COMMAND ----------

// MAGIC %md #### Get ISO countries with COVID-19 from https://github.com/CSSEGISandData/COVID-19

// COMMAND ----------

val iso_path = "/FileStore/data/csse_covid_19_data/UID_ISO_FIPS_LookUp_Table.csv"

// load iso covid-19 iso codes
val iso_df = spark.read
                  .option("header", "true")
                  .csv(iso_path)
                  .filter($"Long_".isNotNull)
                  .withColumn("Long_", $"Long_".cast("Decimal(24,20)"))
                  .withColumn("Lat", $"Lat".cast("Decimal(24,20)"))
                  .withColumn("geom", expr("ST_Point(Long_, Lat)"))
                  .select("iso3", "geom", "Country_Region")

iso_df.createOrReplaceTempView("iso_tbl")

// COMMAND ----------

// MAGIC %md #### Get weather data from https://github.com/CSSEGISandData/COVID-19

// COMMAND ----------

// temperature
val tmax_df = spark.read
                    .option("header", "true")
                    .csv("dbfs:/FileStore/data/weather/temp/tmax.csv")
                    .filter($"lon".isNotNull)
                    .withColumn("time", $"time".cast("timestamp"))
                    .withColumn("lon", $"lon".cast("Decimal(24,20)"))
                    .withColumn("lat", $"lat".cast("Decimal(24,20)"))
                    .withColumn("geom", expr("ST_Point(lon, lat)"))
                    .withColumn("date", $"time".cast("date"))
                    .repartition($"date")
                    .where($"date" >= "2020-04-01")
                    .select("time", "date", "geom", "tmax")  

val tmin_df = spark.read
                    .option("header", "true")
                    .csv("dbfs:/FileStore/data/weather/temp/tmin.csv")
                    .withColumn("time", $"time".cast("timestamp"))
                    .withColumn("lon", $"lon".cast("Decimal(24,20)"))
                    .withColumn("lat", $"lat".cast("Decimal(24,20)"))
                    .withColumn("geom", expr("ST_Point(lon, lat)"))
                    .withColumn("date", $"time".cast("date"))
                    .repartition($"date")
                    .where($"date" >= "2020-04-01")
                    .select("time", "date", "geom", "tmin")

tmax_df.createOrReplaceTempView("tmax_tbl")
tmin_df.createOrReplaceTempView("tmin_tbl")

// humidity
val shum_df = spark.read
                    .option("header", "true")
                    .csv("dbfs:/FileStore/data/weather/humidity/shum.csv")
                    .withColumn("time", $"time".cast("timestamp"))
                    .withColumn("lon", $"lon".cast("Decimal(24,20)"))
                    .withColumn("lat", $"lat".cast("Decimal(24,20)"))
                    .withColumn("geom", expr("ST_Point(lon, lat)"))
                    .withColumn("date", $"time".cast("date"))
                    .repartition($"date")
                    .where($"date" >= "2020-04-01")
                    .select("time", "date", "geom", "shum")

shum_df.createOrReplaceTempView("shum_tbl")

// wind
val uwnd_df = spark.read
                    .option("header", "true")
                    .csv("dbfs:/FileStore/data/weather/wind/uwnd.csv")
                    .withColumn("time", $"time".cast("timestamp"))
                    .withColumn("lon", $"lon".cast("Decimal(24,20)"))
                    .withColumn("lat", $"lat".cast("Decimal(24,20)"))
                    .withColumn("geom", expr("ST_Point(lon, lat)"))
                    .withColumn("date", $"time".cast("date"))
                    .repartition($"date")
                    .where($"date" >= "2020-04-01")
                    .select("time", "date", "geom", "uwnd")

val vwnd_df = spark.read
                    .option("header", "true")
                    .csv("dbfs:/FileStore/data/weather/wind/vwnd.csv")
                    .withColumn("time", $"time".cast("timestamp"))
                    .withColumn("lon", $"lon".cast("Decimal(24,20)"))
                    .withColumn("lat", $"lat".cast("Decimal(24,20)"))
                    .withColumn("geom", expr("ST_Point(lon, lat)"))
                    .withColumn("date", $"time".cast("date"))  
                    .repartition($"date")
                    .where($"date" >= "2020-04-01")  
                    .select("time", "date", "geom", "vwnd")

uwnd_df.createOrReplaceTempView("uwnd_tbl")
vwnd_df.createOrReplaceTempView("vwnd_tbl")

// solar
val nswrs_df = spark.read
                    .option("header", "true")
                    .csv("dbfs:/FileStore/data/weather/solar_radiation/nswrs.csv")
                    .withColumn("time", $"time".cast("timestamp"))
                    .withColumn("lon", $"lon".cast("Decimal(24,20)"))
                    .withColumn("lat", $"lat".cast("Decimal(24,20)"))
                    .withColumn("geom", expr("ST_Point(lon, lat)"))
                    .withColumn("date", $"time".cast("date"))
                    .repartition($"date")
                    .where($"date" >= "2020-04-01")
                    .select("time", "date", "geom", "nswrs")

val dswrf_df = spark.read
                    .option("header", "true")
                    .csv("dbfs:/FileStore/data/weather/solar_radiation/dswrf.csv")
                    .withColumn("time", $"time".cast("timestamp"))
                    .withColumn("lon", $"lon".cast("Decimal(24,20)"))
                    .withColumn("lat", $"lat".cast("Decimal(24,20)"))
                    .withColumn("geom", expr("ST_Point(lon, lat)"))
                    .withColumn("date", $"time".cast("date"))
                    .repartition($"date")
                    .where($"date" >= "2020-04-01")  
                    .select("time", "date", "geom", "dswrf")

val csdsf_df = spark.read
                    .option("header", "true")
                    .csv("dbfs:/FileStore/data/weather/solar_radiation/csdsf.csv")
                    .withColumn("time", $"time".cast("timestamp"))
                    .withColumn("lon", $"lon".cast("Decimal(24,20)"))
                    .withColumn("lat", $"lat".cast("Decimal(24,20)"))
                    .withColumn("geom", expr("ST_Point(lon, lat)"))
                    .withColumn("date", $"time".cast("date"))
                    .repartition($"date")
                    .where($"date" >= "2020-04-01")
                    .select("time", "date", "geom", "csdsf")

nswrs_df.createOrReplaceTempView("nswrs_tbl")
dswrf_df.createOrReplaceTempView("dswrf_tbl")
csdsf_df.createOrReplaceTempView("csdsf_tbl")

// COMMAND ----------

tmax_df.count 
// 
// 6979787

// COMMAND ----------

// MAGIC %md #### Join with covid-19 reported locations

// COMMAND ----------

// joing world_df and iso_df to retrieve locations where covid-19 cases were reported.
// Attention: this doesn't necessarily means that all the cases were identified in the specific location i.e csse_covid_19_data dataset always 
// provides coordinates of Rome to report cases for Italy. If that changes in the future and we can record cases per city this should be modified.  
val covid19_by_loc_df = spark.sql(
    """
        |SELECT world_tbl.ISO3, country, city_ascii as city, world_tbl.geom AS geom
        |FROM iso_tbl, world_tbl 
        |WHERE ST_Distance(iso_tbl.geom, world_tbl.geom) <= 1
    """.stripMargin)
       .groupBy("ISO3", "country", "city") // group by to eliminate dublicates
       .agg(first("geom").as("geom"))
       .cache
       
covid19_by_loc_df.createOrReplaceTempView("c19_loc_tbl")

// COMMAND ----------

display(covid19_by_loc_df.where($"country" === "France").dropDuplicates)

// COMMAND ----------

// MAGIC %md #### Join weather data with covid-19 reported locations

// COMMAND ----------


val maxt_df = spark.sql(
                  """
                  | SELECT 
                  |        mxt.date, ISO3, country, city, tmax
                  | FROM 
                  |     c19_loc_tbl clt,
                  |     tmax_tbl mxt
                  | WHERE 
                  |     ST_Distance(clt.geom, mxt.geom) <= 9
                  """)
.groupBy("date", "ISO3", "country", "city")
.agg(avg("tmax").as("tmax"))
.cache

val mint_df = spark.sql(
                  """
                  | SELECT 
                  |        mtt.date, ISO3, country, city, tmin
                  | FROM 
                  |     c19_loc_tbl clt,
                  |     tmin_tbl mtt
                  | WHERE 
                  |     ST_Distance(clt.geom, mtt.geom) <= 9
                  """)
.groupBy("date", "ISO3", "country", "city")
.agg(avg("tmin").as("tmin"))
.cache

val shum_df = spark.sql(
                  """
                  | SELECT 
                  |        smt.date, ISO3, country, city, shum
                  | FROM 
                  |     c19_loc_tbl clt,
                  |     shum_tbl smt
                  | WHERE 
                  |     ST_Distance(clt.geom, smt.geom) <= 9
                  """)
.groupBy("date", "ISO3", "country", "city")
.agg(avg("shum").as("shum"))
.cache

val uwnd_df = spark.sql(
                  """
                  | SELECT 
                  |        uwt.date, ISO3, country, city, uwnd
                  | FROM 
                  |     c19_loc_tbl clt,
                  |     uwnd_tbl uwt
                  | WHERE 
                  |     ST_Distance(clt.geom, uwt.geom) <= 9
                  """)
.groupBy("date", "ISO3", "country", "city")
.agg(avg("uwnd").as("uwnd"))
.cache

val weather_df = maxt_df.join(mint_df, Seq("date", "ISO3", "city"), "left")
                        .join(shum_df, Seq("date", "ISO3", "city"), "left")
                        .join(uwnd_df, Seq("date", "ISO3", "city"), "left")
                        .cache

// val weather_df = spark.sql(
//                   """
//                   | SELECT 
//                   |        clt.ISO3, country, city_ascii, clt.geom AS geom, 
//                   |        tmax, tmin, shum, uwnd, vwnd
//                   | FROM 
//                   |     covid19_loc_tbl clt, 
//                   |     tmax_tbl mxt, 
//                   |     tmin_tbl mnt, 
//                   |     shum_tbl shmt,
//                   |     uwnd_tbl uwt,
//                   |     vwnd_tbl vwt
//                   | WHERE 
//                   |     ST_Distance(clt.geom, mxt.geom) <= 20 AND
//                   |     ST_Distance(clt.geom, mnt.geom) <= 20 AND
//                   |     ST_Distance(clt.geom, shmt.geom)<= 20 AND
//                   |     ST_Distance(clt.geom, uwt.geom) <= 20 AND
//                   |     ST_Distance(clt.geom, vwt.geom) <= 20
//                   """)


// COMMAND ----------

weather_df.count

// COMMAND ----------

// display(maxt_df)

println(s"maxt_df count:${maxt_df.count}")

val iso_unique_countries = iso_df.select("Country_Region").distinct().collect().map{_.getString(0)}.toSet
val join_unique_countries = maxt_df.select("country").distinct().collect().map{_.getString(0)}.toSet

val diff = iso_unique_countries -- join_unique_countries
println("count diff:" + diff.size)

println(diff)

// COMMAND ----------

display(weather_df)

// COMMAND ----------


