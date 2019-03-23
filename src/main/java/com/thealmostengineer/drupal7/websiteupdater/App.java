package com.thealmostengineer.drupal7.websiteupdater;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
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
//	static String websiteUrl = "http://blueprint/almostengineer/almostengrwebsite/";
//	static String username = "webdriverupdater";
//	static String password = "webdriverupdater";
//	final static String geckoDriverLocation = "/home/almostengineer/java-workspace/geckodriver";
	static WebDriver driver = null;
	
	/**
	 * Prints a message to the console 
	 * 
	 * @param message
	 */
	static void logMessage(String message) {
		System.out.println(message);
	}
	
	static void performUpdate(String websiteUrl, String username, String password, String geckoDriverLocation) throws Exception {
		System.setProperty("webdriver.gecko.driver", geckoDriverLocation); // set location of gecko driver for Firefox
		
		driver = new FirefoxDriver(); // start the browser
		driver.manage().window().maximize(); // maximize window
		driver = setTimeouts(driver); // set timeouts
		driver.get(websiteUrl + "/user"); // got to website
		
		// log in to the website

		driver.findElement(By.id("edit-name")).sendKeys(username); // username
		driver.findElement(By.id("edit-pass")).sendKeys(password); // password
//		driver.findElement(By.id("edit-pass")).submit(); // click login button alternative
		driver.findElement(By.id("edit-submit")).click(); // click login button
		
		// verify that user name is on page, thus login was successful
		
		if (driver.getPageSource().contains(username) == false) {
			throw new Exception("Unable to login to the website");
		}
		else {
			logMessage("Logged into website");
		}
		
		// go the Reports  > Available Updates > Update page to see if there are updates available
		
		driver.findElement(By.id("toolbar-link-admin-reports")).click();
		driver.findElement(By.linkText("Available updates")).click();
		driver.findElement(By.linkText("UPDATE")).click(); // may need to be lower case depending on Gecko Driver version
		
		if (driver.getPageSource().contains("All of your projects are up to date.")) {
			logMessage("All of your projects are up to date.");
		}
		    		
		// perform database backup
		driver.findElement(By.id("toolbar-link-admin-config")).click();
		driver.findElement(By.linkText("Backup and Migrate")).click();
		
		// change the destination
		WebElement destinationElement = driver.findElement(By.id("edit-destination-id"));
		Select destinationSelect = new Select(destinationElement);
		destinationSelect.selectByVisibleText("/var/tmp");
		
		driver.findElement(By.id("edit-submit")).click(); // clicking the Backup Now button
		
		// verify database backup was successful
		if (driver.getPageSource().contains("backed up successfully to") == false) {
			throw new Exception("Not able to perform database backup");
		}
		
		// update database page
		driver.get(websiteUrl + "/update.php");
		if (driver.getPageSource().contains("Drupal database update") == false) {
			throw new Exception("Unable to get to database update page");
		}
		
		driver.findElement(By.xpath("//input[@type='submit']")).click(); // click Continue button
		
		// TODO add code for when updates are available
		
		if (driver.getPageSource().contains("No pending updates.") == false) {
			logMessage("No pending database updates.");
		}
		
		// TODO add code for when updates are available
		    		
		driver.findElement(By.linkText("Front page")).click(); // go to front page
		
		driver.findElement(By.linkText("Log out")).click(); // logout 
	}
	
	/**
	 * Sets all of the timeouts used by the automation
	 * 
	 * @param wDriver Webdriver object
	 * @return
	 */
	static WebDriver setTimeouts(WebDriver wDriver) {
		int timeoutInSeconds = 30;
		wDriver.manage().timeouts().implicitlyWait(timeoutInSeconds, TimeUnit.SECONDS);
		wDriver.manage().timeouts().pageLoadTimeout(timeoutInSeconds, TimeUnit.SECONDS);
		return wDriver;
	}
	
    public static void main( String[] args )
    {
    	int exitCode = 1;
//    	WebDriver driver = null;
    	
        try {
        	// -w websiteaddress -u username -p password -g geckodriver location        	
        	String webAddress = null, userName = null, password = null, geckoLocation = null;
        	
        	for(int counter = 1 ; counter <= args.length; counter++) {
        		if (args[counter-1].equals("-w")) {
        			webAddress = args[counter];
        			logMessage("set webaddress: " + webAddress);
        		}
        		else if (args[counter-1].equals("-u")) {
        			userName = args[counter];
        			logMessage("set username: " + userName);
        		} 
        		else if (args[counter-1].equals("-p")) {
        			password = args[counter];
        			logMessage("set password: " + password);
        		}
        		else if (args[counter-1].equals("-g")) {
        			geckoLocation = args[counter];
        			logMessage("set gecko driver: " + geckoLocation);
        		}
        	}
        	
        	if (webAddress == null || userName == null || password == null || geckoLocation == null) {
        		throw new Exception("All of the required arugments are not present.");
        	}

        	performUpdate(webAddress, userName, password, geckoLocation);
        	logMessage("Update was successful");
        	exitCode = 0;
		} catch (Exception e) {
			logMessage("Update failed");
			logMessage(e.getMessage());
			e.printStackTrace();
		}
        
//        if (driver != null) { 
        	driver.quit(); // close the browser
//        }
        
        System.exit(exitCode);
    }
}
