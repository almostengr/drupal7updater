package com.thealmostengineer.drupal7.webdriver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFileHandler {
	Properties readPropertyFile(String fileName) throws IOException {
		App.logMessage("Loading properties file " + fileName);
		Properties properties = new Properties();
		File file = new File(fileName);
		FileReader fileReader = new FileReader(file);
		
		properties.load(fileReader);
		return properties;
	} // end function
}
