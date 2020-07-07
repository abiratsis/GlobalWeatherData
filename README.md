# GWeather
A command line tool/library that helps users to work with climate data from all over the world.
### Description

GWeather it is a tool for retrieving, exporting and viewing world weather data. The application provides world climate data
for wind, temperature, humidity and solar radiation.

### Purpose

Recently I worked on a project for analyzing climate geo-spacial data. Quickly I realized that I was not able
to find an open-source tool which combines reliable weather data with accurate geographical locations. 
Most of the existing datasets provide the weather data using geographical coordinates and not the actual
text representation of the location e.g `city/country`.  

I came up with GWeather having in mind the next goals:
 - Find a source which provides reliable, frequent and recent climate data 
 - Try to cover as many locations as possible all over the planet
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
2. Unzip the gweather_v0.1.0-alpha.zip.
3. Grant execute permission to `gweather.sh` with `chmod +x gweather.sh`
4. Execute the program `./gweather [options]`

#### Configuration

Users can determine the program settings via the command line (`--input-mode c`) or via a config file (`--input-mode f`).
In both cases users should provide the following settings:

```commandline
rootDir (-r): The root directory where the weather datasources will be exported
geoSparkDistance (-d): The distance between 2 GeoSpark points
mergeWinds (-w): A flag indicating whether winds speeds should be merged into one
mergeTemp (-t): A flag indicating whether min/max temperatures should be merged into one
exportFormat (-f): Type of exported data, it should be one of [delta, orc, parquet, csv]
temperatureScale (-l): Temperature scale, it should be one of [C, F]
numericType (-n): The numeric type for CDF columns, it should be one of [double, float]
activeSources (-a): The sources that should be exported by the program
startAt (-s): The step the process should start from. The available steps are: install prerequisites(1), download data(2), convert to CSV(3), export(4)
```

Use gweather with a configuration file as next:
```commandline
gweather -m f --user-conf <conf_file>
```

Or via the command line:
```commandline
gweather -m f -r /tmp/data/ -d 1 -f "csv" ...
```
**Attention:** be aware that once you specify `--user-conf` (running with config mode) argument you should not add more arguments
since they are mutually exclusive. 

When using command line mode, if some of the arguments are not specified gweather will use the following default values:
```commandline
rootDir: none
geoSparkDistance: 1
mergeWinds: true
mergeTemp: true
exportFormat: parquet
temperatureScale: "C"
numericType: "double"
activeSources: ["airTemperature", "skinTemperature", "minTemperature", "maxTemperature", "humidity", "uwind", "vwind", "clearSkyDownwardSolar", "netShortwaveRadiation"]
startAt: "1"
```

#### Scala API
TODO

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

PSL uses [netCDF](https://psl.noaa.gov/data/gridded/whatsnetCDF.html) for storing the data. GWeather
can convert the netCDF data into different formats. Currently, we support CSV, Apache Parquet, 
Apache ORC and Apache delta-lake.

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