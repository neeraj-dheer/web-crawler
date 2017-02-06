package com.webcrawler.navigate;

import java.util.List;

/**
 * simple parser interface to allow swapping of html implementations
 *
 */
public interface HtmlParser {

	public List<String>getAHrefs(String url);
}
