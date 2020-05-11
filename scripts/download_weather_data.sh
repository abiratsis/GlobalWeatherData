#!/bin/sh
############################### install python prerequisites ###############################
install_prerequisites() {

  echo "Executing install_prerequisites...."
  # pip3
  pip_installed=$(pip3 --version)

  if [ -z "$pip_installed" ]; then
    echo "pip3 is not installed. Please install pip to continue."
    exit 1
  fi

  pandas_installed=$(pip3 show pandas)

  if [ -z "$pandas_installed" ]; then
    pip3 install pandas
  else
    echo "pandas is already installed"
  fi

  # netCDF4
  netCDF4_installed=$(pip3 show netCDF4 | grep Name)

  if [ -z "$netCDF4_installed" ]; then
    pip3 install netCDF4
  else
    echo "netCDF4 is already installed"
  fi
}

############################### Data sources #######################################
download_sources() {
  temp_dir=""
  humidity_dir=""
  wind_dir=""
  solar_dir=""

    # temp
  airtemp_url=""
  skintemp_url=""
  maxtemp_url=""
  mintemp_url=""

  # humidity
  humid_url=""

  # wind
  uwind_url=""
  vwind_url=""

  # solar radiation
  csdlf_url=""
  csdsf_url=""
  dlwrf_url=""
  dswrf_url=""
  nlwrs_url=""
  nswrs_url=""

  for arg in "$@"
  do
    case $arg in
        -t|--temperature)
        temp_dir="$2"
        shift
        shift
        ;;
        -h|--humidity)
        humidity_dir="$2"
        shift
        shift
        ;;
        -w|--wind)
        wind_dir="$2"
        shift
        shift
        ;;
        -s|--solar)
        solar_dir="$2"
        shift
        shift
        ;;
        --airtemp)
        airtemp_url="$2"
        shift
        shift
        ;;
        --skintemp)
        skintemp_url_url="$2"
        shift
        shift
        ;;
        --maxtemp)
        maxtemp_url="$2"
        shift
        shift
        ;;
        --mintemp)
        mintemp_url="$2"
        shift
        shift
        ;;
        --humid)
        humid_url="$2"
        shift
        shift
        ;;
        --uwind)
        uwind_url="$2"
        shift
        shift
        ;;
        --vwind)
        vwind_url="$2"
        shift
        shift
        ;;
        --csdlf)
        csdlf_url="$2"
        shift
        shift
        ;;
        --csdsf)
        csdsf_url="$2"
        shift
        shift
        ;;
        --csdlf)
        csdlf_url="$2"
        shift
        shift
        ;;
        --dlwrf)
        dlwrf_url="$2"
        shift
        shift
        ;;
        --nlwrs)
        nlwrs_url="$2"
        shift
        shift
        ;;
        --nswrs)
        nswrs_url="$2"
        shift
        shift
        ;;
    esac
  done

  echo "Executing create_dirs...."
  if [ -n "$temp_dir" ]; then
    mkdir -p "$temp_dir"

    echo "${temp_dir} was created."
  fi

  if [ -n "$humidity_dir" ]; then
    mkdir -p "$humidity_dir"

    echo "${humidity_dir} was created."
  fi

  if [ -n "$wind_dir" ]; then
    mkdir -p "$wind_dir"

    echo "${wind_dir} was created."
  fi

  if [ -n "$solar_dir" ]; then
    mkdir -p "$solar_dir"

    echo "${solar_dir} was created."
  fi

  echo "executing download_sources..."
  # download Air temperature 2m
  if [ -n "$airtemp_url" ]; then
#    wget -P "$temp_dir" -N "$airtemp_url"
    echo "${airtemp_url} was downloaded at ${temp_dir}."
  fi

  # download skin temperature
  if [ -n "$skintemp_url" ]; then
#    wget -P "$temp_dir" -N "$skintemp_url"
    echo "${skintemp_url} was downloaded at ${temp_dir}."
  fi

  # download max temperature
  if [ -n "$maxtemp_url" ]; then
#    wget -P "$temp_dir" -N "$maxtemp_url"
    echo "${maxtemp_url} was downloaded at ${temp_dir}."
  fi

  # download min temperature
  if [ -n "$mintemp_url" ]; then
#    wget -P "$temp_dir" -N "$mintemp_url"
    echo "${mintemp_url} was downloaded at ${temp_dir}."
  fi

  # download humidity 2m
  if [ -n "$humid_url" ]; then
#    wget -P "$humidity_dir" -N "$humid_url"
    echo "${humid_url} was downloaded at ${humidity_dir}."
  fi

  # download u-wind
  if [ -n "$uwind_url" ]; then
#    wget -P "$wind_dir" -N "$uwind_url"
    echo "${uwind_url} was downloaded at ${wind_dir}."
  fi

  # download v-wind
  if [ -n "$vwind_url" ]; then
#    wget -P "$wind_dir" -N "$vwind_url"
    echo "${vwind_url} was downloaded at ${wind_dir}."
  fi

  # download Clear Sky Downward Longwave Flux
  if [ -n "$csdlf_url" ]; then
#    wget -P "$solar_dir" -N "$csdlf_url"
    echo "${csdlf_url} was downloaded at ${solar_dir}."
  fi

  # download Clear Sky Downward Solar Flux
  if [ -n "$csdsf_url" ]; then
#    wget -P "$solar_dir" -N "$csdsf_url"
    echo "${csdsf_url} was downloaded at ${solar_dir}."
  fi

  # download Downward Longwave Radiation Flux
  if [ -n "$dlwrf_url" ]; then
#    wget -P "$solar_dir" -N "$dlwrf_url"
    echo "${dlwrf_url} was downloaded at ${solar_dir}."
  fi

  # download Downward Solar Radiation Flux
  if [ -n "$dswrf_url" ]; then
#    wget -P "$solar_dir" -N "$dswrf_url"
    echo "${dswrf_url} was downloaded at ${solar_dir}."
  fi

  # download Net Longwave Radiation Flux
  if [ -n "$nlwrs_url" ]; then
#    wget -P "$solar_dir" -N "$nlwrs_url"
    echo "${nlwrs_url} was downloaded at ${solar_dir}."
  fi

  # download Net Shortwave Radiation Flux
  if [ -n "$nswrs_url" ]; then
#    wget -P "$solar_dir" -N "$nswrs_url"
    echo "${nswrs_url} was downloaded at ${solar_dir}."
  fi
}
