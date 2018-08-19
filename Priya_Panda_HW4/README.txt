1. Download jsoup.jar file from https://jsoup.org/download.

2.Open CMD in windows, and type set classpath=jsoup-1.11.2.jar;.;%classpath%  where the user can set the classpath by giving the location of jsoup.jar in the system.

3.Open the submitted homework folder and open all the files in Eclipse.

4.One might see several errors on the project which is because the build path has not been set for the project.

5.Right click  ---> Properties ----> Java Build Path -----> Add JRE [JavaSE - 1.8], commons-io.2.5.jar, and all the jsoup jars by downloading it from the internet.

6.Download lucene-4.7.2.zip from https://lucene.apache.org/. Once downloaded, copy lucene-core-VERSION.jar, lucene-queryparser-VERSION.jar and lucene-analyzers-common-VERSION.jar from
	the downloaded folder and set those in the build path of the project.
	
7.To run the first assignment, run HW4.java in the eclipse.

8.When the programme runs, it asks for the file path of the corpus and the file path where the index will be generated. The programme then asks the user to provide query to be searched post which ouput is displayed in the console for the input query.

9.To run the second assignment, run BM25.java in the eclpise.

10.The ouput would be a file named results.txt where for each query top 100 retrived document IDs and their BM25 scores are shown in the format "query_id	Q0	doc_id	rank	BM25_score	system_name".

11.The files provided to run both the tasks in the assignment are -
	1.queryList.txt
	2.unigram.txt
	3.Corpus(generated earlier)
	4.HW4.java(Task1)
	5.BM25.java(Task2)
	6.postingInfo.java(Task2)
	
The files that are putput to both the tasks are
	1.results.txt //(BM25 IR)(10 Tables)
	2.Lucene_IR_100.txt  //(Lucene IR)(10 Tables)