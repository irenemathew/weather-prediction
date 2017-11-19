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
package com.weather.prediction.exception;

/**
 * Class for handling exceptions that may occur across the project
 * 
 * @author Irene Mathew
 *
 */
public class WeatherException extends Exception {
	private static final long serialVersionUID = 1L;

	private final String customErrorMessage;

	/**
	 * @param message
	 *            Custom messages for each exceptions
	 */
	public WeatherException(String message) {
		this.customErrorMessage = message;
	}

	/**
	 * @param cause
	 *            Cause of the exception
	 * @param message
	 *            Custom messages for each exceptions
	 */
	public WeatherException(Throwable cause, String message) {
		super(cause);
		this.customErrorMessage = message;
	}

	@Override
	public String getMessage() {
		return this.customErrorMessage;
	}
}