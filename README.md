# GWeather
A command line tool/library that helps users to work with climate data from all over the world.
### Description

GWeather it is a tool for retrieving, exporting and viewing world weather data. The application provides world climate data
for wind, temperature, humidity and solar radiation.

### Purpose

Recently I worked on a project for analyzing geo-spacial data, quickly I realized that I was not able
to find an open-source tool which combines reliable weather data with accurate geographical locations. 
Most of the existing datasets provide the weather data using geographical coordinates and not the actual
text representation of the location e.g `city/country`.  

I came up with GWeather having in mind the next goals:
 - Find a source which provides reliable, frequent and recent climate data 
 - Try to cover as many locations as possible all over the planet
 - Calculate efficiently geo-spacial operations
 - Provide a friendly Scala based API
 - Provide a friendly command line interface

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
   
### Prerequisites

Make sure you have the next packages installed on your machine:
- Python3 
- pip3 