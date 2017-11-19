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
 * Bean class for sliding window along with getters and setters..
 * 
 * @author Irene Mathew
 *
 */

import java.util.List;
import java.util.SortedMap;

public class SlidingWindow {

	private int windowNumber;

	private SortedMap<String, List<WeatherData>> slidingWindowMap;

	public int getWindowNumber() {
		return windowNumber;
	}

	public void setWindowNumber(int windowNumber) {
		this.windowNumber = windowNumber;
	}

	public SortedMap<String, List<WeatherData>> getSlidingWindowMap() {
		return slidingWindowMap;
	}

	public void setSlidingWindowMap(
			SortedMap<String, List<WeatherData>> slidingWindowMap) {
		this.slidingWindowMap = slidingWindowMap;
	}

}
