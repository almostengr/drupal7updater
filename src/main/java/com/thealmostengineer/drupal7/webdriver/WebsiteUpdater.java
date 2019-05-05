package com.thealmostengineer.drupal7.webdriver;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class WebsiteUpdater extends App {

	/**
	 * Performs an action to take a backup using the Backup and Migrate module
	 * 
	 * @param backupDestination		The display name of the destination for the backup
	 * @param driver				The webdriver object
	 * @throws Exception
	 */
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
		} // end if
		logMessage("Done performing database backup");
	} // end function
	
	/**
	 * Performs code and database updates on a Drupal 7 website.
	 * 
	 * @param websiteUrl			The URL to the Drupal website
	 * @param username				The username that access to perform updates on the website
	 * @param password				The password associated with the username.
	 * @param geckoDriverLocation	The file path to the Gecko driver
	 * @param backupDestination		The display name of the destination for the backup
	 * @throws Exception
	 */
	public static void performUpdate(String websiteUrl, String username, String password, String geckoDriverLocation, String backupDestination) throws Exception {
		WebDriverSetup webDriverSetup = new WebDriverSetup();
		
		if (websiteUrl == null || username == null || password == null || geckoDriverLocation == null) {
    		throw new Exception("All of the required arguments are not present.");
    	} // end if
		
		webDriverSetup.setDriverProperities(geckoDriverLocation);
		
		int timeOutSeconds = 30;
		driver = new FirefoxDriver(); // start the browser
		driver.manage().window().maximize(); // maximize window
		driver = webDriverSetup.setTimeouts(driver, timeOutSeconds); // set timeouts
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
		} // end if
		
		// go the Reports  > Available Updates > Update page to see if there are updates available
		
		driver.findElement(By.id("toolbar-link-admin-reports")).click();
		driver.findElement(By.linkText("Available updates")).click();
		
		driver = webDriverSetup.setTimeouts(driver, 4);
		
		if (driver.findElements(By.linkText("UPDATE")).size() > 0) {
			driver.findElement(By.linkText("UPDATE")).click();
		}
		else {
			driver.findElement(By.linkText("Update")).click();
		} // end if
		
		driver = webDriverSetup.setTimeouts(driver, timeOutSeconds);
		
		if (driver.getPageSource().contains("All of your projects are up to date.")) {
			logMessage("All of your projects are up to date.");
		}
		else if (driver.getPageSource().contains("Installed version")) {
			
			if (driver.getPageSource().contains("Drupal core")) {
				throw new Exception("Drupal core needs to be updated. Manual intervention required.");
			} // end if
			
			// module updater
			List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@title='Select all rows in this table']"));
			for(int i = 0; i<checkboxes.size(); i++) {
				WebElement currentCheckbox = checkboxes.get(i);
				currentCheckbox.click(); // select the checkbox
			} // end for
			
			driver.findElement(By.id("edit-submit")).click(); // Download these updates button
			
			// if issue with download updated modules, then fail
			if (driver.getPageSource().contains("An error has occurred.")) {
				throw new Exception("Unable to update the out of date modules. Manual intervention required.");
			} // end if
		}
		else {
			throw new Exception("The update does not have the expected text.");
		} // end if
		    		
		if (backupDestination != null) {
			performBackup(backupDestination, driver);
		} // end if
		
		// update database page
		driver.get(websiteUrl + "/update.php");
		if (driver.getPageSource().contains("Drupal database update") == false) {
			throw new Exception("Unable to access database update page");
		} // end if
		
		driver.findElement(By.xpath("//input[@type='submit']")).click(); // click Continue button
		
		logMessage("Checking for database updates");
		driver = webDriverSetup.setTimeouts(driver, 5);
		int submitBtnCount = driver.findElements(By.id("edit-submit")).size();
		driver = webDriverSetup.setTimeouts(driver, timeOutSeconds);
		
		if (submitBtnCount > 0) {
			logMessage("Applying database updates");
			driver.findElement(By.id("edit-submit")).click(); // click Apply pending updates button
			
			if (driver.getPageSource().toLowerCase().contains("failed")) {
				throw new Exception("Database updates failed"); // throw error if database updates fail
			} // end if
			
			logMessage("Done applying database updates");
		}
		else if (driver.getPageSource().contains("No pending updates.")) {
			logMessage("No pending database updates.");
		}
		else {
			throw new Exception("Unable to determine whether database updates need to be performed.");
		} // end if
		
		driver.findElement(By.linkText("Front page")).click(); // go to front page
		driver.findElement(By.linkText("Log out")).click(); // logout 
	} // end function
}
