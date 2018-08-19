import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class Task2 {

	static HashMap<String, Integer> usortedMap;
	static HashMap<String, ArrayList<String>> dusortedMap;
	public static void main (String args[])
	{
		final File folder = new File("D://Corpus");
		
		/*Generating files for n-grams, its term and document frequency tables */
		listFiles(folder,"unigram","unigram.txt");
		generateTFTable("unigramTermFrequency.txt",usortedMap,"unigram");
		generateDFTable("unigramDocFrequency.txt",dusortedMap,"unigram");
		
		listFiles(folder,"bigram","bigram.txt");
		generateTFTable("bigramTermFrequency.txt",usortedMap,"bigram");
		generateDFTable("bigramDocFrequency.txt",dusortedMap,"bigram");
		
		listFiles(folder,"trigram","trigram.txt");
		generateTFTable("trigramTermFrequency.txt",usortedMap,"trigram");
		generateDFTable("trigramDocFrequency.txt",dusortedMap,"trigram");
		
		listFiles(folder,"unigram-term","unigramTermPositions.txt");

	}

	//Creating files for n-grams 
	public static void listFiles(File folder, String ngram,String outputFile) {
		String outputF = outputFile;
		String ngrams = ngram;
		LinkedHashMap<String, ArrayList<postingInfo>> posting = new LinkedHashMap<String, ArrayList<postingInfo>>();
		for(final File fileEntry : folder.listFiles())
		{
			if(fileEntry.isDirectory())
			{
				listFiles(fileEntry,ngrams,outputF);
			}
			else
			{
				try {
					BufferedReader br  = new BufferedReader(new FileReader(fileEntry.getAbsoluteFile()));
					String st;
					try {
						while((st = br.readLine()) != null)
						{
							String a[] = st.split(" ");
							String name = fileEntry.getName();
							name = name.substring(0, name.lastIndexOf('.'));
							String token = "";

							for(int i=0; i< a.length; i++)
							{
								int count = 1;
								int position = 0;
								if(ngram.equals("unigram") || ngram.equals("unigram-term"))
								{
									token = a[i];
								}
								else if(ngram.equals("bigram"))
								{
									if((i+1) < a.length)
									{
										ArrayList<String> bi= new ArrayList<String>();
										token="";
										String str1 = a[i];
										String str2 = a[i+1];
										token = (str1+" "+str2);
									}
									else {
										break;
									}

								}
								else if(ngram.equals("trigram"))
								{
									if((i+1) < a.length && (i+2) < a.length)
									{
										ArrayList<String> bi= new ArrayList<String>();
										token="";
										String str1 = a[i];
										String str2 = a[i+1];
										String str3 = a[i+2];
										token = (str1+" "+str2+" "+str3);
									}
									else {
										break;
									}
								}


								if(!token.matches("\\d+"))
								{
									if(posting.containsKey(token))
									{
										if(name == posting.get(token).get(posting.get(token).size()-1).getDocId())
										{
											posting.get(token).get(posting.get(token).size()-1).increaseFrequency();

											if(ngram.equals("unigram-term"))
											{
												position = i+1;
												posting.get(token).get(posting.get(token).size()-1).addtoPosition(position);
											}
										}
										else {
											if(ngram.equals("unigram-term"))
											{
												ArrayList<Integer> posList = new ArrayList<Integer>();
												position = i+1;
												posList.add(position);
												postingInfo pi = new postingInfo(name,count,posList);
												posting.get(token).add(pi);
											}
											else
											{
												postingInfo pi = new postingInfo(name,count);
												posting.get(token).add(pi);
											}

										}
									}
									else
									{
										ArrayList<postingInfo> list = new ArrayList<postingInfo>();
										ArrayList<Integer> posList = new ArrayList<Integer>();
										if(ngram.equals("unigram-term"))
										{
											position = i+1;
											posList.add(position);
											postingInfo piNew = new postingInfo(name,count,posList);
											list.add(piNew);
											posting.put(token, list);
										}
										else {
											postingInfo piNew = new postingInfo(name,count);
											list.add(piNew);
											posting.put(token, list);
										}

									}
								}
								else
								{
									if(ngram.equals("unigram-term"))
									{
										position = i+1;
										ArrayList<Integer> posList = new ArrayList<Integer>();
										int digitCount = countOccurences(a[i],fileEntry.getAbsoluteFile());
										ArrayList<postingInfo> lists = new ArrayList<postingInfo>();
										postingInfo pe = new postingInfo(name,digitCount,posList);
										lists.add(pe);
										posting.put(token, lists);
									}
									else {
										int digitCount = countOccurences(a[i],fileEntry.getAbsoluteFile());
										ArrayList<postingInfo> lists = new ArrayList<postingInfo>();
										postingInfo pe = new postingInfo(name,digitCount);
										lists.add(pe);
										posting.put(token, lists);
									}

								}
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		BufferedWriter outPrint = writeOutput(outputF);
		outputResults(posting,outPrint,ngrams);
		closeOutput(outputF,outPrint);
	}

	//closing output connection
	public static void closeOutput(String outputF, BufferedWriter outPrint) {
		// TODO Auto-generated method stub
		if(outPrint != null)
		{
			try {
				outPrint.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void outputResults (LinkedHashMap<String, ArrayList<postingInfo>> posting, BufferedWriter out,String ngrams) {

		usortedMap  = new HashMap<String, Integer>();
		dusortedMap = new HashMap<String, ArrayList<String>>();
		for(Map.Entry<String, ArrayList<postingInfo>> entry : posting.entrySet())
		{
			String s = "";
			s = entry.getKey()+" ";
			String keyName = entry.getKey();
			int count = 0;
			String countS = "";
			String str = "";
			String docid= "" ;
			ArrayList<String> al = new ArrayList<String>();
			
			if(!s.equalsIgnoreCase(" "))
			{
				if(ngrams.equals("unigram-term"))
				{
					count = 0;
					for(postingInfo p : entry.getValue())
					{
						al.add(p.docId);
						s = s+" "+"["+p.docId+","+p.termFrequency+","+Arrays.toString(p.position.toArray())+"]";
						count = count + p.termFrequency;
					}
					s= s+"\n";
					//countS = count+"\n";
					//str = s+"("+count+")"+"\n";
				}
				else {
					count = 0;
					
					for(postingInfo p : entry.getValue())
					{
						//docid = docid+" "+"["+p.docId+"]";
						al.add(p.docId);
						
						s = s+" "+"["+p.docId+","+p.termFrequency+"]";
						count = count + p.termFrequency;
					}
					s= s+"\n";
					//countS = count+"\n";
					//str = s+count+"\n";
				}

				try {
					usortedMap.put(keyName, count);
					dusortedMap.put(keyName,al);
					out.write(s);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	public static void generateTFTableoutputResults (Map<String, Integer> t, BufferedWriter out,String ngrams) {
		
		for(Entry<String, Integer> entry : t.entrySet())
		{
			String s = entry.getKey() +" "+entry.getValue();
			if(!s.startsWith(" "))
			{
				s= s+"\n";
				try {
					out.write(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
public static void generateDFTableoutputResults (TreeMap<String, ArrayList<String>> t, BufferedWriter out,String ngrams) {
		
		for(Entry<String, ArrayList<String>> entry : t.entrySet())
		{
			int size = entry.getValue().size();
			String s = entry.getKey() +" "+entry.getValue()+" "+size;
			
			/*if(!(s.startsWith(" ") || s.startsWith("\\")|| s.startsWith("\"")|| s.startsWith("#")|| s.startsWith("\'")|| s.startsWith("(")|| s.startsWith("-")||s.startsWith("<")||s.startsWith("=")
					|| s.startsWith("*")|| s.startsWith("[") || s.startsWith(".")|| s.startsWith("+")|| s.startsWith("?")))
			{*/
				s= s+"\n";
				try {
					out.write(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			/*}*/
			
		}
	}

	//Generating term frequency tables for n-grams
	public static void generateTFTable(String fileName, HashMap<String, Integer> usortedMap, String ngrams) {
		// TODO Auto-generated method stub
		Map<String, Integer> t = sortByFrequency(usortedMap);
		
		BufferedWriter outPrint = writeOutput(fileName);
		generateTFTableoutputResults(t,outPrint,ngrams);
		closeOutput(fileName,outPrint);
	}
	//Generating document frequency tables for n-grams
	public static void generateDFTable(String fileName, HashMap<String, ArrayList<String>> dusortedMap, String ngrams) {
		// TODO Auto-generated method stub
		TreeMap<String, ArrayList<String>> t = sortChronologically(dusortedMap);
		
		BufferedWriter outPrint = writeOutput(fileName);
		generateDFTableoutputResults(t,outPrint,ngrams);
		closeOutput(fileName,outPrint);
		
	}

	//sorting by term frequency
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Integer> sortByFrequency(HashMap<String, Integer> usortedMap) {
		// TODO Auto-generated method stub
		Map<String, Integer> sorted = sortbyFreqVal(usortedMap);
		return sorted;
	}
	
	//sorting by document frequency
	public static Map<String, Integer> sortbyFreqVal(HashMap<String, Integer> usortedMap) {
		// TODO Auto-generated method stub
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(usortedMap.entrySet());
		Collections.sort(list,new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				// TODO Auto-generated method stub
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		Map<String, Integer> tsorted = new LinkedHashMap<String, Integer>();
		for(Map.Entry<String, Integer> entry : list)
		{
			tsorted.put(entry.getKey(), entry.getValue());
		}
		return tsorted;
	}

	public static TreeMap<String, ArrayList<String>> sortChronologically(HashMap<String, ArrayList<String>> dusortedMap) {
		TreeMap<String,ArrayList<String>> sorted = new TreeMap<String,ArrayList<String>>(dusortedMap);
		return sorted;
	}

	public static BufferedWriter writeOutput(String outputFile)
	{
		BufferedWriter out = null;
		File newfile = new File(outputFile);
		try {
			out = new BufferedWriter(new FileWriter(newfile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;

	}
	//counting number of occurences of the word in the file
	public static int countOccurences(String s, File absoluteFile) {
		// TODO Auto-generated method stub
		int count = 0;
		try {
			LineNumberReader br  = new LineNumberReader(new FileReader(absoluteFile));
			String ls;
			try {
				while((ls = br.readLine()) != null)
				{
					for(String elem : ls.split(" "))
					{
						if(elem.equalsIgnoreCase(s))
						{
							count++;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;

	}

}