import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.util.concurrent.TimeUnit;

public class BFS_Crawl {
	//set Max depth to crawl
	public static final int MAX_DEPTH = 6;
	//set Max number of links to crawl
	public static final int MAX_LINKS = 1000;
	//file location where html files are stored
	public static File path = new File(System.getProperty("user.dir"));
	//for storing unvisited URLs
	static Queue<String> urlListqueue = new LinkedList<String>();
	//to store URLs that are unique and in sequence
	static LinkedHashSet<String> trackerList = new LinkedHashSet<>();
	//depth of the crawling
	int crawlDepth = 0;

	//function to crawl all ULS from the given seed URL in BFS manner
	private void fetchAllURLs(String URL, int depth) {
		// TODO Auto-generated method stub

		urlListqueue.add(URL);
		INIT: while(urlListqueue.size()!=0)
		{
			/*To get proper URL from the first string in the queue*/
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
					String filter = "a[href^=\"/wiki/\"]" + "a:not([href*=#])"+ "a:not([href*=:])" + "a[href]:not([href~=(?i)\\.pn?g$])" 
							+ "a[href]:not([href~=(?i)\\.sv?g$])" + "a[href]:not([href~=(?i)\\.jpe?g$])"+
							"a[href]:not([href~=(?i)\\.jp?g$])" + "a[href]:not([href~=(?i)\\.gif$])" + "a[href]:not([href~=(?i)\\.tif$])";
					//getting all the urls from the url that is being processed
					Elements customProperLinks = innerbody.select(filter);
					//calculating Max depth reached while accessing the urls
					depth = currentDepth +1 ;
					crawlDepth = depth;

					for (Element link : customProperLinks)
					{
						//to verify that the page is in English
						if(link.attr("abs:href").contains("en.wikipedia.org"))
						{
							//if max depth reached or max number of links reached, return to original outer loop
							if(trackerList.size() == MAX_LINKS || depth == MAX_DEPTH+1)
							{
								continue INIT;
							}
							else {
								//finally adding valid URLs to the queue and the set 
								urlListqueue.add("Depth - "+depth+"\t"+ link.attr("abs:href"));
								//adding valid URLs to the set 
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
			int counter = 1;
			FileWriter write = new FileWriter(fileName);
			for(String str : trackerList)
			{
				//downloading HTML pages in text format from the valid URLs
				downloadPage(str,new File(path + "\\code" + counter +".html"),counter);
				//write all URLs to the file
				write.write(str);
				write.write(System.getProperty("line.separator"));
				counter++;
			}
			write.close();
			System.out.println("Finished Writing");
			System.out.println("Crawled Depth - "+crawlDepth);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Downloading all HTML pages of valid URLs
	public void downloadPage(String str,File path, int fileCounter) {
		// TODO Auto-generated method stub
		try {
			//Delay of 1 sec for request
			TimeUnit.SECONDS.sleep(1);
			URL url = new URL(str);
			FileWriter fw = new FileWriter(path,true);
			URLConnection con =  url.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			HttpURLConnection conn = null;
			if(con instanceof HttpURLConnection)
			{
				conn = (HttpURLConnection)con;
			}
			conn.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String newstr;
			while((newstr = br.readLine()) != null)
			{
				fw.write(newstr);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		BFS_Crawl bfs = new BFS_Crawl();
		//seed url
		String URL = "https://en.wikipedia.org/wiki/Solar_eclipse";
		//Appending depth to the url
		String customURL = "Depth - 1"+"\t"+URL;
		//Crawling all valid URLs taking the URL and the current depth as arguments
		bfs.fetchAllURLs(customURL,1);
		//Writing and downloading all urls to the file
		bfs.writetoFile("BFS_URLS.txt");
	}
}
