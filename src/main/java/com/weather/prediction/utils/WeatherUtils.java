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
 * Class which contains methods, widely used across the application.
 * 
 * @author Irene Mathew
 *
 */
import static com.weather.prediction.constants.WeatherConstants.DATE_TIME_DELIMITER;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.weather.prediction.exception.WeatherException;

public class WeatherUtils {
	/**
	 * This function extracts date from dateTime(yyyy-MM-dd'T'HH:mm:ss'Z')
	 * 
	 * @param dateTime
	 *            date and time provided as a string
	 * @return date in the format yyyy-MM-dd
	 * 
	 */
	public static String extractDatefromDateTime(String dateTime) {
		return dateTime.split(DATE_TIME_DELIMITER, -1)[0];
	}

	/**
	 * This function takes date in Date type and converts it to String type
	 * 
	 * @param date
	 *            in Date type
	 * @param dateFormat
	 *            date format to which the date is converted
	 * @return date in string
	 * 
	 */
	public static String dateToString(Date date, String dateFormat) {
		SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);
		return formatDate.format(date);
	}

	/**
	 * This function takes date in String type and converts it to Date type
	 * 
	 * @param date
	 *            in String type
	 * @param dateFormat
	 *            date format to which the date is converted
	 * @return date in Date type
	 * 
	 */
	public static Date stringToDate(String date, String dateFormat)
			throws ParseException {
		SimpleDateFormat formatDate = new SimpleDateFormat(dateFormat);
		return formatDate.parse(date);
	}

	/**
	 * This function extracts time from dateTime(yyyy-MM-dd'T'HH:mm:ss'Z')
	 * 
	 * @param dateTime
	 *            date and time provided as a string
	 * @return time in the format HH:mm:ss'Z'
	 * 
	 */
	public static String extractTimefromDateTime(String dateTime) {
		return dateTime.split(DATE_TIME_DELIMITER, -1)[1];
	}

	/**
	 * This function takes date in String type and converts it to the given
	 * format
	 * 
	 * @param inputFormat
	 *            input date format
	 * @param outputFormat
	 *            output date format
	 * @param inputDate
	 *            date which needs to be formatted
	 * @return date in given output format
	 * 
	 */
	public static String formatDate(String inputFormat, String outputFormat,
			String inputDate) throws ParseException {
		SimpleDateFormat informat = new SimpleDateFormat(inputFormat);
		SimpleDateFormat outformat = new SimpleDateFormat(outputFormat);
		Date date = informat.parse(inputDate);
		return outformat.format(date);
	}

	/**
	 * This function rounds off float values to the given decimal places
	 * 
	 * @param value
	 *            float value
	 * @param decimalPlace
	 *            number of decimal places
	 * @return date in given output format
	 * 
	 */
	public static float roundDecimalPlaces(float value, int decimalPlace) {
		return BigDecimal.valueOf(value)
				.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	/**
	 * This function creates output directory recursively if the path doesn't
	 * exist.
	 * 
	 * @param outputPath
	 *            the directory to which output is written
	 * @throws WeatherException is thrown when outputPath is not entered in config file
	 */
	public static void createOutputPath(String outputPath)
			throws WeatherException {

		if (outputPath == null)
			throw new WeatherException(
					"Please provide an output directory/path in the config file");
		File outDir = new File(outputPath);
		if (!outDir.exists())
			outDir.mkdirs();
	}

}
