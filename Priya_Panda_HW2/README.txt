1. Download jsoup.jar file from https://jsoup.org/download.

2.Open CMD in windows, and type set classpath=jsoup-1.11.2.jar;.;%classpath%  where the user can set the classpath by giving the location of jsoup.jar in the system.

3.Then for running the java files to obtain G1 or BFS related files, type in the CMD
	javac BFS_Crawl.java
	java BFS_Crawl
    The program will run for sometime producing output files
	G1.txt (G1 graph)
	G1_Outlinks.txt (Outlinks of 1000 urls)
	G1_PageInfo.txt (Detailed Page Info for 1000 URLs)
	BFSPerplexity.txt (Perplexity count for each iteration)
	BFSSortedByRank.txt (Sorting based on PageRank Algorithm)
	BFSSortedByCount.txt (Sorting based on InLink Count)


4.Then for running the java files to obtain G2 or DFS related files, type in the CMD
	javac DFS_Crawl.java
	java DFS_Crawl
    The program will run for sometime producing output files
	G2.txt (G2 graph)
	G2_Outlinks.txt (Outlinks of 1000 urls)
	G2_PageInfo.txt (Detailed Page Info for 1000 URLs)
	DFSPerplexity.txt (Perplexity count for each iteration)
	DFSSortedByRank.txt (Sorting based on PageRank Algorithm)
	DFSSortedByCount.txt (Sorting based on InLink Count)

	