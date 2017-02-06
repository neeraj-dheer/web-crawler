package com.webcrawler.utils;

import java.util.function.Predicate;

/**
 * Simple filter to filter links to navigate.
 * Check if link file ends with htm(l) - if it does,
 * assume we want to follow the link.
 * Filter out any other link files like jpg etc.
 *
 */
public class FilterPredicate implements Predicate<String> {

	private final String pattern;
	
	
	public FilterPredicate(String domain){
		if(domain == null){
			throw new IllegalArgumentException("Domain cannot be null");
		}
		pattern = ".*" + domain + ".*\\.htm[l?]";

	}
	
	@Override
	public boolean test(String t) {
	
		return (t == null ? false : t.matches(pattern));
	}

}