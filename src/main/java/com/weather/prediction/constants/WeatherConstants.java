/******************************************************
 * Copyright (c) November 2017, Irene Mathew.
 * All Rights Reserved
 * 
 * This file can be redistributed and/or modified,
 * under the terms of the GNU General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or any later version.
 *******************************************************/
package com.weather.prediction.constants;

/**
 * Class containing constant values used across the project
 * 
 * @author Irene Mathew
 *
 */
public class WeatherConstants {

	//Constants related to look up and output files
	public static final String LOOKUP_PATH = "/lookup.properties";
	public static final String OUTPUT_FILE_NAME ="/output.txt";

	//Constants related to delimiter used 
	public static final String COMMA_DELIMITER = ",";
	public static final String DATE_TIME_DELIMITER = "T";
	public static final String PIPE_DELIMITER = "|";

	//Constants related to API URL for Historical Data Download
	public static final String BASE_URL = "http://www.bom.gov.au/climate/dwo/#MONYR#/text/#STATIONID#.#MONYR#.csv";
	public static final String STATION_URL_CONSTANT = "#STATIONID#";
	public static final String MONTH_URL_CONSTANT = "#MONYR#";

	//Constants related to Date,Time and its format
	public static final String DATE_KEYWORD = "\"Date\"";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String INPUT_DATETIME_FORMAT = "yyyy-MM-dd hh:mm";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String MORNING_FORECAST_TIME = "09:00";
	public static final String NOON_FORECAST_TIME = "15:00";
	
	//Constants related to windows
	public static final int WINDOW_COUNT = 8;
	public static final int WINDOW_SIZE=7;

	//Constants for weather conditions
	public static final String SUNNY = "SUNNY";
	public static final String SNOWY = "SNOWY";
	public static final String RAINY = "RAINY";
	public static final String CLOUDY="CLOUDY";
	public static final String COLD="COLD";
	public static final String MOSTLY_SUNNY="MOSTLY SUNNY";
	public static final String NOT_FOUND="NOT FOUND";

}
