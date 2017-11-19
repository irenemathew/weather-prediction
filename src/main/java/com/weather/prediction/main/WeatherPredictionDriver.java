/******************************************************
 * Copyright (c) November 2017, Irene Mathew.
 * All Rights Reserved
 * 
 * This file can be redistributed and/or modified,
 * under the terms of the GNU General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or any later version.
 *******************************************************/
package com.weather.prediction.main;

/**
 * Main class which downloads historical weather data and triggers forecast for consecutive five days.
 * 
 * @author Irene Mathew
 *
 */

import static com.weather.prediction.constants.WeatherConstants.COMMA_DELIMITER;
import static com.weather.prediction.constants.WeatherConstants.DATE_FORMAT;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.weather.prediction.bean.WeatherData;
import com.weather.prediction.bean.WeatherDataArchive;
import com.weather.prediction.exception.WeatherException;
import com.weather.prediction.logic.WeatherPredictor;
import com.weather.prediction.utils.WeatherHistoryDownloader;
import com.weather.prediction.utils.WeatherUtils;

public class WeatherPredictionDriver {

	final static Logger logger = Logger
			.getLogger(WeatherPredictionDriver.class);

	/**
	 * This function is the starting point of the application. It triggers
	 * weather historical data download based on given location and creates a
	 * output directory path provided by user if the path doesn't exist. It also
	 * calls another function to load historical data into weather data object.
	 * 
	 * @param args
	 *            [0] Location name for which prediction is to be made
	 * @param args
	 *            [1] Directory to which output should be written
	 * 
	 */
	public static void main(String[] args) {
		WeatherPredictionDriver predictor = new WeatherPredictionDriver();
		List<String> historyData = new ArrayList<String>();
		try {
			String location = args[0].toUpperCase();
			String outputPath = args[1];
			// creates output directory
			WeatherUtils.createOutputPath(outputPath);
			WeatherHistoryDownloader downloader = new WeatherHistoryDownloader(
					location);
			// downloading historical data from real time API
			historyData = downloader.downloadHistoryFromAPI();
			if (historyData.size() > 0) {
				predictor.loadHistoricalData(historyData, outputPath);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * This function loads historical data to archiveMap which is used through
	 * out the application for prediction and triggers prediction engine.
	 * 
	 * @param historyData
	 *            List of historical weather records from real time API
	 * @param outputPath
	 *            output directory path
	 * @throws WeatherException
	 *             is thrown when error occurs in forecasting for next five days
	 * @throws ParseException
	 *             is thrown when date is not correctly parsed
	 * @throws FileNotFoundException
	 *             is thrown if the output path doesn't exist
	 */
	private void loadHistoricalData(List<String> historyData, String outputPath)
			throws ParseException, WeatherException, FileNotFoundException {
		WeatherDataArchive weatherDataArchive = new WeatherDataArchive();
		Map<String, List<WeatherData>> weatherArchiveMap = new HashMap<String, List<WeatherData>>();
		logger.info("Historical Weather Data :Started loading to application");
		for (String line : historyData) {
			WeatherData weatherData = new WeatherData();
			String lineSplit[] = line.split(COMMA_DELIMITER, -1);
			weatherData.setLocation(lineSplit[0]);
			weatherData.setLat(lineSplit[1]);
			weatherData.setLongt(lineSplit[2]);
			weatherData.setElevtn(lineSplit[3]);
			String date = WeatherUtils.extractDatefromDateTime(lineSplit[4]);
			weatherData.setDateTime(lineSplit[4]);
			weatherData.setDate(WeatherUtils.stringToDate(date, DATE_FORMAT));
			weatherData.setTemp(Float.parseFloat(lineSplit[5]));
			weatherData.setHumidity(Float.parseFloat(lineSplit[6]));
			weatherData.setPressure(Float.parseFloat(lineSplit[7]));
			putToArchiveMap(weatherData, weatherArchiveMap);
		}
		logger.info("Historical Weather Data :Successfully loaded");

		weatherDataArchive.setWeatherArchive(weatherArchiveMap);
		WeatherPredictor predictionDriver = new WeatherPredictor(
				weatherDataArchive);
		// triggering prediction engine
		predictionDriver.predictWeatherforFiveDays(outputPath);

	}

	/**
	 * This function adds historical data to a map which is finally set to
	 * archiveMap
	 * 
	 * @param weatherData
	 *            object corresponding to each weather record
	 * @param weatherArchiveMap
	 *            contains historical weather data
	 * 
	 */
	private void putToArchiveMap(WeatherData weatherData,
			Map<String, List<WeatherData>> weatherArchiveMap) {

		if (!weatherArchiveMap.containsKey(WeatherUtils.dateToString(
				weatherData.getDate(), DATE_FORMAT))) {
			List<WeatherData> list = new ArrayList<WeatherData>();
			list.add(weatherData);
			weatherArchiveMap.put(WeatherUtils.dateToString(
					weatherData.getDate(), DATE_FORMAT), list);
		} else {
			weatherArchiveMap.get(
					WeatherUtils.dateToString(weatherData.getDate(),
							DATE_FORMAT)).add(weatherData);
		}

	}

}
