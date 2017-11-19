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
 * Bean class for weather record object along with getters and setters.
 * 
 * @author Irene Mathew
 *
 */

import static com.weather.prediction.constants.WeatherConstants.PIPE_DELIMITER;

import java.util.Date;

public class WeatherData {

	private String location;
	private String lat;
	private String longt;
	private String elevtn;
	private String dateTime;
	private Date date;
	private float pressure;
	private float humidity;
	private float temp;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	private String condition;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLongt() {
		return longt;
	}

	public void setLongt(String longt) {
		this.longt = longt;
	}

	public String getElevtn() {
		return elevtn;
	}

	public void setElevtn(String elevtn) {
		this.elevtn = elevtn;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public float getPressure() {
		return pressure;
	}

	public void setPressure(float pressure) {
		this.pressure = pressure;
	}

	public float getHumidity() {
		return humidity;
	}

	public void setHumidity(float humidity) {
		this.humidity = humidity;
	}

	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String toString() {
		return this.location + PIPE_DELIMITER + this.lat + PIPE_DELIMITER
				+ this.longt + PIPE_DELIMITER + this.elevtn + PIPE_DELIMITER
				+ this.dateTime + PIPE_DELIMITER + this.temp + PIPE_DELIMITER
				+ this.humidity + PIPE_DELIMITER + this.pressure
				+ PIPE_DELIMITER + this.condition;
	}

}
