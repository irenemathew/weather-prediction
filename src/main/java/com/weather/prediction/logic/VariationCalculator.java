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
 * Class which does calculations related to variation vector.
 * 
 * @author Irene Mathew
 *
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.weather.prediction.bean.VariationVector;
import com.weather.prediction.bean.WeatherData;
import com.weather.prediction.exception.WeatherException;
import com.weather.prediction.utils.WeatherUtils;

public class VariationCalculator {
	private List<WeatherData> presentYearList;
	private List<WeatherData> minDistanceWindowList;

	public VariationCalculator(List<WeatherData> presentYearList,
			List<WeatherData> minDistanceWindowList) {
		this.presentYearList = presentYearList;
		this.minDistanceWindowList = minDistanceWindowList;
	}

	/**
	 * This function mainly calls various functions to calculate mean variation
	 * vector of previous and current year's record. Then predicted variation is
	 * calculated by the finding average of these 2 mean variation vectors.
	 * 
	 * @return predicted variation
	 */
	public List<VariationVector> findVariationFactor() throws WeatherException {
		List<VariationVector> variationFactor = new ArrayList<VariationVector>();
		List<VariationVector> meanPreviousVectorList = findMeanVariation(minDistanceWindowList);
		List<VariationVector> meanPresentVectorList = findMeanVariation(this.presentYearList);
		validateMeanVectorSize(meanPreviousVectorList, meanPresentVectorList);
		variationFactor = findAvgVariationFactor(meanPreviousVectorList,
				meanPresentVectorList);
		return variationFactor;

	}

	/**
	 * This function validates the mean vector size of last year's list with
	 * current year's list.
	 * 
	 * @param meanPreviousVectorList
	 *            mean vector of last year's list
	 * @param meanPresentVectorList
	 *            mean vector of current year's list
	 * 
	 * @throws WeatherException
	 *             is thrown when list size of present year and past year
	 *             doesn't match.
	 */
	private void validateMeanVectorSize(
			List<VariationVector> meanPreviousVectorList,
			List<VariationVector> meanPresentVectorList)
			throws WeatherException {
		if (meanPreviousVectorList.size() != meanPresentVectorList.size())
			throw new WeatherException(
					"Vector list size of previous year is not equal to present year's list");

	}

	/**
	 * This function calculates predicted variation for 2 forecast times a day
	 * by taking average of mean variation of present and last year.
	 * 
	 * @param meanPreviousVectorList
	 *            mean predicted variation of previous year
	 * @param meanPresentVectorList
	 *            mean predicted variation of present year
	 * @return final predicted variation vector for 2 times
	 */

	private List<VariationVector> findAvgVariationFactor(
			List<VariationVector> meanPreviousVectorList,
			List<VariationVector> meanPresentVectorList) {
		List<VariationVector> avgVariationFactorList = new ArrayList<VariationVector>();
		List<VariationVector> morningVectorList = Arrays.asList(
				meanPreviousVectorList.get(0), meanPresentVectorList.get(0));
		List<VariationVector> noonVectorList = Arrays.asList(
				meanPreviousVectorList.get(1), meanPresentVectorList.get(1));

		VariationVector morningFactor = calculateMean(morningVectorList);
		VariationVector noonFactor = calculateMean(noonVectorList);
		avgVariationFactorList.add(morningFactor);
		avgVariationFactorList.add(noonFactor);
		return avgVariationFactorList;
	}

	/**
	 * This function calculates the mean of variation vector list for a day. In
	 * this function, the mean calculation for morning and noon time has
	 * calculated separately.
	 * 
	 * @return mean of variation vector list containing 2 forecast times
	 */
	private List<VariationVector> findMeanVariation(List<WeatherData> dataList) {
		List<VariationVector> meanVariation = new ArrayList<VariationVector>();
		List<VariationVector> morningVectorList = findVariationVector(dataList,
				0, 2);
		List<VariationVector> noonVectorList = findVariationVector(dataList, 1,
				3);
		VariationVector meanMorningVariation = calculateMean(morningVectorList);
		VariationVector meanNoonVariation = calculateMean(noonVectorList);
		meanVariation.add(meanMorningVariation);
		meanVariation.add(meanNoonVariation);
		return meanVariation;
	}

	/**
	 * This function finds the mean of variation vector list formed at a
	 * particular time(morning/noon).
	 * 
	 * @param vectorList
	 *            list of vector list
	 * @return mean of variation vector list for one time
	 */

	private VariationVector calculateMean(List<VariationVector> vectorList) {
		VariationVector meanVariationVector = new VariationVector();
		String time = null;
		for (VariationVector vector : vectorList) {
			time = vector.getTime();
			float temperature = meanVariationVector.getTemperature()
					+ vector.getTemperature();
			meanVariationVector.setTemperature(temperature);
			float pressure = meanVariationVector.getPressure()
					+ vector.getPressure();
			meanVariationVector.setPressure(pressure);
			float humidity = meanVariationVector.getHumidity()
					+ vector.getHumidity();
			meanVariationVector.setHumidity(humidity);
		}
		meanVariationVector.setTemperature(meanVariationVector.getTemperature()
				/ vectorList.size());
		meanVariationVector.setHumidity(meanVariationVector.getHumidity()
				/ vectorList.size());
		meanVariationVector.setPressure(meanVariationVector.getPressure()
				/ vectorList.size());
		meanVariationVector.setTime(time);
		return meanVariationVector;
	}

	/**
	 * This function finds variation vector for a set of records by providing
	 * start and next positions. Since we have historical data twice a day in a
	 * single list, iteration is done for every alternate positions of the list
	 * based on given positions.
	 * 
	 * @param dataList
	 *            list of weather records
	 * @param startPosition
	 *            start position of the record
	 * @param nextPosition
	 *            next position of the record
	 * @return variation vector calculated from the list of weather records
	 */
	private List<VariationVector> findVariationVector(
			List<WeatherData> dataList, int startPosition, int nextPosition) {
		List<VariationVector> vectorList = new ArrayList<VariationVector>();
		WeatherData firstItem = dataList.get(startPosition);
		for (int i = nextPosition; i < dataList.size(); i = i + 2) {
			WeatherData secondItem = dataList.get(i);
			VariationVector vector = new VariationVector();
			vector.setTime(WeatherUtils.extractTimefromDateTime(secondItem
					.getDateTime()));
			vector.setTemperature(secondItem.getTemp() - firstItem.getTemp());
			vector.setPressure(secondItem.getPressure()
					- firstItem.getPressure());
			vector.setHumidity(secondItem.getHumidity()
					- firstItem.getHumidity());
			vectorList.add(vector);
			firstItem = secondItem;
		}
		return vectorList;
	}
}
