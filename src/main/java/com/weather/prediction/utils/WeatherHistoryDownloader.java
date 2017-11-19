/******************************************************
 * Copyright (c) November 2017, Irene Mathew.
 * All Rights Reserved
 * 
 * This file can be redistributed and/or modified,
 * under the terms of the GNU General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or any later version.
 *******************************************************/
package com.weather.prediction.utils;

/**
 * Class which downloads historical weather data from real time API.
 * 
 * @author Irene Mathew
 *
 */
import static com.weather.prediction.constants.WeatherConstants.BASE_URL;
import static com.weather.prediction.constants.WeatherConstants.COMMA_DELIMITER;
import static com.weather.prediction.constants.WeatherConstants.DATE_FORMAT;
import static com.weather.prediction.constants.WeatherConstants.DATE_KEYWORD;
import static com.weather.prediction.constants.WeatherConstants.DATE_TIME_FORMAT;
import static com.weather.prediction.constants.WeatherConstants.INPUT_DATETIME_FORMAT;
import static com.weather.prediction.constants.WeatherConstants.LOOKUP_PATH;
import static com.weather.prediction.constants.WeatherConstants.MONTH_URL_CONSTANT;
import static com.weather.prediction.constants.WeatherConstants.MORNING_FORECAST_TIME;
import static com.weather.prediction.constants.WeatherConstants.NOON_FORECAST_TIME;
import static com.weather.prediction.constants.WeatherConstants.STATION_URL_CONSTANT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.weather.prediction.exception.WeatherException;

public class WeatherHistoryDownloader {
	private String location;
	public List<String> dataLines;

	private Logger logger = Logger.getLogger(WeatherHistoryDownloader.class);

	public WeatherHistoryDownloader(String location) {
		this.location = location;
	}

	/**
	 * This function triggers many sub functions for API URL creation and data
	 * download based on today's date.
	 * 
	 * @return historical data returned as a list of string lines
	 * @throws IOException
	 *             is thrown when look up file is not found
	 * @throws ParseException
	 *             is thrown when date is not correctly parsed
	 * @throws WeatherException
	 *             is thrown when given input location is invalid/ API URL is
	 *             unavailable
	 * 
	 */

	public List<String> downloadHistoryFromAPI() throws IOException,
			ParseException, WeatherException {
		dataLines = new ArrayList<String>();
		logger.debug("Location:" + location);
		String locDetails = getLocationDetailsFromLookUp();
		if (locDetails == null)
			throw new WeatherException(
					"Given input location is not valid. Please enter any one of these locations:{CANBERRA,SYDNEY,MELBOURNE,BRISBANE,PERTH,ADELAIDE,HOBART,DARWIN,GOLDCOAST}");
		List<String> monthList = findHistoricalDataMonths();
		logger.debug("Number of months for historical download: "
				+ monthList.size());
		String locDetailsSplit[] = locDetails.split(COMMA_DELIMITER, -1);
		String stationId = locDetailsSplit[0];
		String coordinates = locDetailsSplit[1] + COMMA_DELIMITER
				+ locDetailsSplit[2] + COMMA_DELIMITER + locDetailsSplit[3];
		for (String monthYear : monthList) {
			logger.info("Started historical data download from API");
			String urlPath = createUrl(BASE_URL, stationId, monthYear);
			callAPIUrl(urlPath, coordinates);
			logger.info("Completed historical data download from API");
		}
		return dataLines;
	}

	/**
	 * This function gets stationId, coordinates and altitude from lookup file
	 * for the given location .
	 * 
	 * @return stationId, coordinates and altitude as a comma separated String
	 * @throws IOException
	 *             is thrown when look up file is not found
	 * 
	 */

	public String getLocationDetailsFromLookUp() throws IOException {
		Properties prop = new Properties();
		InputStream stream = this.getClass().getResourceAsStream(LOOKUP_PATH);
		prop.load(stream);
		return prop.getProperty(this.location.toUpperCase());

	}

	/**
	 * This function gets list of months for historical data download. 5 months
	 * are considered for the same(ie., current and previous month of the
	 * year,then current,current-1 and current+1 months of the last year)
	 * 
	 * @return list of months in the format yyyyMM
	 * 
	 */

	public List<String> findHistoricalDataMonths() {
		List<String> yearMonthList = new ArrayList<String>();
		yearMonthList.add(getYearMonth(0, 0));
		yearMonthList.add(getYearMonth(0, -1));
		yearMonthList.add(getYearMonth(-1, -1));
		yearMonthList.add(getYearMonth(-1, 1));
		yearMonthList.add(getYearMonth(-1, 0));
		return yearMonthList;
	}

	/**
	 * This function finds month in the format yyyyMM based on arguments.
	 * 
	 * @param diffYears
	 *            number of years to subtract from current year
	 * @param diffMonths
	 *            number of months to subtract from current month
	 * 
	 * @return month in format yyyyMM
	 * 
	 */

	public static String getYearMonth(int diffYears, int diffMonths) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, diffMonths);
		cal.add(Calendar.YEAR, diffYears);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		return year + "" + String.format("%02d", month);
	}

	/**
	 * This function creates API URL based on month and stationId. Parts of the
	 * base URL(<#STATIONID#>,<#MONYR#>) are replaced with arguments passed to
	 * the function.
	 * 
	 * @param baseUrl
	 *            URL template
	 * @param stationId
	 *            station ID
	 * @param monthYear
	 *            month in the format yyyyMM
	 * 
	 * @return URL created
	 * 
	 */

	public String createUrl(String baseUrl, String stationId, String monthYear) {
		baseUrl = baseUrl.replaceAll(STATION_URL_CONSTANT, stationId);
		baseUrl = baseUrl.replaceAll(MONTH_URL_CONSTANT, monthYear);
		logger.debug("API URL to hit: " + baseUrl);
		return baseUrl;
	}

	/**
	 * This function takes each line and parses it to the format
	 * <LOCATION,COORDINATES,ALTITUDE,DATETIME,TEMPERATURE,HUMIDITY,PRESSURE>.
	 * It ignores records of today and future dates.
	 * 
	 * @param line
	 *            each line from API
	 * @param todayDate
	 *            today's date
	 * @param coordinates
	 *            geo-coordinates of location
	 * 
	 * @throws WeatherException
	 *             is thrown when API Date format is changed
	 */

	public void callDataParser(String line, String todayDate, String coordinates)
			throws WeatherException {
		try {
			if (line.startsWith(COMMA_DELIMITER)) {
				String lineSplit[] = line.split(COMMA_DELIMITER, -1);
				String lineDate = lineSplit[1];
				// skipping header, today's and future record because we are
				// predicting today + 4 days
				if (lineDate.compareTo(todayDate) < 0
						&& !lineDate.equals(DATE_KEYWORD)) {
					String forecastMorningTimestamp = WeatherUtils.formatDate(
							INPUT_DATETIME_FORMAT, DATE_TIME_FORMAT, (lineDate
									+ " " + MORNING_FORECAST_TIME));
					String forecastNoonTimestamp = WeatherUtils.formatDate(
							INPUT_DATETIME_FORMAT, DATE_TIME_FORMAT, (lineDate
									+ " " + NOON_FORECAST_TIME));
					dataLines.add(location + COMMA_DELIMITER + coordinates
							+ COMMA_DELIMITER + forecastMorningTimestamp
							+ COMMA_DELIMITER + lineSplit[10] + COMMA_DELIMITER
							+ lineSplit[11] + COMMA_DELIMITER + lineSplit[15]);

					dataLines.add(location + COMMA_DELIMITER + coordinates
							+ COMMA_DELIMITER + forecastNoonTimestamp
							+ COMMA_DELIMITER + lineSplit[16] + COMMA_DELIMITER
							+ lineSplit[17] + COMMA_DELIMITER + lineSplit[21]);
				}

			}
		} catch (ParseException e) {
			throw new WeatherException(
					"Date format in the source API has been changed. Expected Input Date Format is: "
							+ INPUT_DATETIME_FORMAT);
		}

	}

	/**
	 * This function calls API URL and thereby a parser for the downloaded data.
	 * 
	 * @param urlPath
	 *            API URL to hit
	 * @param coordinates
	 *            coordinates of the location is passed since API doesn't
	 *            provide geo-coordinates
	 * 
	 * @throws ParseException
	 *             is thrown when date is not correctly parsed
	 * @throws WeatherException
	 *             is thrown when requested API URL is unavailable
	 */
	public void callAPIUrl(String urlPath, String coordinates)
			throws ParseException, WeatherException {
		try {
			URL url = new URL(urlPath);
			Date date = Calendar.getInstance().getTime();
			String todayDate = WeatherUtils.dateToString(date, DATE_FORMAT);
			String line = null;
			URLConnection urlConnection = url.openConnection();
			InputStream in = urlConnection.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));

			while ((line = reader.readLine()) != null) {
				callDataParser(line, todayDate, coordinates);
			}
		} catch (IOException e) {
			throw new WeatherException("Requested API Url:" + urlPath
					+ " is currently unavailable");
		}
	}

}
