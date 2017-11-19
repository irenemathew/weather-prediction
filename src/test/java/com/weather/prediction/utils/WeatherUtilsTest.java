/******************************************************
* Copyright (c) November 2017, Irene Mathew.
* All Rights Reserved
* 
* This file can be redistributed and/or modified,
* under the terms of the GNU General Public License
* as published by the Free Software Foundation,
* either version 3 of the License, or any later version.
*******************************************************/
/**
 * 
 */
package com.weather.prediction.utils;

import static com.weather.prediction.constants.WeatherConstants.DATE_FORMAT;
import static com.weather.prediction.constants.WeatherConstants.DATE_TIME_FORMAT;
import static org.junit.Assert.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.weather.prediction.constants.WeatherConstants;
import com.weather.prediction.exception.WeatherException;


/**
 * @author Irene Mathew
 *
 */
public class WeatherUtilsTest {
	private static Date inputDate;
	private static String inputDateTimeString;
	private static String inputDateString;
	private static String timeString;
	private static String outputPath;

	@BeforeClass
	public static void setUpOnce() throws ParseException {
		inputDateTimeString = "2016-10-03T15:00:00Z";
		inputDateString="2016-10-03";
		timeString="15:00:00Z";
		outputPath="/out/";
		SimpleDateFormat sdf_dateTime = new SimpleDateFormat(WeatherConstants.DATE_TIME_FORMAT);
		inputDate = sdf_dateTime.parse(inputDateTimeString);
	}
	
	@Test
	public void testExtractDatefromDateTime_Pass()   {
		assertEquals(inputDateString, WeatherUtils.extractDatefromDateTime(inputDateTimeString));
	}
	
	@Test
	public void testDateToString_Pass()   {
		assertEquals(inputDateString, WeatherUtils.dateToString(inputDate,DATE_FORMAT));
	}
	
	@Test
	public void testExtractTimefromDateTime_Pass()   {
		assertEquals(timeString, WeatherUtils.extractTimefromDateTime(inputDateTimeString));
	}
	
	@Test
	public void testFormatDate_Pass() throws ParseException   {
		assertEquals(inputDateString, WeatherUtils.formatDate(DATE_TIME_FORMAT,DATE_FORMAT,inputDateTimeString));
	}
	
	@Test
	public void testroundDecimalPlaces_Pass() throws ParseException   {
		float input=WeatherUtils.roundDecimalPlaces(21.3326f,2);
		float expected=21.33f;
		assertEquals(expected,input,0.0f);
	}
	
	@Test
	public void testCreateOutputPathIfNotExists_Pass() throws WeatherException{
		WeatherUtils.createOutputPath(outputPath);
		File file = new File(outputPath);
		assertTrue(file.exists());
	}
	
	@Test(expected=WeatherException.class)
	public void testCreateOutputPathIfNotExists_Fail() throws WeatherException{
		String outputPath=null;
		WeatherUtils.createOutputPath(outputPath);
		File file = new File(outputPath);
		assertTrue(file.exists());
	}
	
	
}
	
	
	

