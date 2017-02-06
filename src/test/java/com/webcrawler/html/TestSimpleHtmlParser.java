package com.webcrawler.html;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

public class TestSimpleHtmlParser {

	SimpleHtmlParser parser = new SimpleHtmlParser(); //keep this for future tests
	
	@Test
	public void testFileParse() throws IOException{
		URL fileUrl = this.getClass().getResource("links1.html");
		System.out.println(fileUrl);
		List<String> links = 	SimpleHtmlParser.parseFileForLinks(new File(fileUrl.getFile()));
		assertEquals(4, links.size());
		assertThat(links, hasItems(
				"http://www.mydomain.com/proj1/test1.html",
				"http://www.mydomain.com/proj1/test2.jpg",
				"http://www.someother.com/theirproj/test3.html",
				"http://www.mydomain.com/proj2/test4.htm"
				));
	}
	
	@Test(expected=java.io.IOException.class)
	public void fileDoesntExist() throws IOException{
		SimpleHtmlParser.parseFileForLinks(new File("doesnt exist"));
			
	}
}
