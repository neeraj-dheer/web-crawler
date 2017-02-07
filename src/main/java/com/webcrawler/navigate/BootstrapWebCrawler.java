package com.webcrawler.navigate;

import java.util.List;
import java.util.Map;

/**
 * simple class to start the web crawler based on inputs
 * from the command line. 
 *
 *Usage:
 *java BootstrapWebCrawler <site to crawl>
 *
 */
public class BootstrapWebCrawler {

	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Invalid Arguments for web crawler");
			System.out.println("Usage: java <java options> BootstrapWebCrawler <site to crawl> [max visits]");
			System.out.println("Java options include classpath etc if required.");
			System.out.println("site is mandatory. max visits (number of maximum to visit) is optional. defaults to 1000");
			System.exit(1);
		}

		String site = args[0];
		long maxVisits = -1;
		if(args.length > 1){
			try{
				maxVisits = Long.parseLong(args[1].trim());
			}catch(Exception e){
				System.out.println("Max visits not a number. Defaulting to 1000. max visit provided:" + args[1]);
			}
		}
		
		WebCrawler crawler = new WebCrawler();
		
		Map<String, List<String>> crawlResults = crawler.crawl(site, maxVisits);
		prettyPrint(crawlResults);
		
	}

	private static void prettyPrint(Map<String, List<String>> toPrint){
		System.out.println("--------------------------------------------");
		toPrint.forEach( (entry, value) -> {
			System.out.println("--" + entry);
			value.forEach(val -> System.out.println("----" + val));
		});
		System.out.println("--------------------------------------------");
	}
}
