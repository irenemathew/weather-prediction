/******************************************************
 * Copyright (c) November 2017, Irene Mathew.
 * All Rights Reserved
 * 
 * This file can be redistributed and/or modified,
 * under the terms of the GNU General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or any later version.
 *******************************************************/
package com.weather.prediction.bean;

/**
 * Bean class for weather historical data object<Date,List<WeatherData>> along with getters and setters.
 * 
 * @author Irene Mathew
 *
 */

import java.util.List;
import java.util.Map;

public class WeatherDataArchive {

	private Map<String, List<WeatherData>> weatherArchive;

	public Map<String, List<WeatherData>> getWeatherArchive() {
		return weatherArchive;
	}

	public void setWeatherArchive(
			Map<String, List<WeatherData>> weatherArchiveMap) {
		this.weatherArchive = weatherArchiveMap;
	}

}
