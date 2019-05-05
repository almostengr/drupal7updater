package com.thealmostengineer.drupal7.webdriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFileHandler {
	Properties readPropertyFile(String filePath) {
		App.logMessage("Loading properties file " + filePath);
		Properties properties = new Properties();
		File file = new File(filePath);
		FileReader fileReader = null;
		
		try {
			fileReader = new FileReader(file);
			properties.load(fileReader);
			fileReader.close(); // close properties file
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			properties = null;
		} catch(IOException e) {
			e.printStackTrace();
			properties = null;
		} // end try
		
		return properties;
	} // end function
}
