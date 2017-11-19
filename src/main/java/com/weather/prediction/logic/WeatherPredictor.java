/******************************************************
 * Copyright (c) November 2017, Irene Mathew.
 * All Rights Reserved
 * 
 * This file can be redistributed and/or modified,
 * under the terms of the GNU General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or any later version.
 *******************************************************/
package com.weather.prediction.logic;

/**
 * Class which solely coordinates prediction for consecutive five days and writes prediction to an output file.
 * 
 * @author Irene Mathew
 *
 */

import static com.weather.prediction.constants.WeatherConstants.CLOUDY;
import static com.weather.prediction.constants.WeatherConstants.COLD;
import static com.weather.prediction.constants.WeatherConstants.DATE_FORMAT;
import static com.weather.prediction.constants.WeatherConstants.DATE_TIME_DELIMITER;
import static com.weather.prediction.constants.WeatherConstants.MOSTLY_SUNNY;
import static com.weather.prediction.constants.WeatherConstants.NOT_FOUND;
import static com.weather.prediction.constants.WeatherConstants.OUTPUT_FILE_NAME;
import static com.weather.prediction.constants.WeatherConstants.RAINY;
import static com.weather.prediction.constants.WeatherConstants.SNOWY;
import static com.weather.prediction.constants.WeatherConstants.SUNNY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.weather.prediction.bean.VariationVector;
import com.weather.prediction.bean.WeatherData;
import com.weather.prediction.bean.WeatherDataArchive;
import com.weather.prediction.exception.WeatherException;
import com.weather.prediction.utils.WeatherUtils;

public class WeatherPredictor {
	private WeatherDataArchive weatherDataArchive;

	final static Logger logger = Logger.getLogger(WeatherPredictor.class);

	public WeatherPredictor(final WeatherDataArchive weatherDataArchive) {
		this.weatherDataArchive = weatherDataArchive;
	}

	/**
	 * This function triggers forecasting for consecutive five days. It selects
	 * a list of input records from the historical data and gives it to the
	 * algorithm. Forecast data corresponding to each day is written to an
	 * output file and also fed to the archiveMap so that next day prediction
	 * makes use of this data.
	 * 
	 * @param outputPath
	 *            output directory
	 * 
	 * @throws ParseException
	 *             is thrown when date is not correctly parsed
	 * @throws WeatherException
	 *             is thrown when algorithm specific conditions are not met
	 * @throws FileNotFoundException
	 *             is thrown if the output path doesn't exist
	 */
	public void predictWeatherforFiveDays(String outputPath)
			throws ParseException, WeatherException, FileNotFoundException {
		PrintWriter writer = new PrintWriter(new File(outputPath
				+ OUTPUT_FILE_NAME));
		logger.info("Predicted output is written to " + outputPath
				+ OUTPUT_FILE_NAME);
		for (int i = 0; i < 5; i++) {
			List<WeatherData> presentYearList = new ArrayList<WeatherData>();
			List<WeatherData> lastYearList = new ArrayList<WeatherData>();
			String dayBeforePrediction = getLatestDateFromArchive();
			logger.debug("Previous Day before Prediction date"
					+ dayBeforePrediction);
			presentYearList.addAll(getRecordsFromArchive(dayBeforePrediction,
					0, 7));
			lastYearList.addAll(getRecordsFromArchive(dayBeforePrediction, 358,
					372));
			// sorting based on date and time
			Collections.sort(presentYearList, (o1, o2) -> o1.getDateTime()
					.compareTo(o2.getDateTime()));
			Collections.sort(lastYearList, (o1, o2) -> o1.getDateTime()
					.compareTo(o2.getDateTime()));
			validateNumberOfInputRecordsToAlgorithm(presentYearList, 7);
			validateNumberOfInputRecordsToAlgorithm(lastYearList, 14);
			SlidingWindowImplementation algorithm = new SlidingWindowImplementation(
					presentYearList, lastYearList);
			// calling algorithm to find predicted variation
			List<VariationVector> predictedVariation = algorithm
					.implementSlidingWindow();
			logger.info("Started Prediction");
			// adding predicted variation to previous day in order to get result
			findResultWithVariation(predictedVariation, dayBeforePrediction,
					writer);
		}
		logger.info("Completed Prediction");
		writer.close();

	}

	/**
	 * This function checks the number of input records given to the algorithm.
	 * 
	 * @param recordList
	 *            List of weather records to the algorithm
	 * @param expectedNumber
	 *            Expected number of records
	 * @throws WeatherException
	 *             is thrown when the expected number is not met
	 */
	private void validateNumberOfInputRecordsToAlgorithm(
			List<WeatherData> recordList, int expectedNumber)
			throws WeatherException {
		int inputNumber = recordList.size() / 2;
		if (inputNumber != expectedNumber)
			throw new WeatherException("Input list size(" + inputNumber
					+ ") is not same as expected(" + expectedNumber + ")");

	}

	/**
	 * This function gives output of each day forecast based on the prediction
	 * variation got from algorithm and writes it to an output file. Prediction
	 * variation is always added to weather record corresponding to previous day
	 * of the forecast date.
	 * 
	 * @param predictedVariation
	 *            Predicted variation calculated from algorithm
	 * @param dayBeforePrediction
	 *            Day before forecast date
	 * @param writer
	 *            Printwriter object for the output file
	 * @throws ParseException
	 *             is thrown when date is not correctly parsed
	 */
	private void findResultWithVariation(
			List<VariationVector> predictedVariation,
			String dayBeforePrediction, PrintWriter writer)
			throws ParseException {
		String predicitonDate = findPredictionDate(dayBeforePrediction);
		logger.info("Prediction Date: " + predicitonDate);
		List<WeatherData> predictionDayList = new ArrayList<WeatherData>();
		List<WeatherData> previousDayDataList = this.weatherDataArchive
				.getWeatherArchive().get(dayBeforePrediction);
		for (WeatherData previousDayData : previousDayDataList) {
			logger.debug("Previous Day Data:" + previousDayData.toString());
			String time = WeatherUtils.extractTimefromDateTime(previousDayData
					.getDateTime());
			for (VariationVector variation : predictedVariation) {
				WeatherData predictionData = new WeatherData();
				if (time.equals(variation.getTime())) {
					predictionData.setDate(WeatherUtils.stringToDate(
							predicitonDate, DATE_FORMAT));
					predictionData.setDateTime(predicitonDate
							+ DATE_TIME_DELIMITER + time);
					predictionData.setElevtn(previousDayData.getElevtn());
					predictionData.setLat(previousDayData.getLat());
					predictionData.setLongt(previousDayData.getLongt());
					predictionData.setLocation(previousDayData.getLocation());
					predictionData.setHumidity(WeatherUtils.roundDecimalPlaces(
							previousDayData.getHumidity()
									+ variation.getHumidity(), 2));
					predictionData.setPressure(WeatherUtils.roundDecimalPlaces(
							previousDayData.getPressure()
									+ variation.getPressure(), 2));
					predictionData.setTemp(WeatherUtils.roundDecimalPlaces(
							previousDayData.getTemp()
									+ variation.getTemperature(), 2));
					predictionData.setCondition(findCondition(
							predictionData.getTemp(),
							predictionData.getPressure(),
							predictionData.getHumidity()));
					predictionDayList.add(predictionData);
					writer.write(predictionData.toString() + "\n");
					logger.info("Predicted Data: " + predictionData.toString());
				}
			}

		}

		this.weatherDataArchive.getWeatherArchive().put(predicitonDate,
				predictionDayList);

	}

	/**
	 * This function finds weather conditions based on boundary values of
	 * temperature,humidity and presssure. These values are derived based on the
	 * observation of historical data and weather patterns.
	 * 
	 * @param temp
	 *            Temperature of the predicted record
	 * @param pressure
	 *            Pressure of the predicted record
	 * @param humidity
	 *            Humidity of the predicted record
	 * 
	 * @return weather condition
	 */

	private String findCondition(float temp, float pressure, float humidity) {
		String condition = "";

		if (temp <= 5)
			condition = SNOWY;
		else if (temp > 5 && temp <= 15)
			condition = COLD;
		else if (temp >= 23 && humidity < 80 && pressure >= 1005)
			condition = SUNNY;
		else if ((temp > 15 && temp < 23) && (humidity >= 36 && humidity < 80) && pressure >=1005)
			condition = MOSTLY_SUNNY;
		else if (humidity > 85 || pressure <1005)
			condition = RAINY;
		else if (humidity >= 80 && humidity < 85)
			condition = CLOUDY;
		else
			condition = NOT_FOUND;
		return condition;
	}

	/**
	 * This function filters a certain number of records from historical data
	 * which is further fed to algorithm. All dates between start and end day
	 * differences are fetched.
	 * 
	 * @param startPredictionDate
	 *            start date of prediction
	 * @param beginRange
	 *            Start day difference
	 * @param endRange
	 *            End day difference
	 * 
	 * @return list of weather records filtered from historical data
	 */
	private List<WeatherData> getRecordsFromArchive(String startPredictionDate,
			int beginRange, int endRange) throws ParseException {
		Set<String> dateSet = this.weatherDataArchive.getWeatherArchive()
				.keySet();
		List<WeatherData> filteredItems = new ArrayList<WeatherData>();
		Date startDate = WeatherUtils.stringToDate(startPredictionDate,
				DATE_FORMAT);
		for (String date : dateSet) {
			Date formattedDate = WeatherUtils.stringToDate(date, DATE_FORMAT);
			long diff = startDate.getTime() - formattedDate.getTime();
			long dayDiff = diff / (1000 * 60 * 60 * 24);
			if (dayDiff >= beginRange && dayDiff < endRange) {
				filteredItems.addAll(this.weatherDataArchive
						.getWeatherArchive().get(date));
			}
		}
		return filteredItems;
	}

	/**
	 * This function finds the prediction date for each iteration.
	 * 
	 * @param dayBeforePrediction
	 *            day before prediction day
	 * 
	 * @return prediction date
	 * 
	 * @throws ParseException
	 *             is thrown when date is not correctly parsed
	 */
	private String findPredictionDate(String dayBeforePrediction)
			throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(WeatherUtils.stringToDate(dayBeforePrediction,
				DATE_FORMAT));
		calendar.add(Calendar.DATE, 1);
		Date date = calendar.getTime();
		return WeatherUtils.dateToString(date, DATE_FORMAT);

	}

	/**
	 * This function finds maximum date from the historical data. Prediction
	 * variation from algorithm is added to weather record corresponding to this
	 * date since prediction date is always the day following the maximum/latest
	 * date.
	 * 
	 * @return maximum date from historical data
	 */
	private String getLatestDateFromArchive() {
		TreeSet<String> dateSet = new TreeSet<String>();
		dateSet.addAll(this.weatherDataArchive.getWeatherArchive().keySet());
		return dateSet.last();
	}

}
