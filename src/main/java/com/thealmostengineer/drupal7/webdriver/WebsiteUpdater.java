package com.thealmostengineer.drupal7.webdriver;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class WebsiteUpdater extends App {

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
	
	public static void performUpdate(String websiteUrl, String username, String password, String geckoDriverLocation, String backupDestination) throws Exception {
		
		if (websiteUrl == null || username == null || password == null || geckoDriverLocation == null) {
    		throw new Exception("All of the required arguments are not present.");
    	}
		
		System.setProperty("webdriver.gecko.driver", geckoDriverLocation); // set location of gecko driver for Firefox
		
		int timeOutSeconds = 30;
		driver = new FirefoxDriver(); // start the browser
		driver.manage().window().maximize(); // maximize window
		driver = setTimeouts(driver, timeOutSeconds); // set timeouts
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
		
		driver = setTimeouts(driver, 4);
		
		if (driver.findElements(By.linkText("UPDATE")).size() > 0) {
			driver.findElement(By.linkText("UPDATE")).click();
		}
		else {
			driver.findElement(By.linkText("Update")).click();
		}
		
		driver = setTimeouts(driver, timeOutSeconds);
		
		if (driver.getPageSource().contains("All of your projects are up to date.")) {
			logMessage("All of your projects are up to date.");
		}
		else if (driver.getPageSource().contains("Installed version")) {
			
			if (driver.getPageSource().contains("Drupal core")) {
				throw new Exception("Drupal core needs to be updated. Manual intervention required.");
			}
			
			// module updater
			List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@title='Select all rows in this table']"));
			for(int i = 0; i<checkboxes.size(); i++) {
				WebElement currentCheckbox = checkboxes.get(i);
				currentCheckbox.click(); // select the checkbox
			}
			
			driver.findElement(By.id("edit-submit")).click(); // Download these updates button
			
			// if issue with download updated modules, then fail
			if (driver.getPageSource().contains("An error has occurred.")) {
				throw new Exception("Unable to update the out of date modules. Manual intervention required.");
			}
		}
		else {
			throw new Exception("The update does not have the expected text.");
		}
		    		
		if (backupDestination != null) {
			performBackup(backupDestination, driver);
		} // end if
		
		// update database page
		driver.get(websiteUrl + "/update.php");
		if (driver.getPageSource().contains("Drupal database update") == false) {
			throw new Exception("Unable to access database update page");
		}
		
		driver.findElement(By.xpath("//input[@type='submit']")).click(); // click Continue button
		
		logMessage("Checking for database updates");
		driver = setTimeouts(driver, 5);
		int submitBtnCount = driver.findElements(By.id("edit-submit")).size();
		driver = setTimeouts(driver, timeOutSeconds);
		
		if (submitBtnCount > 0) {
			logMessage("Applying database updates");
			driver.findElement(By.id("edit-submit")).click(); // click Apply pending updates button
			
			if (driver.getPageSource().toLowerCase().contains("failed")) {
				throw new Exception("Database updates failed"); // throw error if database updates fail
			}
			
			logMessage("Done applying database updates");
		}
		else if (driver.getPageSource().contains("No pending updates.")) {
			logMessage("No pending database updates.");
		}
		else {
			throw new Exception("Unable to determine whether database updates need to be performed.");
		}
		
		driver.findElement(By.linkText("Front page")).click(); // go to front page
		driver.findElement(By.linkText("Log out")).click(); // logout 
	}
}
