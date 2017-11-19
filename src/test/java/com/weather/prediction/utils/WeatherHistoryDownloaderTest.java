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

import static com.weather.prediction.constants.WeatherConstants.BASE_URL;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.weather.prediction.exception.WeatherException;

/**
 * @author Irene Mathew
 *
 */
public class WeatherHistoryDownloaderTest {

	private static String location;
	private static String coordinates;
	private static String url;
	private static String stationId;
	private static String monthYear;
	WeatherHistoryDownloader downloader;
	private static List<String> expectedList;
	
	@BeforeClass
	public static void setUpOnce() throws ParseException, IOException {
		coordinates = "-35.28,149.13,57.5";
		location = "CANBERRA";
		stationId="IDCJDW2801";
		monthYear="201710";
		url="http://www.bom.gov.au/climate/dwo/201710/text/IDCJDW2801.201710.csv";
		
	}
	
	 @Before
	   public void initialize() {
		downloader = new WeatherHistoryDownloader(
					location);
		downloader.dataLines= new ArrayList<String>();
	 }

	@Test(expected = WeatherException.class)
	public void testCallAPIUrl_Fail() throws ParseException, WeatherException {
		String inCorrectUrl = "http://www.bom.gov.au/climate/dwo/201708/text/ID7892.201708.csv";
		downloader.callAPIUrl(inCorrectUrl, coordinates);
	}
	@Test
	public void testGetLocationDetailsFromLookUp_Pass() throws IOException{
		assertEquals("IDCJDW2801,-35.28,149.13,57.5",downloader.getLocationDetailsFromLookUp());
	}
	
	@Test
	public void testCreateUrl_Pass() throws IOException{
		assertEquals(url,downloader.createUrl(BASE_URL,stationId, monthYear));
	}
	
	@Test
	public void test_callAPIUrl_Pass() throws ParseException, WeatherException, IOException {
		File file = new File(this.getClass().getResource("/testOutput").getFile());
		expectedList = Files.readAllLines(file.toPath());
		downloader.callAPIUrl(url, coordinates);
		assertEquals(expectedList.size(),downloader.dataLines.size());
	}

}
