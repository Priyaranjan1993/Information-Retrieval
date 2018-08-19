import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.util.concurrent.TimeUnit;

public class DFS_Crawl {
	//set Max depth to crawl
	public static final int MAX_DEPTH = 6;
	//set Max number of links to crawl
	public static final int MAX_LINKS = 1000;
	//to store URLs that are unique and in sequence
	static LinkedHashSet<String> linkset = new LinkedHashSet<String>();
	//depth of the crawling
	int crawlDepth = 0;
		
	//function to crawl all ULS from the given seed URL in DFS manner
	public void getpageLinks(String URL, int depth) {
		// TODO Auto-generated method stub
		if((!linkset.contains(URL) && (depth != MAX_DEPTH + 1) && (linkset.size() < MAX_LINKS)))
		{

			try {
				/*Add the processed url to the final set*/
				linkset.add(URL);
				//delay of 1 second
				TimeUnit.SECONDS.sleep(1);
				//to connect to the page
				Document doc = Jsoup.connect(URL).get();
				//getting the content from the body
				Element body = doc.getElementById("bodyContent");
				Element innerbody = body.getElementById("mw-content-text");
				//applying proper filters to get valid URLs as per the question
				String filter = "a[href^=\"/wiki/\"]" + "a:not([href*=#])"+ "a:not([href*=:])" + "a[href]:not([href~=(?i)\\.pn?g$])" 
						+ "a[href]:not([href~=(?i)\\.sv?g$])" + "a[href]:not([href~=(?i)\\.jpe?g$])"+
						"a[href]:not([href~=(?i)\\.jp?g$])" + "a[href]:not([href~=(?i)\\.gif$])" + "a[href]:not([href~=(?i)\\.tif$])";
				//getting all the urls from the url that is being processed
				Elements customProperLinks = innerbody.select(filter);
				//calculating Max depth reached while accessing the urls
				depth++;
				crawlDepth = depth;
				for(Element page : customProperLinks)
				{
					//to verify that the page is in English
					if(page.attr("abs:href").contains("en.wikipedia.org"))
					{
						//recursing over the URL we get from the url being processed
						getpageLinks(page.attr("abs:href"), depth);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	public void writetoFile(String fileName) {
		// TODO Auto-generated method stub
		try {
			FileWriter write = new FileWriter(fileName);
			for(String str : linkset)
			{
				//write all URLs to the file
				write.write(str);
				write.write(System.getProperty("line.separator"));
			}
			write.close();
			System.out.println("Finished Writing");
			System.out.println("Crawled Depth - "+(crawlDepth-1));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String args[])
	{
		DFS_Crawl dfs = new DFS_Crawl();
		//seed url(Frontier)
		String seedURL = "https://en.wikipedia.org/wiki/Solar_eclipse";
		//Crawling all valid URLs
		dfs.getpageLinks(seedURL,1);
		//writing all URLs to a text file
		dfs.writetoFile("DFS_URLs.txt");
	}
}
