Pre-requisites:

1. Java 8

2. Maven configured correctly to ensure dependencies can be downloaded

Run:

1. Once code has been downloaded, go to the project root directory: webcrawler

2. The project uses maven as its build tool, so once in the webcrawler directory, run: mvn install

3. The build runs the unit tests and should finish successfully.

4. The build also builds one jar which includes all project dependencies - This is done for convenience only.

5. Go to the maven repository and to the appropriate directory (in this case com/crawler/webcrawler).

6. Ensure the following jar file is present: webcrawler-0.0.1-SNAPSHOT-jar-with-dependencies.jar

7. Run the jar with the following command:
java -jar webcrawler-0.0.1-SNAPSHOT-jar-with-dependencies.jar http://wiprodigital.com

8. An optional parameter specifying the maximum number of links to be traversed can be provided
(this defaults to 1000 if not provided) by specifying it as the second parameter to the above command, like so:
java -jar webcrawler-0.0.1-SNAPSHOT-jar-with-dependencies.jar http://wiprodigital.com 500

9. The program should list out all sites traversed and the links found on each site traversed.

Things to be improved:

1. Tests: Minimal unit tests to test basic functionality written. Loads more tests need to be written.

2. Multi-threading support:
  Currently the visitor works in its own thread - but is single threaded at the moment.
  This can be multi-threaded by enabling correct "termination signalling" between the threads 
(should only be a change to the way the BlockingQueue is accessed)

3. Exception Handling:
  Currently, certain situations will cause code to block forever. This needs to be fixed by adding more fine grained
exception handling. Ex:- correctly define fatal vs non-fatal exceptions for the executor itself and its crawler thread(s).
Have used "available exceptions" and RuntimeException where no other suitable exception was found. 
A suitable exception hierarchy is required - both for meaningful exceptions as well as for #1 above.

4. Link status - Currently all links are recorded but the "visit" status of each is not.
So we do not know if any link could not be visited etc.
To implement this, rather than using List/Set of String urls, use a java object to:
  i)   have url
  ii)  have visit status - Enum - NOT_VISITED, SUCCESSFUL, EXCEPTION
  iii) store exception if any from the visit.

5. "Proper" html parser to be used. This implementation needs to be utilised by implementing the HtmlParser interface.
Also need to update filtering of sites based on domain from current simple pattern matching.

6. Logging - Bad habit of having used System.out.println and then changing to log statements 
should have been discouraged in favour of proper logging.