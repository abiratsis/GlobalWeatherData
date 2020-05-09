#!/bin/sh

mkdir -p "/Users/abiratsis/Desktop/Covid-19/Data/weather/temp"
mkdir -p "/Users/abiratsis/Desktop/Covid-19/Data/weather/humidity"
mkdir -p "/Users/abiratsis/Desktop/Covid-19/Data/weather/wind"
mkdir -p "/Users/abiratsis/Desktop/Covid-19/Data/weather/solar_radiation"

# Attention: if facing issues with locale, check this https://stackoverflow.com/questions/56716993/error-message-when-starting-vim-failed-to-set-locale-category-lc-numeric-to-en

# download Air temperature 2m
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/temp" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/air.2m.gauss.2020.nc"

# download skin temperature
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/temp" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/skt.sfc.gauss.2020.nc"

# download max temperature
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/temp" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/tmax.2m.gauss.2020.nc"

# download min temperature
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/temp" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/tmin.2m.gauss.2020.nc"

# download humidity 2m
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/humidity" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/shum.2m.gauss.2020.nc"

# download u-wind
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/wind" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/uwnd.10m.gauss.2020.nc"

# download v-wind
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/wind" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/vwnd.10m.gauss.2020.nc"

# download Clear Sky Downward Longwave Flux
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/solar_radiation" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/csdlf.sfc.gauss.2020.nc"

# download Clear Sky Downward Solar Flux
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/solar_radiation" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/csdsf.sfc.gauss.2020.nc"

# download Downward Longwave Radiation Flux
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/solar_radiation" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/dlwrf.sfc.gauss.2020.nc"

# download Downward Solar Radiation Flux
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/solar_radiation" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/dswrf.sfc.gauss.2020.nc"

# download Net Longwave Radiation Flux
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/solar_radiation" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/nlwrs.sfc.gauss.2020.nc"

# download Net Shortwave Radiation Flux
wget -P "/Users/abiratsis/Desktop/Covid-19/Data/weather/solar_radiation" -N "ftp://ftp2.psl.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/nswrs.sfc.gauss.2020.nc"
