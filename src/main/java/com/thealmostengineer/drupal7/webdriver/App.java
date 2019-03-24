package com.thealmostengineer.drupal7.webdriver;

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
	 * @param message
	 */
	static void logMessage(String message) {
		System.out.println(message);
	}
	
	static void performBackup(String backupDestination, WebDriver driver) throws Exception {
		logMessage("Performing database backup");
		
		// perform database backup
		driver.findElement(By.id("toolbar-link-admin-config")).click();
		driver.findElement(By.linkText("Backup and Migrate")).click();
		
		// change the destination
		WebElement destinationElement = driver.findElement(By.id("edit-destination-id"));
		Select destinationSelect = new Select(destinationElement);
		destinationSelect.selectByVisibleText(backupDestination);;
		
		driver.findElement(By.id("edit-submit")).click(); // clicking the Backup Now button
		
		// verify database backup was successful
		if (driver.getPageSource().contains("backed up successfully to") == false) {
			throw new Exception("Not able to perform database backup.");
		}
		logMessage("Done performing database backup");
	}
	
	/**
	 * Sets all of the timeouts used by the automation
	 * 
	 * @param wDriver Webdriver object
	 * @return
	 */
	static WebDriver setTimeouts(WebDriver wDriver, int timeInSeconds) {
		wDriver.manage().timeouts().implicitlyWait(timeInSeconds, TimeUnit.SECONDS);
		wDriver.manage().timeouts().pageLoadTimeout(timeInSeconds, TimeUnit.SECONDS);
		return wDriver;
	}
	
	static void setDriverProperities(String geckoDriverLocation) {
		System.setProperty("webdriver.gecko.driver", geckoDriverLocation); // set location of gecko driver for Firefox
	}
	
    public static void main( String[] args )
    {
    	int exitCode = 1;
    	
        try {
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
        
        System.exit(exitCode);
    }
}
