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
 * Class which implements sliding window algorithm.
 * 
 * @author Irene Mathew
 *
 */

import static com.weather.prediction.constants.WeatherConstants.DATE_FORMAT;
import static com.weather.prediction.constants.WeatherConstants.WINDOW_COUNT;
import static com.weather.prediction.constants.WeatherConstants.WINDOW_SIZE;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.weather.prediction.bean.SlidingWindow;
import com.weather.prediction.bean.VariationVector;
import com.weather.prediction.bean.WeatherData;
import com.weather.prediction.exception.WeatherException;
import com.weather.prediction.utils.WeatherUtils;

public class SlidingWindowImplementation {

	private List<WeatherData> presentYearList;
	private List<WeatherData> lastYearList;
	public List<SlidingWindow> slidingWindowList;
	final static Logger logger = Logger
			.getLogger(SlidingWindowImplementation.class);

	public SlidingWindowImplementation(List<WeatherData> presentYearList,
			List<WeatherData> lastYearList) {

		this.presentYearList = presentYearList;
		this.lastYearList = lastYearList;
	}

	/**
	 * This function is the main function for implementing algorithm. It gives
	 * the predicted variation as output. It also calls many sub functions to
	 * calculate windows, minimum Euclidean distance, mean variation of current
	 * and last year and finally predicted variation.
	 * 
	 * @throws ParseException
	 *             is thrown when date is not correctly parsed
	 * @throws WeatherException
	 *             is thrown when algorithm specific conditions are not met
	 */
	public List<VariationVector> implementSlidingWindow()
			throws ParseException, WeatherException {
		divideIntoWindows(this.lastYearList);
		Map<String, List<WeatherData>> minDistanceWindowMap = findWindowWithMinimumEuclideanDistance();
		List<WeatherData> minDistanceWindowList = new ArrayList<WeatherData>();
		// adding all records of various times for each day to a single list
		for (String date : minDistanceWindowMap.keySet()) {
			minDistanceWindowList.addAll(minDistanceWindowMap.get(date));
		}

		VariationCalculator variationCalculator = new VariationCalculator(
				this.presentYearList, minDistanceWindowList);
		List<VariationVector> variationFactor = variationCalculator
				.findVariationFactor();
		return variationFactor;
	}

	/**
	 * This function calculates Euclidean distance between each window and
	 * current year record and finds the window with minimum Euclidean distance.
	 * 
	 * @return window with minimum Euclidean distance
	 */
	public Map<String, List<WeatherData>> findWindowWithMinimumEuclideanDistance() {
		SortedMap<Double, Integer> distanceMap = calculateMinimumEuclideanDistance();
		Map<String, List<WeatherData>> minDistanceWindowMap = new HashMap<String, List<WeatherData>>();
		double minDistanceKey = distanceMap.firstKey();
		// getting window number with minimum distance
		int windowNumber = distanceMap.get(minDistanceKey);
		logger.debug("Minimum window number:" + windowNumber);
		for (SlidingWindow slidingWindow : slidingWindowList) {
			if (windowNumber == slidingWindow.getWindowNumber()) {
				// assigning all weather records of minimum window to another
				// map for further calculation
				minDistanceWindowMap = slidingWindow.getSlidingWindowMap();
				logger.debug("Identified records for a window with minimum distance");
			}
		}

		return minDistanceWindowMap;

	}

	/**
	 * This function calculates Euclidean distance between each window and
	 * current year record. The result is assigned to a
	 * map<Distance,Window_Number> which will be sorted in ascending order based
	 * on distance.
	 * 
	 * @return Sorted Map having distance as key and window_number as value.
	 */

	private SortedMap<Double, Integer> calculateMinimumEuclideanDistance() {
		SortedMap<Double, Integer> distanceMap = new TreeMap<>();
		for (SlidingWindow slidingWindow : slidingWindowList) {
			int windowNumber = slidingWindow.getWindowNumber();
			Map<String, List<WeatherData>> dateBasedWindow = slidingWindow
					.getSlidingWindowMap();
			double sum = 0, distance = 0;
			int count = 0;
			for (String date : dateBasedWindow.keySet()) {
				// average of each record is calculated because records are
				// present for 2 forecast times a day
				float avgHumidityPresent = (this.presentYearList.get(count)
						.getHumidity() + this.presentYearList.get(count + 1)
						.getHumidity()) / 2;
				float avgPressurePresent = (this.presentYearList.get(count)
						.getPressure() + this.presentYearList.get(count + 1)
						.getPressure()) / 2;
				float avgTempPresent = (this.presentYearList.get(count)
						.getTemp() + this.presentYearList.get(count + 1)
						.getTemp()) / 2;
				float avgHumidityWindow = (dateBasedWindow.get(date).get(0)
						.getHumidity() + dateBasedWindow.get(date).get(1)
						.getHumidity()) / 2;
				float avgPressureWindow = (dateBasedWindow.get(date).get(0)
						.getPressure() + dateBasedWindow.get(date).get(1)
						.getPressure()) / 2;
				float avgTempWindow = (dateBasedWindow.get(date).get(0)
						.getTemp() + dateBasedWindow.get(date).get(1).getTemp()) / 2;
				sum += Math.pow((avgHumidityWindow - avgHumidityPresent), 2)
						+ Math.pow((avgPressureWindow - avgPressurePresent), 2)
						+ Math.pow((avgTempWindow - avgTempPresent), 2);
				count = count + 2;
			}

			distance = Math.round(Math.sqrt(sum) * 10000.0) / 10000.0;
			distanceMap.put(distance, windowNumber);
		}
		return distanceMap;
	}

	/**
	 * This function segments last year's records into various windows and is
	 * assigned to List<SlidingWindow>.
	 * 
	 * @param lastYearList
	 *            list of last year's records
	 */
	public void divideIntoWindows(List<WeatherData> lastYearList)
			throws ParseException, WeatherException {
		int totalSize = lastYearList.size();
		int numberOfDays = totalSize / 2; // Since 2 forecast times are present
		int counter = -1;
		slidingWindowList = new ArrayList<SlidingWindow>();
		for (int i = 0; i < totalSize - numberOfDays + 1; i = i + 2) {
			counter++;
			SlidingWindow slidingWindow = new SlidingWindow();
			SortedMap<String, List<WeatherData>> slidingWindowMap = new TreeMap<String, List<WeatherData>>();
			slidingWindow.setWindowNumber(counter);
			for (int j = 0; j < numberOfDays - 1; j = j + 2) {
				putToSlidingWindowMap(lastYearList.get(i + j), slidingWindowMap);
				putToSlidingWindowMap(lastYearList.get(i + j + 1),
						slidingWindowMap);
			}
			validateWindowSize(slidingWindowMap.size());
			slidingWindow.setSlidingWindowMap(slidingWindowMap);
			slidingWindowList.add(slidingWindow);
		}
		validateNumberOfWindows(slidingWindowList.size());
	}

	/**
	 * This function assigns weather data object to each window (
	 * <Date,List<WeatherData>>)
	 * 
	 * @param weatherData
	 *            weather record identified to add to a window
	 * @param slidingWindowMap
	 *            Map having records corresponding to a single window
	 * 
	 */
	private void putToSlidingWindowMap(WeatherData weatherData,
			SortedMap<String, List<WeatherData>> slidingWindowMap) {

		if (!slidingWindowMap.containsKey(WeatherUtils.dateToString(
				weatherData.getDate(), DATE_FORMAT))) {
			List<WeatherData> list = new ArrayList<WeatherData>();
			list.add(weatherData);
			slidingWindowMap.put(WeatherUtils.dateToString(
					weatherData.getDate(), DATE_FORMAT), list);
		} else {
			slidingWindowMap.get(
					WeatherUtils.dateToString(weatherData.getDate(),
							DATE_FORMAT)).add(weatherData);
		}
	}

	/**
	 * This function validates the number of records in a particular window.
	 * 
	 * @param windowSize
	 *            size of created window
	 * @throws WeatherException
	 *             is thrown when size of created window is not same as
	 *             expected.
	 */
	public void validateWindowSize(int windowSize) throws WeatherException {
		if (windowSize != WINDOW_SIZE)
			throw new WeatherException("Window Size(" + windowSize
					+ ") is not same as expected");

	}

	/**
	 * This function validates whether the number of windows formed is equal to
	 * 8. If not, an exception along with a custom message is thrown.
	 * 
	 * @param windowCount
	 *            Number of windows created
	 * @throws WeatherException
	 *             is thrown when number of windows is not same as expected
	 */
	public void validateNumberOfWindows(int windowCount)
			throws WeatherException {
		if (WINDOW_COUNT != windowCount)
			throw new WeatherException(
					"Total number of windows is not equal to" + windowCount);

	}
}
