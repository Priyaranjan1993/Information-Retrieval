import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class Focused_Crawling {
	//set Max depth to crawl
	public static final int MAX_DEPTH = 6;
	//set Max number of links to crawl
	public static final int MAX_LINKS = 1000;
	//for storing unvisited URLs
	static Queue<String> urlListqueue = new LinkedList<String>();
	//to store URLs that are unique and in sequence
	static LinkedHashSet<String> trackerList = new LinkedHashSet<>();
	//depth of the crawling
	int crawlDepth = 0;
	
	private void fetchAllURLs(String uRL,int depth, String[] keywords) {
		// TODO Auto-generated method stub
		urlListqueue.add(uRL);
		//Given strings for focused crawling(Keywords)
		String str1= keywords[0];
		String str2 = keywords[1];
		INIT: while(urlListqueue.size()!=0)
		{
			/*To get proper URL from the string*/
			int x = urlListqueue.element().indexOf("\t");
			String str = urlListqueue.element().substring(8,x);
			int currentDepth = Integer.parseInt(str);
			String preInitialURL = urlListqueue.remove();
			String initialURL = preInitialURL.substring(x).trim();
			/*Add the url to the final set*/
			trackerList.add(initialURL);
			
			if((trackerList.size() < MAX_LINKS) && (depth != MAX_DEPTH+1))
			{
				 try {
					//to connect to the page
					Document doc = Jsoup.connect(initialURL).get();
					//delay of 1 second
					TimeUnit.SECONDS.sleep(1);
					//getting the content from the body
					Element body = doc.getElementById("bodyContent");
					Element innerbody = body.getElementById("mw-content-text");
					//applying proper filters to get valid URLs as per the question
					String filter = "a[href^=\"/wiki/\"]" + "a:not([href*=#])"+ "a:not([href*=:])" +
					"a[href]:not([href~=(?i)\\.pn?g$])"+"a[href]:not([href~=(?i)\\.tif$])" + "a[href]:not([href~=(?i)\\.sv?g$])" +
							"a[href]:not([href~=(?i)\\.jpe?g$])"+ "a[href]:not([href~=(?i)\\.jp?g$])";
					//getting all the urls from the url that is being processed
					Elements customProperLinks = innerbody.select(filter);
					//calculating Max depth reached while accessing the urls
					depth = currentDepth +1 ;
					crawlDepth = depth;
					
					for (Element link : customProperLinks)
					{
						//to verify that the page is in English and the keywords are in anchor text or text within a URL
						if(link.attr("abs:href").contains("en.wikipedia.org") && ((link.attr("abs:href").toLowerCase().contains(str1)) || (link.attr("abs:href").toLowerCase().contains(str2))
								 || ((link.text().toLowerCase().contains(str1.toLowerCase())) || (link.text().toLowerCase().contains(str2.toLowerCase())))))
						{
							//if max depth reached or max number of links reached, return to original loop
							if(trackerList.size() == MAX_LINKS || depth == MAX_DEPTH+1)
							{
								continue INIT;
							}
							else {
								//finally adding valid URLs to the queue and the set 
								urlListqueue.add("Depth - "+depth+"\t"+ link.attr("abs:href"));
								trackerList.add(link.attr("abs:href"));
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void writetoFile(String fileName) {
		// TODO Auto-generated method stub
		try {
			FileWriter write = new FileWriter(fileName);
			for(String str : trackerList)
			{
				//write all URLs to the file
				write.write(str);
				write.write(System.getProperty("line.separator"));
			}
			write.close();
			System.out.println("Finished Writing");
			System.out.println("Crawled Depth - "+crawlDepth);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		Focused_Crawling fcr = new Focused_Crawling();
		//seed url
		String URL = "https://en.wikipedia.org/wiki/Solar_eclipse";
		//Appending depth to the url
		String customURL = "Depth - 1"+"\t"+URL;
		//setting the keywords in the string array
		String[] keywords = {"lunar","moon"};
		//Crawling all valid URLs taking the URL, the current depth and array of strings as arguments
		fcr.fetchAllURLs(customURL,1,keywords);
		//Writing all urls to the file
		fcr.writetoFile("Focused_URLS.txt");
	}

}
