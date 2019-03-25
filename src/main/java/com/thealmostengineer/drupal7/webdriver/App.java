package com.thealmostengineer.drupal7.webdriver;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Perform the action of updating the database and doing database backup on a Drupal 
 * 7 website. 
 *
 * @author almostengineer
 * 
 */
public class App 
{
	static WebDriver driver = null;
	
	/**
	 * Prints a message to the console 
	 * 
	 * @param message	The message to write to the console
	 */
	static void logMessage(String message) {
		System.out.println(message);
	}
	
	/**
	 * Sets all of the timeouts used by the automation
	 * 
	 * @param wDriver Webdriver object
	 * @param timeInSeconds 	The timeout value in seconds
	 * @return
	 */
	static WebDriver setTimeouts(WebDriver wDriver, int timeInSeconds) {
		wDriver.manage().timeouts().implicitlyWait(timeInSeconds, TimeUnit.SECONDS);
		wDriver.manage().timeouts().pageLoadTimeout(timeInSeconds, TimeUnit.SECONDS);
		return wDriver;
	}
	
	/**
	 * Set the property for the Gecko driver location
	 * @param geckoDriverLocation	The file path to the geckodriver
	 */
	static void setDriverProperities(String geckoDriverLocation) {
		System.setProperty("webdriver.gecko.driver", geckoDriverLocation); // set location of gecko driver for Firefox
	}
	
	/**
	 * main function. Takes arugments from the command line.
	 * @param args		-w Website address
	 * 					-u Username
	 * 					-p password
	 * 					-g gecko driver file location
	 * 					-b backup Destination for Backup and Migrate module
	 * 					-d local Directory that contains files to be uploaded
	 */
    public static void main( String[] args )
    {
    	int exitCode = 1;
    	
        try {
        	logMessage("Start time: " + LocalDateTime.now().toString());
        	
        	// read in the arguments
        	// -w websiteaddress -u username -p password -g geckodriver location        	
        	String webAddress = null, userName = null, password = null, geckoLocation = null, backupDestination = null;
        	String localDirectory = null, archiveLocation = null;
			for(int counter = 1 ; counter <= args.length; counter++) {
        		if (args[counter-1].equals("-w")) {
        			webAddress = args[counter];
        		}
        		else if (args[counter-1].equals("-u")) {
        			userName = args[counter];
        		} 
        		else if (args[counter-1].equals("-p")) {
        			password = args[counter];
        		}
        		else if (args[counter-1].equals("-g")) {
        			geckoLocation = args[counter];
        		}
        		else if (args[counter-1].equals("-b")) {
        			backupDestination = args[counter];
        		}
        		else if (args[counter-1].equals("-d")) {
        			localDirectory = args[counter];
        		}
        		else if (args[counter-1].equals("-w")) {
        			webAddress = args[counter];
        		}
        		else if (args[counter-1].equals("-archive")) {
        			archiveLocation = args[counter];
        		}
        	}
        	
        	setDriverProperities(geckoLocation);
        	
//        	WebsiteUpdater.performUpdate(webAddress, userName, password, geckoLocation, backupDestination);
        	
        	PhotoUploader.performFileUpload(localDirectory, archiveLocation, webAddress, userName, password);
        	
        	logMessage("Closing browser");
//        	driver.quit(); // close the browser if all goes well
        	logMessage("Process completed successfully");
        	exitCode = 0;
		} catch (Exception e) {
			logMessage("Process failed");
			logMessage(e.getMessage());
			e.printStackTrace();
		}
        
        logMessage("End time: " + LocalDateTime.now().toString());
        
        System.exit(exitCode);
    }
}
