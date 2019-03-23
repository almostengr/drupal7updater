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
	static String websiteUrl = "http://blueprint/almostengineer/almostengrwebsite/";
	static String username = "webdriverupdater";
	static String password = "webdriverupdater";
	
	static WebDriver setTimeouts(WebDriver wDriver) {
		int timeoutInSeconds = 30;
		wDriver.manage().timeouts().implicitlyWait(timeoutInSeconds, TimeUnit.SECONDS);
		wDriver.manage().timeouts().pageLoadTimeout(timeoutInSeconds, TimeUnit.SECONDS);
		return wDriver;
	}
	
    public static void main( String[] args )
    {
    	int exitCode = 1;
    	WebDriver driver = null;
    	
        try {
    		System.setProperty("webdriver.gecko.driver", "/home/almostengineer/java-workspace/geckodriver");
    		
    		driver = new FirefoxDriver();
    		
    		driver.manage().window().maximize(); // maximize window
    		
    		driver = setTimeouts(driver); // set timeouts
    		
    		driver.get(websiteUrl + "/user"); // got to website
    		
    		// log in to the website
    		
    		driver.findElement(By.id("edit-name")).sendKeys(username); // username
    		driver.findElement(By.id("edit-pass")).sendKeys(password); // password
    		
//    		driver.findElement(By.id("edit-pass")).submit(); // click login button alternative
    		driver.findElement(By.id("edit-submit")).click(); // click login button
    		
    		// verify that user name is on page, thus login was successful
    		
    		if (driver.getPageSource().contains(username) == false) {
    			throw new Exception("Unable to login to the website");
    		}
    		
    		// go the Reports  > Update page to see if there are updates available 
    		driver.findElement(By.id("toolbar-link-admin-reports")).click();
    		driver.findElement(By.linkText("Available updates")).click();
    		
//    		driver.findElement(By.linkText("Update")).click();
    		driver.findElement(By.linkText("UPDATE")).click();
    		
    		if (driver.getPageSource().contains("All of your projects are up to date.")) {
    			System.out.println("All of your projects are up to date.");
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
    		driver.get(websiteUrl + "update.php");
    		if (driver.getPageSource().contains("Drupal database update") == false) {
    			throw new Exception("Unable to get to database update page");
    		}
    		
    		driver.findElement(By.xpath("//input[@type='submit']")).click(); // click Continue button
    		
    		// TODO add code for when updates are available
    		
    		if (driver.getPageSource().contains("No pending updates.") == false) {
    			System.out.println("No pending database updates.");
    		}
    		
    		// TODO add code for when updates are available
    		    		
    		driver.findElement(By.linkText("Front page")).click(); // go to front page
    		
    		driver.findElement(By.linkText("Log out")).click(); // logout 
    		
        	System.out.println("Update was successful");
        	exitCode = 0;
		} catch (Exception e) {
			System.out.println("Update failed");
			e.printStackTrace();
		}
        
        if (driver != null) { 
        	driver.quit(); // close the browser
        }
        
        System.exit(exitCode);
    }
}
