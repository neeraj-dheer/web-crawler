package com.webcrawler.utils;

import java.util.function.Predicate;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestFilterPredicate {

	Predicate<String> predicate = new FilterPredicate("mydomain");
	
	@Test
	public void testHtmlFilteredIn(){
		boolean result = predicate.test("http://mydomain.com/test.html");
		assertTrue(result);
	}
	
	@Test
	public void testFilteredIn(){
		boolean result = predicate.test("http://mydomain.com/about-us");
		assertTrue(result);
	}

	@Test
	public void testJpgFilteredOut(){
		boolean result = predicate.test("http://mydomain.com/test.jpg");
		assertFalse(result);
	}

	@Test
	public void testNull(){
		boolean result = predicate.test(null);
		assertFalse(result);
	}

	@Test
	public void testDifferentDomain(){
		boolean result = predicate.test("http://somedomain.com/test.html");
		assertFalse(result);
	}
	
}