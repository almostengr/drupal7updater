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
	
    public static void main( String[] args )
    {
    	int exitCode = 1;
    	
        try {
        	// read in the arguments
        	// -w websiteaddress -u username -p password -g geckodriver location        	
        	String webAddress = null, userName = null, password = null, geckoLocation = null, backupDestination = null;
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
        	}
        	
        	WebsiteUpdater.performUpdate(webAddress, userName, password, geckoLocation, backupDestination);
        	logMessage("Closing browser");
        	driver.quit(); // close the browser if all goes well
        	logMessage("Update was successful");
        	exitCode = 0;
		} catch (Exception e) {
			logMessage("Update failed");
			logMessage(e.getMessage());
			e.printStackTrace();
		}
        
        System.exit(exitCode);
    }
}
