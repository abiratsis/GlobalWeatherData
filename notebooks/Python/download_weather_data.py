# Databricks notebook source
# MAGIC %md #### Script for converting NetCDF to CSV (https://confluence.ecmwf.int/display/CKB/How+to+convert+NetCDF+to+CSV)

# COMMAND ----------

import netCDF4
import sys
import pandas as pd
import numpy as np
from netCDF4 import num2date
import os

def nc_to_csv(nc_file, extract_var):
  f = netCDF4.Dataset(nc_file, mode='r')
  target_var = f.variables[extract_var]
 
  # Get dimensions assuming 3D: time, latitude, longitude
  time_dim, lat_dim, lon_dim = target_var.get_dims()
  time_var = f.variables[time_dim.name]
  times = num2date(time_var[:], time_var.units)
  latitudes = f.variables[lat_dim.name][:]
  longitudes = f.variables[lon_dim.name][:]

  output_dir = os.path.dirname(nc_file)
  csv_name = nc_file.split(".")[0]
  filename = os.path.join(output_dir, f'{extract_var}.csv')
  print(f'Writing data in tabular form to {filename} (this may take some time)...')
  times_grid, latitudes_grid, longitudes_grid = [
      x.flatten() for x in np.meshgrid(times, latitudes, longitudes, indexing='ij')]
  df = pd.DataFrame({
      'time': [t.strftime() for t in times_grid],
      'lat': latitudes_grid,
      'lon': longitudes_grid,
      extract_var: target_var[:].flatten()})
  
  df.dropna(subset=[extract_var]).to_csv(filename, index=False)

# COMMAND ----------

# MAGIC %md #### Get temperature data from cdc.noaa.gov

# COMMAND ----------

# MAGIC %sh mkdir -p /dbfs/FileStore/data/weather/temp/

# COMMAND ----------

# MAGIC %sh wget -P "/dbfs/FileStore/data/weather/temp/" -N "ftp://ftp.cdc.noaa.gov/Datasets/cpc_global_temp/tmax.2020.nc"

# COMMAND ----------

# MAGIC %sh wget -P "/dbfs/FileStore/data/weather/temp/" -N "ftp://ftp.cdc.noaa.gov/Datasets/cpc_global_temp/tmin.2020.nc"

# COMMAND ----------

# MAGIC %md #### Convert temp data to csv

# COMMAND ----------

tmin_path = "/dbfs/FileStore/data/weather/temp/tmin.2020.nc"
nc_to_csv(tmin_path, "tmin")

tmax_path = "/dbfs/FileStore/data/weather/temp/tmax.2020.nc"
nc_to_csv(tmax_path, "tmax")

# COMMAND ----------

# MAGIC %fs ls /FileStore/data/weather/temp

# COMMAND ----------

# MAGIC %md #### Get humidity data

# COMMAND ----------

# MAGIC %sh mkdir -p /dbfs/FileStore/data/weather/humidity/

# COMMAND ----------

# MAGIC %sh wget -P "/dbfs/FileStore/data/weather/humidity/" -N "ftp://ftp.cdc.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/shum.2m.gauss.2020.nc"

# COMMAND ----------

# MAGIC %md #### Convert humidity data to csv

# COMMAND ----------

hum_path = "/dbfs/FileStore/data/weather/humidity/shum.2m.gauss.2020.nc"
nc_to_csv(hum_path, "shum")

# COMMAND ----------

# MAGIC %md #### Get wind data

# COMMAND ----------

# MAGIC %sh mkdir -p /dbfs/FileStore/data/weather/wind/

# COMMAND ----------

# MAGIC %sh wget -P "/dbfs/FileStore/data/weather/wind/" -N "ftp://ftp.cdc.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/uwnd.10m.gauss.2020.nc"

# COMMAND ----------

# MAGIC %sh wget -P "/dbfs/FileStore/data/weather/wind/" -N "ftp://ftp.cdc.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/vwnd.10m.gauss.2020.nc"

# COMMAND ----------

# MAGIC %md #### Convert wind data to csv

# COMMAND ----------

uwind_path = "/dbfs/FileStore/data/weather/wind/uwnd.10m.gauss.2020.nc"
vwind_path =  "/dbfs/FileStore/data/weather/wind/vwnd.10m.gauss.2020.nc"

nc_to_csv(uwind_path, "uwnd")
nc_to_csv(vwind_path, "vwnd")

# COMMAND ----------

# MAGIC %md #### Get solar radiation data

# COMMAND ----------

# MAGIC %sh mkdir -p /dbfs/FileStore/data/weather/solar_radiation/

# COMMAND ----------

# MAGIC %md Get Net Shortwave Radiation

# COMMAND ----------

# MAGIC %sh wget -P "/dbfs/FileStore/data/weather/solar_radiation/" -N "ftp://ftp.cdc.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/nswrs.sfc.gauss.2020.nc"

# COMMAND ----------

# MAGIC %md Get Downward Solar Radiation

# COMMAND ----------

# MAGIC %sh wget -P "/dbfs/FileStore/data/weather/solar_radiation/" -N "ftp://ftp.cdc.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/dswrf.sfc.gauss.2020.nc"

# COMMAND ----------

# MAGIC %md Get Clear Sky Downward Solar 

# COMMAND ----------

# MAGIC %sh wget -P "/dbfs/FileStore/data/weather/solar_radiation/" -N "ftp://ftp.cdc.noaa.gov/Datasets/ncep.reanalysis.dailyavgs/surface_gauss/csdsf.sfc.gauss.2020.nc"

# COMMAND ----------

# MAGIC %md #### Convert solar data to csv

# COMMAND ----------

nswrs_path = "/dbfs/FileStore/data/weather/solar_radiation/nswrs.sfc.gauss.2020.nc"
dswrf_path = "/dbfs/FileStore/data/weather/solar_radiation/dswrf.sfc.gauss.2020.nc"
csdsf_path = "/dbfs/FileStore/data/weather/solar_radiation/csdsf.sfc.gauss.2020.nc"

nc_to_csv(nswrs_path, "nswrs")
nc_to_csv(dswrf_path, "dswrf")
nc_to_csv(csdsf_path, "csdsf")

# COMMAND ----------

# dbfs:/FileStore/data/weather/temp/tmax.csv
# dbfs:/FileStore/data/weather/temp/tmin.csv
# dbfs:/FileStore/data/weather/humidity/shum.csv
# dbfs:/FileStore/data/weather/wind/uwnd.csv
# dbfs:/FileStore/data/weather/wind/vwnd.csv
# dbfs:/FileStore/data/weather/solar_radiation/nswrs.csv
# dbfs:/FileStore/data/weather/solar_radiation/dswrf.csv
# dbfs:/FileStore/data/weather/solar_radiation/csdsf.csv

tmax_df = spark.read.option("header", "true").csv("dbfs:/FileStore/data/weather/temp/tmax.csv")
tmin_df = spark.read.option("header", "true").csv("dbfs:/FileStore/data/weather/temp/tmin.csv")
shum_df = spark.read.option("header", "true").csv("dbfs:/FileStore/data/weather/humidity/shum.csv") # !!!!
uwnd_df = spark.read.option("header", "true").csv("dbfs:/FileStore/data/weather/wind/uwnd.csv")
vwnd_df = spark.read.option("header", "true").csv("dbfs:/FileStore/data/weather/wind/vwnd.csv")
nswrs_df = spark.read.option("header", "true").csv("dbfs:/FileStore/data/weather/solar_radiation/nswrs.csv")

# diff = tmax_df.join(tmin_df, ["time", "lat", "lon"], "left_anti") # OK

diff = tmax_df.join(shum_df, ["time", "lat", "lon"], "left_anti")

# diff -> 6916906
# shum_df -> 1985280
# uwnd_df -> 1985280
# vwnd_df -> 1985280
diff.count()

# COMMAND ----------


