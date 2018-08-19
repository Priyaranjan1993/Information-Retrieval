import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

public class Task1 {
	static String fileTitle ="";

	public static String getFileExtension(File f)
	{
		String filename = f.getName();
		if(filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != 0)
		{
			return filename.substring(filename.lastIndexOf(".")+1);
		}
		else return "";
	}

	//parsing the file with given conditions
	public static String parse(File file) {
		// TODO Auto-generated method stub
		Document document;
		try {
			document = Jsoup.parse(file, "UTF-8", "");
			Element title = document.getElementById("firstHeading");
			String fileName = file.getName();
			String proprName = fileName.substring(0, fileName.lastIndexOf("."));
			fileTitle = proprName;
			Element body = document.getElementById("bodyContent");
			Element innerbody = body.getElementById("mw-content-text");
			innerbody.select("table").remove();
			innerbody.select("div #toc").remove();
			innerbody.select("math").remove();
			innerbody.select("dl dd span .mwe-math-element").remove();
			String output = title.text()+" "+innerbody.text();
			output = caseFolding(output);
			output = punctuationHandling(output);
			return output;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	//handling punctuation
	public static String punctuationHandling(String input) {
		// TODO Auto-generated method stub
		for(int k =0; k< input.length();k++)
		{
			if(k>0)
			{
				if(Character.isDigit(input.charAt(k)))
				{
					Character first =input.charAt(k-1);
					Character ch = Character.MIN_VALUE;
					if(first.equals('(')||first.equals(')')||first.equals(':')||first.equals('[')||
							first.equals(']')||first.equals(',')||first.equals(';')||first.equals('?')||first.equals('\'')||first.equals('\'')||first.equals('$')||first.equals('#'))
					{
						input = input.replace(input.charAt(k-1), ch);
					}
					if((k+1) < input.length())
					{
						Character second =input.charAt(k+1);
						if(second.equals('(')||second.equals(')')||second.equals(':')||second.equals('[')||
								second.equals(']')||second.equals(',')||second.equals(';')||second.equals('?')||second.equals('\'')||second.equals('\'')||second.equals('$')||second.equals('#'))
						{
							input = input.replace(input.charAt(k+1), ch);
						}
					}

				}
			}

		}

		return input.replaceAll("(?<!\\d)[.,;:?'<>/#%$^&(){}_\\]!\\[\\\"`](?!\\d)", "");
	}

	//handling caseFolding
	public static String caseFolding(String input) {
		// TODO Auto-generated method stub
		return input.toLowerCase();

	}

	//writing to file
	public void writetoFile(String fileName,String el) {
		// TODO Auto-generated method stub
		BufferedWriter write = null;
		try {

			write = new BufferedWriter(new FileWriter("D://Corpus/"+fileTitle+".txt"));
			write.write(el);
			write.close();
			/*out.close();*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main (String args[])
	{
		Task1 tk1 = new Task1();
		File input  = new File("C:/Users/priya/Desktop/Example");
		File[] list = input.listFiles();
		File file = new File("D://Corpus");
		if(list!=null)
		{
			file.mkdirs();
		}
		for(int k=0; k<list.length; k++)
		{
			if(list[k].isFile() && getFileExtension(list[k]).equalsIgnoreCase("html"))
			{
				String el = "";
				el = parse(list[k]);
				tk1.writetoFile(list[k].getName(),el);
			}
		}
	}
}
