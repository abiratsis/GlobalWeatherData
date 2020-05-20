import netCDF4
import sys
import pandas as pd
import numpy as np
from netCDF4 import num2date
import os

nc_file = sys.argv[1]
extract_var = sys.argv[2]
f = netCDF4.Dataset(nc_file, mode='r')

target_var = f.variables[extract_var]
 
# Get dimensions assuming 3D: time, latitude, longitude
time_dim, lat_dim, lon_dim = target_var.get_dims()
time_var = f.variables[time_dim.name]
times = num2date(time_var[:], time_var.units)
latitudes = f.variables[lat_dim.name][:]
longitudes = f.variables[lon_dim.name][:]

output_dir, fname = os.path.split(nc_file)
csv_name = fname.replace(".nc", ".csv")

filename = os.path.join(output_dir, csv_name)
print(f'Writing data in tabular form to {filename} (this may take some time)...')
times_grid, latitudes_grid, longitudes_grid = [
    x.flatten() for x in np.meshgrid(times, latitudes, longitudes, indexing='ij')]

df = pd.DataFrame({
    'time': [t.strftime() for t in times_grid],
    'lat': latitudes_grid,
    'lon': longitudes_grid,
    extract_var: target_var[:].flatten()})
df.to_csv(filename, index=False)

print(f'Done extracting to {filename}')