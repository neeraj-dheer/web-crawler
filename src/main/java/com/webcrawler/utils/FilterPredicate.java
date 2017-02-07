package com.webcrawler.utils;

import java.net.URL;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple filter to filter links to navigate.
 * Check if link file ends with htm(l) - if it does,
 * assume we want to follow the link.
 * Filter out any other link files like jpg etc.
 *
 */
public class FilterPredicate implements Predicate<String> {

	private final String domainPattern;
	
	
	public FilterPredicate(String domain){
		if(domain == null){
			throw new IllegalArgumentException("Domain cannot be null");
		}
		domainPattern = ".*" + domain + ".*";

	}
	
	@Override
	public boolean test(String t) {
	
		if(null == t){
			return false;
		}
		boolean matches = false;
		try{
			if(checkDomain(t)) {
				matches = true;
				URL site = new URL(t);
				String path = StringUtils.defaultString(site.getPath());
				if((path.contains("#")) ||
				(path.contains(".") && (! (path.endsWith(".htm") || path.endsWith(".html"))))) {
					matches = false;
				}
			
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return matches;
	}

	public boolean checkDomain(String t){
		return t.matches(domainPattern);
	}
	
}