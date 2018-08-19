import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.util.concurrent.TimeUnit;

public class BFS_Crawl {
	//set Max depth to crawl
	public static final int MAX_DEPTH = 6;
	//set Max number of links to crawl
	public static final int MAX_LINKS = 1000;
	private static final double TELEPORTATION = 0.85;
	/*private static final double TELEPORTATION = 0.55;*/
	public static double ENTROPY = 0;
	//file location where html files are stored
	public static File path = new File(System.getProperty("user.dir"));
	//for storing unvisited URLs
	static Queue<String> urlListqueue = new LinkedList<String>();
	//to store URLs that are unique and in sequence
	static LinkedHashSet<String> trackerList = new LinkedHashSet<>();
	//depth of the crawling
	int crawlDepth = 0;

	static int counter = 0;

	//Map for inlinks
	public Map<String, Set<String>> inLinkMap = new HashMap<String, Set<String>>();
	//Map for outlinks
	public static LinkedHashMap<String, Integer> outlinkMap = new LinkedHashMap<String, Integer>();
	//List for sink nodes
	public static ArrayList<String> sinkNodes = new ArrayList<String>();
	//List for perplexity
	public static ArrayList<Double> perplexity = new ArrayList<Double>();

	public static LinkedHashMap<String, Page> Pages = new LinkedHashMap<String, Page>();
	static LinkedHashMap<String, Page> custPagesMap = new LinkedHashMap<String, Page>();
	//function to crawl all ULS from the given seed URL in BFS manner
	private void fetchAllURLs(String URL, int depth) {
		// TODO Auto-generated method stub

		urlListqueue.add(URL);
		INIT: while(urlListqueue.size()!=0)
		{
			/*To get proper URL from the first string in the queue*/
			int x = urlListqueue.element().indexOf("\t");
			String urlstr = urlListqueue.element().substring(8,x);
			int currentDepth = Integer.parseInt(urlstr);
			String preInitialURL = urlListqueue.remove();
			String initialURL = preInitialURL.substring(x).trim();
			/*Add the url to the final set*/
			trackerList.add(initialURL);

			if(true)
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
					counter++;

					//created an inlink map for valid urls
					for (Element urlLink : customProperLinks)
					{
						if(urlLink.attr("abs:href").contains("en.wikipedia.org"))
						{
							Set<String> inLinkSet = new HashSet<String>();
							String valArray[] = initialURL.split("/"); 
							String val = valArray[valArray.length -1];
							inLinkSet.add(val);
							if(inLinkMap.containsKey(urlLink.attr("abs:href")))
							{
								Set<String> newSet = new HashSet<String>();
								newSet =  inLinkMap.get(urlLink.attr("abs:href"));
								newSet.add(val);
								inLinkMap.put(urlLink.attr("abs:href"), newSet);
							}
							else {

								inLinkMap.put(urlLink.attr("abs:href"), inLinkSet);
							}
						}
					}
					if((trackerList.size() < MAX_LINKS) && (depth != MAX_DEPTH+1))
					{
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


	//to write G1 file
	public void writetoFileMap(String fileName, Map<String, Set<String>> inLinkMap2) {
		// TODO Auto-generated method stub
		try {
			FileWriter write = new FileWriter(fileName);
			for(Entry<String, Set<String>> entry : inLinkMap2.entrySet())
			{
				if(trackerList.contains(entry.getKey()))
				{
					String keyArray[] = entry.getKey().split("/"); 
					String key = keyArray[keyArray.length -1];
					write.write(key +" "+String.join(" ",entry.getValue()));
					write.write(System.getProperty("line.separator"));
				}

			}
			write.close();
			System.out.println("Finished Writing");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//to write the sorted files according to pageRank and inlink count
	public void writetoSortFileMap(Map<String, Page> sortedPageMap,String fileName,int change) {
		// TODO Auto-generated method stub
		try {
			FileWriter write = new FileWriter(fileName);
			int j =1;
			for(Entry<String, Page> entry : sortedPageMap.entrySet())
			{
				if(j>50)
					break;
				else {
					String pageName = entry.getKey();
					String valArray[] = pageName.split("/"); 
					String val = valArray[valArray.length -1];
					Page page = entry.getValue();
					String output = val + " : ";
					if(change == 1)
					{
						output += page.getPageRank();
					}
					else {
						output += page.getInLinks().size();
					}
					write.write(output);
					write.write(System.getProperty("line.separator"));
					j++;

				}
			}
			write.close();
			System.out.println("Finished Writing");
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

	//PageRank algorithm Calculation
	public static void pageRank(LinkedHashMap<String, Page> page, LinkedHashMap<String, Integer> outlinks, ArrayList<String> sinkNodes,String fileName) {

		try{
			FileWriter write = new FileWriter(fileName);

			for(Map.Entry<String, Page> entry : page.entrySet())
			{
				Page pgInfo = entry.getValue();
				double pRank = (double)1/page.size();
				pgInfo.setPageRank(pRank);
			}

			double sinkPR;
			int count = 1;
			perplexity = new ArrayList<Double>();
			String text="";
			while (!converged(page,count,write))
				/*while(count<=100)*/
			{
				sinkPR = 0;
				for (String node : sinkNodes)
				{
					sinkPR += page.get(node).getPageRank();
				}

				for(Map.Entry<String, Page> pageEntry : page.entrySet())
				{
					double newpageRank;
					Page info = pageEntry.getValue();
					newpageRank = (1 - TELEPORTATION)/page.size();
					newpageRank += TELEPORTATION * sinkPR/page.size();

					for(String inlinkPage : info.getInLinks())
					{
						inlinkPage= "https://en.wikipedia.org/wiki/"+inlinkPage;
						newpageRank += TELEPORTATION * page.get(inlinkPage).getPageRank()/page.get(inlinkPage).getOutLinks();
					}
					info.setPageRank(newpageRank);
				}
				count++;
			}
			write.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	//converge calculation
	public static boolean converged(Map<String, Page> Pages,int counter, FileWriter write) throws IOException
	{
		ENTROPY = 0;

		for(Map.Entry<String, Page> entry : Pages.entrySet())
		{
			Page newPage;
			newPage = entry.getValue();
			ENTROPY += (newPage.getPageRank() * Math.log(newPage.getPageRank()));
		}

		ENTROPY = ENTROPY * -1;
		perplexity.add(Math.pow(2, ENTROPY));
		write.write("Perplexity value for iteration "+counter +": "+Math.pow(2, ENTROPY)+"\n");

		if(perplexity.size() == 4 && isEqual(perplexity))
		{
			return true;
		}
		else {
			if(perplexity.size() == 4)
				perplexity.remove(0);
			return false;

		}

	}

	public static boolean isEqual(ArrayList<Double> Perplexity)
	{
		for(int k = 1; k< Perplexity.size(); k++)
		{
			double difference = Perplexity.get(k-1) - Perplexity.get(k);
			if(difference > 1.0)
			{
				return false;
			}
		}
		return true;

	}
	
	//Calculate outlinks of a page
	public static void calculateOutlinks(String fileName,Map<String, Set<String>> inLinkMaps, int x)
	{
		if(x != 1)
		{
			for(Map.Entry<String, Set<String>> entry : inLinkMaps.entrySet())
			{
				if(trackerList.contains(entry.getKey()))
				{
					for(String string : entry.getValue())
					{
						if(outlinkMap.containsKey(string))
						{
							int value = outlinkMap.get(string);
							value++;
							outlinkMap.put(string, value);
						}
						else {
							outlinkMap.put(string, 1);
						}
					}
				}
			}
		}
		else {
			BufferedReader br = null;
			LinkedHashMap<String, Integer> custoutlinkMap = new LinkedHashMap<String, Integer>();
			LinkedHashMap<String, Set<String>> inLinkMap = new LinkedHashMap<String, Set<String>>();
			try {
				br = new BufferedReader(new FileReader("6nodes.txt"));
				String contentLine;
				while((contentLine = br.readLine())!= null)
				{
					String arrayContent[] = contentLine.split(" ");
					//ArrayList<String> custinLinks = new ArrayList<String>();
					Set<String> custinLinksSet = new HashSet<String>();
					for(int i =1; i<arrayContent.length;i++)
					{
						custinLinksSet.add(arrayContent[i]);
						//outLinkCount++;
					}
					inLinkMap.put(arrayContent[0], custinLinksSet);
					for(String str : custinLinksSet)
					{
						if(custoutlinkMap.containsKey(str))
						{
							int value = custoutlinkMap.get(str);
							value++;
							custoutlinkMap.put(str, value);
						}
						else {
							custoutlinkMap.put(str, 1);
						}
					}
				}

				for(Map.Entry<String, Integer> entry : custoutlinkMap.entrySet())
				{
					Page customPage = new Page();
					ArrayList<String> list = new ArrayList<String>(inLinkMap.get(entry.getKey()));
					customPage.setInLinks(list);
					customPage.setOutLinks(entry.getValue());
					custPagesMap.put(entry.getKey(), customPage);
				}
			/*	for(Entry<String, Page> entry : custPagesMap.entrySet())
				{
					System.out.println("Page - "+entry.getKey()+" Inlink - "+entry.getValue().getInLinks()+" OutLinkCount - "+entry.getValue().getOutLinks());
				}*/

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//Sets a page with number of inlinks and outlink counts
	public static void setPage(String fileName,Map<String, Set<String>> inLinkMap, Map<String, Integer> outLinkMap, Map<String, Page> Pages)
	{

		for(String page :trackerList)
		{
			Page p = new Page();
			Set<String> inLinkValues = new HashSet<String>();
			inLinkValues = inLinkMap.get(page);
			ArrayList<String> list= new ArrayList<String>(inLinkValues);
			p.setInLinks(list);

			int outlinkValue = 0;
			String pageArray[] = page.split("/"); 
			String newPage = pageArray[pageArray.length -1];

			if(outLinkMap.containsKey(newPage))
			{
				outlinkValue = outLinkMap.get(newPage);
			}
			else {
				outLinkMap.put(page, 0);
				outlinkValue = 0;
			}

			p.setOutLinks(outlinkValue);

			Pages.put(page, p);
		}

		try {
			FileWriter write = new FileWriter(fileName);
			for(Map.Entry<String, Page> pageInfo : Pages.entrySet())
			{
				//System.out.println(entry.getKey());
				write.write("Page - "+pageInfo.getKey()+" Inlink Count - "+pageInfo.getValue().getInLinks().size()+" OutLinkCount - "+pageInfo.getValue().getOutLinks());
				write.write(System.getProperty("line.separator"));
			}
			write.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	//Calculate Sink Nodes
	public static void findSinkNodes(Map<String, Page> Pages)
	{
		for(Map.Entry<String, Page> entry : Pages.entrySet())
		{
			Page p = entry.getValue();
			if(p.getOutLinks() == 0)
			{
				sinkNodes.add(entry.getKey());
			}
		}
		System.out.println("No Outlinks - "+sinkNodes.size());
	}

	//sort based on inlink count
	public static Map<String,Page> sortBasedOnInlinkCount(Map<String, Page> Pages)
	{
		ArrayList<Map.Entry<String, Page>> entry = new ArrayList<Map.Entry<String, Page>>(Pages.entrySet());
		Collections.sort(entry, new Comparator<Map.Entry<String, Page>>() {

			@Override
			public int compare(Map.Entry<String, Page> o1, Map.Entry<String, Page> o2) {
				// TODO Auto-generated method stub
				if(o1.getValue().getInLinks().size() < o2.getValue().getInLinks().size())
				{
					return 1;
				}
				else if(o1.getValue().getInLinks().size() > o2.getValue().getInLinks().size())
				{
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		LinkedHashMap<String, Page> sortedInlinkMap = new LinkedHashMap<String, Page>();
		for(Map.Entry<String, Page> filteredentry : entry)
		{
			sortedInlinkMap.put(filteredentry.getKey(), filteredentry.getValue());
		}
		return sortedInlinkMap;
	}

	//sort based on PageRank Algorithm
	public static Map<String,Page> sortBasedOnRankValues(Map<String, Page> Pages)
	{
		ArrayList<Map.Entry<String, Page>> entry = new ArrayList<Map.Entry<String, Page>>(Pages.entrySet());
		Collections.sort(entry, new Comparator<Map.Entry<String, Page>>() {

			@Override
			public int compare(Map.Entry<String, Page> o1, Map.Entry<String, Page> o2) {
				// TODO Auto-generated method stub
				if(o1.getValue().getPageRank() < o2.getValue().getPageRank())
				{
					return 1;
				}
				else if(o1.getValue().getPageRank() > o2.getValue().getPageRank())
				{
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		LinkedHashMap<String, Page> sortedInlinkMap = new LinkedHashMap<String, Page>();
		for(Map.Entry<String, Page> filteredentry : entry)
		{
			sortedInlinkMap.put(filteredentry.getKey(), filteredentry.getValue());
		}
		return sortedInlinkMap;
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
		//bfs.writetoFile("BFS_URLS_TEST.txt");
		//generate G2 file
		bfs.writetoFileMap("G1.txt",bfs.inLinkMap);
		calculateOutlinks("G1_Outlinks.txt",bfs.inLinkMap,2);
		setPage("G1_PageInfo.txt",bfs.inLinkMap,outlinkMap,Pages);
		findSinkNodes(Pages);
		//Calculate Rank
		pageRank(Pages, outlinkMap, sinkNodes,"BFSPerplexity.txt");
		//Sorting according to pageRank and inlink count
		LinkedHashMap<String, Page> sortedByRank = (LinkedHashMap<String, Page>)sortBasedOnRankValues(Pages);
		LinkedHashMap<String, Page> sortedByInlinkCount = (LinkedHashMap<String, Page>)sortBasedOnInlinkCount(Pages);
		bfs.writetoSortFileMap(sortedByRank,"BFSSortedByRank.txt",1);
		bfs.writetoSortFileMap(sortedByInlinkCount,"BFSSortedByCount.txt",2);

	}
}
