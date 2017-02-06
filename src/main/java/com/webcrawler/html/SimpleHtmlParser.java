package com.webcrawler.html;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import com.webcrawler.navigate.HtmlParser;

/**
 * saves given url to local file first, then parses for a hrefs.
 * could also use buffered reading from the HttpConnection stream 
 *
 */
public class SimpleHtmlParser implements HtmlParser{

	private static long ctr = 0;
	
	public List<String>getAHrefs(String url){
		
		List<String> hrefs = Collections.EMPTY_LIST;
		File tempDir = FileUtils.getTempDirectory();
		File f = new File(tempDir.getAbsolutePath() + File.separatorChar + ctr++);
		try {
			
			//copy url to disk since html may be too big to hold in memory
			FileUtils.copyURLToFile(new URL(url), f, 1000, 1000);
			hrefs = parseFileForLinks(f);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not parse url :" + url, e);
		} 
		
		try {
			FileUtils.forceDelete(f);
		} catch (IOException e) {
			System.out.println("Could not delete temporary file : " + f.getAbsolutePath());
			e.printStackTrace();
		}
		
		return hrefs;
	}
	
	/**
	 * parses a given file for a href links.
	 * uses a simple pattern-match and assumes a and href are on the same line.
	 * should be a private method to hide implementation details
	 * but is more of a generic utility, so could be part of a "Utils" class
	 * once parameter is changed from File to InputStream/Reader 
	 * @param f the file to be parsed for links
	 * @return list of links (a hrefs) found
	 * @throws IOException for any IO-related exception
	 */
	public static List<String> parseFileForLinks(File f) throws IOException{
		List<String> hrefs = new ArrayList<>();

		LineIterator iterator = FileUtils.lineIterator(f);
		iterator.forEachRemaining( s -> {
			String line = StringUtils.replace(StringUtils.deleteWhitespace(s), "\"", "'");
			if(line.matches(".*a.*href='.*")){
				System.out.println("one line:" + line);
				String link = StringUtils.substringsBetween(line, "href='", "'")[0];
				hrefs.add(link);
			}
				
		});
		iterator.close();
		
		return hrefs;
	}
	
	
}
