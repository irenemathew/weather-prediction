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
package com.weather.prediction.logic;

import static com.weather.prediction.constants.WeatherConstants.COMMA_DELIMITER;
import static com.weather.prediction.constants.WeatherConstants.DATE_FORMAT;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.weather.prediction.bean.SlidingWindow;
import com.weather.prediction.bean.WeatherData;
import com.weather.prediction.exception.WeatherException;
import com.weather.prediction.utils.WeatherUtils;

/**
 * @author Irene Mathew
 *
 */
public class SlidingWindowImplementationTest {
	private List<WeatherData> presentYearList;
	private List<WeatherData> previousYearList;
	private SlidingWindowImplementation algorithm;

	@Before
	public void setUp() throws Exception {
		populatePreviousYearData();
		populatePresentYearData();
		algorithm = new SlidingWindowImplementation(this.previousYearList,
				this.presentYearList);
		algorithm.slidingWindowList = new ArrayList<SlidingWindow>();
	}

	@Test
	public void testvalidateNumberOfWindows_Pass() throws Exception {
		algorithm.divideIntoWindows(this.previousYearList);
		algorithm.validateNumberOfWindows(algorithm.slidingWindowList.size());
	}
	
	@Test(expected = WeatherException.class)
	public void testvalidateNumberOfWindows_Fail() throws Exception {
		algorithm.divideIntoWindows(this.previousYearList);
		algorithm.slidingWindowList.remove(0);
		algorithm.validateNumberOfWindows(algorithm.slidingWindowList.size());
	}
	
	@Test
	public void testvalidateWindowSize_Pass() throws Exception {
		algorithm.divideIntoWindows(this.previousYearList);
		algorithm.validateWindowSize(algorithm.slidingWindowList.get(0).getSlidingWindowMap().size());
	}

	@Test(expected = WeatherException.class)
	public void testvalidateWindowSize_Fail() throws Exception {
		algorithm.divideIntoWindows(this.previousYearList);
		String removeKey=algorithm.slidingWindowList.get(0).getSlidingWindowMap().lastKey();
		algorithm.slidingWindowList.get(0).getSlidingWindowMap().remove(removeKey);
		algorithm.validateWindowSize(algorithm.slidingWindowList.get(0).getSlidingWindowMap().size());
	}

	/**
	 * @throws IOException
	 * @throws ParseException
	 * 
	 */
	private void populatePreviousYearData() throws IOException, ParseException {
		String INPUT_PATH = "/previousYear";
		this.previousYearList = new ArrayList<WeatherData>();
		File file = new File(this.getClass().getResource(INPUT_PATH).getFile());
		List<String> lines = Files.readAllLines(file.toPath());
		for (String line : lines) {
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
			this.previousYearList.add(weatherData);
		}
	}

	/**
	 * @throws IOException
	 * @throws ParseException
	 * 
	 */
	private void populatePresentYearData() throws IOException, ParseException {
		String INPUT_PATH = "/presentYear";
		this.presentYearList = new ArrayList<WeatherData>();
		File file = new File(this.getClass().getResource(INPUT_PATH).getFile());
		List<String> lines = Files.readAllLines(file.toPath());
		for (String line : lines) {
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
			this.presentYearList.add(weatherData);
		}
	}

}
