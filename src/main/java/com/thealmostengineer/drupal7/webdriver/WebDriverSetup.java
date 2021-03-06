package com.thealmostengineer.drupal7.webdriver;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;

public class WebDriverSetup {

	/**
	 * Sets all of the timeouts used by the automation
	 * 
	 * @param wDriver Webdriver object
	 * @param timeInSeconds 	The timeout value in seconds
	 * @return
	 */
	WebDriver setTimeouts(WebDriver wDriver, int timeInSeconds) {
		wDriver.manage().timeouts().implicitlyWait(timeInSeconds, TimeUnit.SECONDS);
		wDriver.manage().timeouts().pageLoadTimeout(timeInSeconds, TimeUnit.SECONDS);
		return wDriver;
	} // end function
	
	/**
	 * Set the property for the Gecko driver location
	 * @param geckoDriverLocation	The file path to the geckodriver
	 */
	void setDriverProperities(String geckoDriverLocation) {
		System.setProperty("webdriver.gecko.driver", geckoDriverLocation); // set location of gecko driver for Firefox
	} // end function
}
