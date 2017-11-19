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
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.weather.prediction.bean.WeatherData;
import com.weather.prediction.bean.WeatherDataArchive;
import com.weather.prediction.exception.WeatherException;
import com.weather.prediction.utils.WeatherUtils;

/**
 * @author Irene Mathew
 *
 */
public class WeatherPredictorTest {
	private WeatherDataArchive weatherDataArchive;
	private WeatherPredictor weatherPredictor;
	private String outputPath;

	@Before
	public void setUp() throws Exception {
		loadWeatherHistory();
		outputPath="/out/";
		this.weatherPredictor = new WeatherPredictor(this.weatherDataArchive);
	}

	@Test
	public void testPredictWeatherforFiveDays_Pass() throws Exception {
		
		this.weatherPredictor.predictWeatherforFiveDays(outputPath);
		File file = new File(this.getClass().getResource("/HistoryAfterPrediction").getFile());
		List<String> expectedList = Files.readAllLines(file.toPath());
		assertEquals(expectedList.size()/2,this.weatherDataArchive.getWeatherArchive().size());
	}

	@Test(expected = NullPointerException.class)
	public void testPredictWeatherforFiveDays_Fail() throws ParseException,
			WeatherException, FileNotFoundException {
		WeatherPredictor weatherPredictor = new WeatherPredictor(null);
		weatherPredictor.predictWeatherforFiveDays(outputPath);
	}

	public void loadWeatherHistory() throws IOException, ParseException {
		String INPUT_PATH = "/InputDataSet.csv";
		this.weatherDataArchive=new WeatherDataArchive();
		File file = new File(this.getClass().getResource(INPUT_PATH).getFile());
		List<String> lines = Files.readAllLines(file.toPath());
		Map<String, List<WeatherData>> weatherArchiveMap = new HashMap<String, List<WeatherData>>();
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
			putToArchiveMap(weatherData, weatherArchiveMap);
		}
		this.weatherDataArchive.setWeatherArchive(weatherArchiveMap);
	}

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
