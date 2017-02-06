package com.webcrawler.navigate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.webcrawler.html.SimpleHtmlParser;
import com.webcrawler.utils.FilterPredicate;

/**
 * core web crawler class.
 * takes a domain and a url and crawls for all available links in the same domain
 * finding available links is delegated to an html parser
 * crawling is completed when either:
 * 1. max specified links are traversed - specified by VISIT_MAX
 * 2. all links have been traversed
 */
public class WebCrawler {
	
	private Map<String, List<String>> siteMap = new ConcurrentHashMap<>();
	private final int MAX_DOWNLOADERS = 1;
	private long maxVisits = 1000;
	private Predicate<String> filterLinksPredicate; 
	private AtomicLong totalVisited = new AtomicLong();
	private BlockingQueue<String> toVisit = new LinkedBlockingQueue<>();

	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private HtmlParser htmlParser = new SimpleHtmlParser();
	private String domain;
	

	/**
	 * within the web crawler, src would be the url of the page to crawl.
	 * @param src
	 */
	public Map<String, List<String>> crawl(String src, long maxVisits){

		try{
			if(maxVisits > 0){
				this.maxVisits = maxVisits;
			}
			URL start = new URL(src);
			domain = start.getHost();
			filterLinksPredicate = new FilterPredicate(domain);
		}catch(MalformedURLException e){
			e.printStackTrace();
			throw new IllegalArgumentException("Malformed url provided: " + src);
		}
		
		

		visit(src);
		return siteMap;

	}

	public void visit(String url){
		
		try {
			toVisit.put(url);
		} catch (InterruptedException e) {
			//should not happen since the queue is empty and we havent started
			//any consumers yet - but if it does and we cant put the start url in
			//queue, then might as well stop right away
			throw new IllegalStateException("Cannot insert base url in queue.", e);
		}
		
		for(int i = 0; i < MAX_DOWNLOADERS; i++) {
			executor.execute(new LinksAdder(toVisit));
		}
		
		try {
			boolean term = executor.awaitTermination(1, TimeUnit.DAYS); // shouldnt come to await for so long, but adjust to provide for current sites to complete
			if(!term){
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			//main executor interrupted - stop and get out with whatever we have
			e.printStackTrace();
		}
	}
	
	/**
	 * calculates whether we need to crawl further based on:
	 * 1. total urls crawled (should be less than MAX configured) OR
	 * 2. the blocking queue containing the urls is empty, ie all urls have been traversed
	 * 
	 * all threads downloading sites and crawling are expected to check with thie method whether they should continue to crawl or stop
	 * @return true - if further crawling is required
	 */
	private boolean crawlRequired() {
		boolean reqd = ((totalVisited.get() < maxVisits) && !toVisit.isEmpty());
		if(!reqd) {
			executor.shutdown();
		}
		System.out.println("crawl required:" + reqd);
		
		return reqd;
	}
	
	public HtmlParser getHtmlParser() {
		return htmlParser;
	}

	public void setHtmlParser(HtmlParser htmlParser) {
		this.htmlParser = htmlParser;
	}

	public Predicate<String> getFilterLinksPredicate() {
		return filterLinksPredicate;
	}

	public void setFilterLinksPredicate(Predicate<String> filterLinksPredicate) {
		this.filterLinksPredicate = filterLinksPredicate;
	}

	/**
	 * runnable download a site and then add the newly found links from that site to the blocking queue
	 * 
	 */
	private class LinksAdder implements Runnable{
		
		private final BlockingQueue<String> queue;
		private volatile boolean shouldRun = true;
		
		LinksAdder(BlockingQueue<String> q){
			queue = q;
		}
		
		public void run(){
			System.out.println("Starting crawler thread:" + Thread.currentThread().getName());
			while(shouldRun){
				try{
					String url = queue.take();
					System.out.println("got url:[" + url + "]");
					List<String> links = htmlParser.getAHrefs(url);
					System.out.println("got links:" + links);
					siteMap.put(url, links);
					Set<String> distinctLinks = links.stream()
													.filter(filterLinksPredicate)
													.collect(Collectors.toSet());
					System.out.println("traversable set has:" + distinctLinks);
					queue.addAll(distinctLinks);
					totalVisited.addAndGet(distinctLinks.size());
					checkShouldRun();
				}catch(InterruptedException ie){
					//being interrupted, so probably want to stop
					shouldRun = false;
					Thread.currentThread().interrupt();
				}catch(Exception e){
					//some other exception = possibly related to the url like:
					//1. url doesnt exist
					//2. html parsing exception etc.
					//in this case, just log and move on to the next url
					e.printStackTrace();
				}
			}
		}
		
		private void checkShouldRun(){
			shouldRun = crawlRequired();
			
		}
	}
}