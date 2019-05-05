package com.thealmostengineer.drupal7.webdriver;

import java.time.LocalDateTime;
import java.util.Properties;

import org.openqa.selenium.WebDriver;

/**
 * Perform the action of updating the database and doing database backup on a Drupal 
 * 7 website. 
 *
 * @author almostengineer
 * 
 */
public class UserInterface 
{
	static WebDriver driver = null;
	
	/**
	 * Prints a message to the console 
	 * 
	 * @param message	The message to write to the console
	 */
	static void logMessage(String message) {
		System.out.println(message);
	} // end function
		
	/**
	 * main function. Takes arugments from the command line.
	 * @param args		-w Website address
	 * 					-u Username
	 * 					-p password
	 * 					-g gecko driver file location
	 * 					-b backup Destination for Backup and Migrate module
	 * 					-d local Directory that contains files to be uploaded
	 */
    public static void main( String[] args ) {
    	logMessage("Start time: " + LocalDateTime.now().toString());
    	int exitCode = 1;
    	
        try {
        	// read in the arguments
        	// -w websiteaddress -u username -p password -g geckodriver location        	
        	String webAddress = null, userName = null, password = null, geckoLocation = null, backupDestination = null;
        	String localDirectory = null, archiveLocation = null, programToRun = null;
        	WebDriverSetup webDriverSetup = new WebDriverSetup();
        	
        	if (args[0].isEmpty()) {
        		throw new Exception("Path to properties file was not provided.");
        	} // end function
    	
        	PropertiesFileHandler propertiesFileHandler = new PropertiesFileHandler();
        	Properties properties = propertiesFileHandler.readPropertyFile(args[0]);
        	
        	// put arguments into local variables
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
        		else if (args[counter-1].equals("-t")) {
        			programToRun = args[counter];
        		} // end else if
        	} // end for

//        	setDriverProperities(geckoLocation);
			webDriverSetup.setDriverProperities(geckoLocation);

			// run the appropriate program or exit
			if (programToRun.equals("uploadphotos")) {
				PhotoUploader.performFileUpload(localDirectory, archiveLocation, webAddress, userName, password);
			}
			else if (programToRun.equals("websiteupdate")) {
				WebsiteUpdater.performUpdate(webAddress, userName, password, geckoLocation, backupDestination);
			}
			else if (properties.getProperty("programToRun").equals("uploadphotos")) {
				PhotoUploader.performFileUpload(properties.getProperty("localDirectory"), 
						properties.getProperty("archiveDirectory"), properties.getProperty("webAddress"), 
						properties.getProperty("username"), properties.getProperty("password"));
			}
			else if (properties.getProperty("programToRun").equals("websiteupdate")) {
				WebsiteUpdater.performUpdate(properties.getProperty("webAddress"), 
						properties.getProperty("username"), properties.getProperty("password"),
						properties.getProperty("geckoLocation"), properties.getProperty("backupDestination"));
			}
			else {
				throw new Exception("No program selected to run");
			} // end if
			
        	logMessage("Closing browser");
        	driver.quit(); // close the browser if all goes well
        	
        	logMessage("Process completed successfully");
        	exitCode = 0;
		} catch (Exception e) {
			logMessage("Process failed");
			logMessage(e.getMessage() + System.getProperty("line.separator"));
			e.printStackTrace();
		} // end try catch
        
        logMessage("End time: " + LocalDateTime.now().toString());
        System.exit(exitCode);
    } // end function
}
