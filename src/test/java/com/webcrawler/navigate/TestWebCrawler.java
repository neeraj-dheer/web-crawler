package com.webcrawler.navigate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Test;

import com.webcrawler.html.HtmlParser;
import com.webcrawler.utils.FilterPredicate;

public class TestWebCrawler {
	
	WebCrawler crawl = new WebCrawler("mydomain.com");
	
	@Test
	@SuppressWarnings("unchecked")
	public void testOneCrawl(){
		HtmlParser parser = mock(HtmlParser.class);
		
		when(parser.getAHrefs(any(String.class))).thenReturn(Arrays.asList("next.url", "one.more"))
		.thenReturn(Collections.EMPTY_LIST).thenReturn(Collections.EMPTY_LIST);
		
		crawl.setHtmlParser(parser);
		
		Predicate<String> predicate = mock(FilterPredicate.class);
		when(predicate.test(any(String.class))).thenReturn(true);
		
		crawl.setFilterLinksPredicate(predicate);
		Map<String, List<String>> results = crawl.crawl("test.url");
		assertEquals(3, results.size());
		List<String> links = results.get("test.url");
		assertEquals(2, links.size());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDuplicateLink(){
		HtmlParser parser = mock(HtmlParser.class);
		
		when(parser.getAHrefs(any(String.class))).thenReturn(Arrays.asList("next.url", "one.more", "next.url"))
		.thenReturn(Collections.EMPTY_LIST).thenReturn(Collections.EMPTY_LIST);
		
		crawl.setHtmlParser(parser);
		
		Predicate<String> predicate = mock(FilterPredicate.class);
		when(predicate.test(any(String.class))).thenReturn(true);
		
		crawl.setFilterLinksPredicate(predicate);
		Map<String, List<String>> results = crawl.crawl("test.url");
		assertEquals(3, results.size());
		List<String> links = results.get("test.url");
		assertEquals(3, links.size());
	}

}