package com.thealmostengineer.drupal7.webdriver;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.google.common.io.Files;

/**
 * Uploads photos to a custom Drupal website
 * 
 * @author almostengineer
 *
 */
public class PhotoUploader extends UserInterface {
	/**
	 * Uploads a folder of files to the website. 
	 * 
	 * @param localDirectory	The directory on the local machine that contains the image files
	 * @param archiveDirectory	The directory on the local machine that the files should be moved to once uploaded
	 * @param webAddress		The URL to the website
	 * @param username			The username to login to the website. Username needs to have access to create content.
	 * @param password			The password associated with the username.
	 * @throws Exception		Exceptions that are thrown from Webdriver for elements not being found on the page.
	 */
	public static void performFileUpload(String localDirectory, String archiveDirectory, String webAddress, String username, String password) throws Exception {
		WebDriverSetup webDriverSetup = new WebDriverSetup();
		int timeOutSeconds = 30;
		driver = new FirefoxDriver(); // start the browser
		driver.manage().window().maximize(); // maximize window
		driver = webDriverSetup.setTimeouts(driver, timeOutSeconds); // set timeouts
		driver.get(webAddress + "/user"); // got to website
		
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
		
		File folderPath = new File(localDirectory);
		for (final File fileName : folderPath.listFiles()) {
			logMessage("Uploading " + fileName.getAbsolutePath());
			
			driver.findElement(By.linkText("Add content")).click(); // click the Add Content link
			driver.findElement(By.linkText("Image")).click(); // select the Image content type
			
			driver.findElement(By.id("edit-title")).sendKeys(fileName.getName()); // enter the filename
			
			driver.findElement(By.id("edit-field-images-und-0-upload")).sendKeys(fileName.getAbsolutePath()); // select the file
			
			driver.findElement(By.id("edit-submit")).click(); // upload the file
			
			logMessage("Done uploading " + fileName.getAbsolutePath());

			logMessage("Moving file to archive");
			File archiveFile = new File(archiveDirectory + "/" + fileName.getName());
			Files.move(fileName, archiveFile);  // move the file so that it is not reprocessed on future runs
			logMessage("Done moving file to archive: " + archiveFile.getAbsolutePath());
		} // end for
		
		driver.findElement(By.linkText("Home")).click(); // go to homepage when done

//		logMessage("Logging out");
//		driver.findElement(By.linkText("Log out")).click(); // log out of site
	} // end function
}
