# GWeather
A command line tool/library that helps users to work with climate data from all over the world.

### Description

GWeather it is a tool for retrieving, exporting and viewing world weather data. The application provides world climate data
for wind, temperature, humidity and solar radiation.

Currently, GWeather supports the following features:
   1. Download climate datasets from [PSL](https://psl.noaa.gov/)
   2. Download cities/countries dataset from [simplemaps.com](https://simplemaps.com/data/world-cities)
   3. Merge/join above datasets by location's coordinates
   4. Export merged data

### Purpose

Recently I worked on a project for analyzing climate geo-spacial data. Quickly I realized that I was not able
to find an open-source tool which combines reliable weather data with accurate geographical locations. 
Most of the existing datasets provide the weather data using numeric coordinates (lon, lat) and not the actual
text representation of the location e.g `city/country`. The main purpose of the project, is to join the weather dataset together 
with the locations' dataset in order to create a readable dataset, which will be easier to analyze and process. Furthermore,
the application should support exporting data in different popular formats e.g CSV, parquet, delta-lake, etc.

I came up with GWeather having in mind the next goals:
 - Find a source which provides reliable, frequent and recent climate data 
 - Try to cover as many locations as possible all over the planet
 - Find proper tools/libraries that support joining geo-spacial data
 - Calculate efficiently geo-spacial operations
 - Provide a friendly Scala based API
 - Provide a friendly command line interface

### Usage
 
#### Prerequisites

Before using GWeather please make sure you have the next packages installed on your machine:
- Java JRE 8 or later
- Python3 
- pip3 

#### Installation

1. Download [gweather_v0.1.0-alpha.zip](https://github.com/abiratsis/GlobalWeatherData/releases/download/v0.1.0-alpha/gweather_v0.1.0-alpha.zip)
2. Unzip gweather_v0.1.0-alpha.zip.
3. Grant execute permission to `gweather.sh` with `chmod +x gweather.sh`
4. Execute the program `./gweather.sh [options]`

#### Configuration

Users can set program's settings via the command line (`--input-mode c`) or through a config file (`--input-mode f`).
In both cases the following settings should be determined:


| Argument            | Short | Required          | Default      | Description |
|:--------------------| ----- |-------------------| ------------- |  ------------ |
| inputMode           | -m   | yes               | -           | Where to get configuration from. Valid options are [c <command_line>, f <config_file>] |
| outputDir           | -o   | yes (in cmd mode only) | -      | The output directory where the weather datasources will be exported |
| geoSparkDistance    | -d   | no                | 1           | The distance between 2 GeoSpark points |
| mergeWinds          | -w   | no                | true        | A flag indicating whether winds speeds should be merged into one|
| mergeTemp           | -t   | no                | true        | A flag indicating whether min/max temperatures should be merged into one |
| exportFormat        | -f   | no                | parquet     | Type of exported data, it should be one of [delta, orc, parquet, csv] |
| temperatureScale    | -l   | no                | C           | Temperature scale, it should be one of [C <celcius>, F <farenheit>] |
| numericType         | -n   | no                | double      | The numeric type for CDF columns, it should be one of [double, float] |
| activeSources       | -a   | no                | (see below) | The data sources that should be exported by the program |
| startAt             | -s   | no                | 1           | The step the process should start from. The available steps are: install prerequisites(1), download data(2), convert to CSV(3), export(4) |


These data sources will be downloaded by default: `airTemperature, skinTemperature, minTemperature, maxTemperature, humidity, uwind, vwind, 
clearSkyDownwardSolar, netShortwaveRadiation`

*Example*: load configuration from file:
```commandline
gweather -m f --user-conf <conf_file>
```

*Example*: determine settings via the command line:
```commandline
gweather -m c -r /tmp/data/ -d 1 -f "csv" ...
```

**Attention:** when using command line mode, if any of the previous arguments is not specified gweather will use their default values.

If you decide to run the application using a config file please consider the next [HOCON](https://en.wikipedia.org/wiki/HOCON) config sample as reference:

```hocon
root-dir = "/tmp/"
geo-spark-distance = 1
export-format = "parquet"
start-at = 4
temperature-scale = "C"
numeric-type = "float"

merge-winds = true
merge-temperatures = true

spark {
  "spark.executor.instances": 2,
  "spark.executor.cores": 4
}

active-sources = [
    "airTemperature",
    "skinTemperature",
    "minTemperature",
    "maxTemperature"
    "humidity",
    "uwind",
    "vwind",
    "clearSkyDownwardSolar",
    "netShortwaveRadiation"
]
```

#### Scala API
If you like to use GWeather as a library first import the required dependency by adding the following lines
to your build.sbt file:

```sbt
externalResolvers += "GWeather" at "https://maven.pkg.github.com/abiratsis/GlobalWeatherData"

libraryDependencies += "abiratsis" %% "globalweatherdata" % "0.1.0"
```

Now you will be able to call the `Pipeline/PipelineBuilder` classes as shown next:

```scala
import com.abiratsis.gweather

....

    val pipeline = new PipelineBuilder()
      .withParameter("output-dir", "/Users/some_user/export_29_09_2020/")
      .withParameter("geo-spark-distance", 1)
      .withParameter("export-format", "parquet")
      .withParameter("merge-winds", true)
      .build()

    pipeline.execute()

    pipeline.ctx.spark.read.parquet(pipeline.ctx.userConfig.outputDir + "/export")
      .where("Country == 'Greece'")
      .show(1000)

```

### Datasets: weather & locations

#### Weather data
GWeather uses `NCEP/NCAR Reanalysis 1: Surface Flux` datasets provided by [NOAA](https://psl.noaa.gov/) Physical Sciences Laboratory (PSL).
NCEP/NCAR Reanalysis 1 project is using a state-of-the-art analysis/forecast system to perform data assimilation using past data from 1948 to the present. 
Please refer to PSL official web [page](https://psl.noaa.gov/data/gridded/data.ncep.reanalysis.html) for more information about the
NCEP/NCAR Reanalysis 1 project.

The datasets have the following characteristics:
 
 - Temporal Coverage: daily values for 1948/01/01 to present
 - Spatial Coverage: T62 Gaussian grid with 192x94 points
 - Levels: Surface or near the surface

Weather components used by GWeather:

   - Air Temperature 2m ([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=Air+Temperature))
   - Specific humidity at 2 meters ([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=Specific+humidity))
   - Skin Temperature ([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=Skin+Temperature))
   - U-wind at 10 m ([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=u-wind&Level=10))
   - V-wind at 10 m ([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=v-wind&Level=10))
   - Maximum temperature at 2m	([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Values&Variable=Maximum+temperature))
   - Minimum temperature at 2m	([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Values&Variable=Minimum+temperature))
   - Clear sky downward longwave flux ([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=Clear+sky+downward+longwave+flux))
   - Clear sky downward solar flux	([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=Clear+sky+downward+solar+flux))
   - Downward longwave radiation flux ([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=Downward+longwave+radiation+flux))
   - Downward solar radiation flux ([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=Downward+solar+radiation+flux))
   - Net longwave radiation ([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=Net+longwave+radiation+flux))
   - Net shortwave radiation ([source](https://psl.noaa.gov/cgi-bin/db_search/DBSearch.pl?Dataset=NCEP+Reanalysis+Daily+Averages&Variable=Net+shortwave+radiation+flux))

PSL stores data in [netCDF](https://psl.noaa.gov/data/gridded/whatsnetCDF.html). GWeather converts netCDF data into different formats. 
Currently, we support CSV, Apache Parquet, Apache ORC and Apache delta-lake.

#### World data

GWeather combines weather data together the world cities/towns database offered from 
[simplemaps](https://simplemaps.com/data/world-cities). The dataset is accurate and up-to-date since the data 
has been imported from authoritative sources such as the NGIA, US Geological Survey, US Census Bureau, and NASA.

### References
- https://psl.noaa.gov/data/gridded/data.ncep.reanalysis.surfaceflux.html
- https://psl.noaa.gov/data/gridded/data.ncep.reanalysis.html
- https://psl.noaa.gov/data/gridded/whatsnetCDF.html
- https://www.psl.noaa.gov/about/
- https://simplemaps.com/data/world-cities