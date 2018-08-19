import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.util.concurrent.TimeUnit;

public class DFS_Crawl {
	//set Max depth to crawl
	public static final int MAX_DEPTH = 6;
	//set Max number of links to crawl
	public static final int MAX_LINKS = 1000;
	private static final double TELEPORTATION = 0.85;
	/*private static final double TELEPORTATION = 0.55;*/
	public static double ENTROPY = 0;
	//to store URLs that are unique and in sequence
	static LinkedHashSet<String> linkset = new LinkedHashSet<String>();
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
						//created an inlink map for valid urls
						Set<String> inLinkSet = new HashSet<String>();
						String valArray[] = URL.split("/"); 
						String val = valArray[valArray.length -1];
						inLinkSet.add(val);
						if(inLinkMap.containsKey(page.attr("abs:href")))
						{
							Set<String> newSet = new HashSet<String>();
							newSet =  inLinkMap.get(page.attr("abs:href"));
							newSet.add(val);
							inLinkMap.put(page.attr("abs:href"), newSet);
						}
						else {

							inLinkMap.put(page.attr("abs:href"), inLinkSet);
						}
						
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
	
	//Calculate outlinks of a page
	public static void calculateOutlinks(String fileName,Map<String, Set<String>> inLinkMaps, int x)
	{
		if(x != 1)
		{
			for(Map.Entry<String, Set<String>> entry : inLinkMaps.entrySet())
			{
				if(linkset.contains(entry.getKey()))
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

		for(String page :linkset)
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
				write.write("Page - "+pageInfo.getKey()+" InlinkCount - "+pageInfo.getValue().getInLinks().size()+" OutLinkCount - "+pageInfo.getValue().getOutLinks());
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
	
	//to write G2 file
	public void writetoFileMap(String fileName, Map<String, Set<String>> inLinkMap2) {
		// TODO Auto-generated method stub
		try {
			FileWriter write = new FileWriter(fileName);
			for(Entry<String, Set<String>> entry : inLinkMap2.entrySet())
			{
				if(linkset.contains(entry.getKey()))
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


	public static void main(String args[])
	{
		DFS_Crawl dfs = new DFS_Crawl();
		//seed url(Frontier)
		String seedURL = "https://en.wikipedia.org/wiki/Solar_eclipse";
		//Crawling all valid URLs
		dfs.getpageLinks(seedURL,1);
		//writing all URLs to a text file
		//dfs.writetoFile("DFS_URLs.txt");
		//generate G2 file
		dfs.writetoFileMap("G2.txt",dfs.inLinkMap);
		calculateOutlinks("G2_Outlinks.txt",dfs.inLinkMap,2);
		setPage("G2_PageInfo.txt",dfs.inLinkMap,outlinkMap,Pages);
		findSinkNodes(Pages);
		//Calculate Rank
		pageRank(Pages, outlinkMap, sinkNodes,"DFSPerplexity.txt");
		//Sorting according to pageRank and inlink count
		LinkedHashMap<String, Page> sortedByRank = (LinkedHashMap<String, Page>)sortBasedOnRankValues(Pages);
		LinkedHashMap<String, Page> sortedByInlinkCount = (LinkedHashMap<String, Page>)sortBasedOnInlinkCount(Pages);
		dfs.writetoSortFileMap(sortedByRank,"DFSSortedByRank.txt",1);
		dfs.writetoSortFileMap(sortedByInlinkCount,"DFSSortedByCount.txt",2);
	}
}
