import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Page {

	public ArrayList<String> inLinks = new ArrayList<String>();
	int outLinks;
	double pageRank = 0;
	public ArrayList<String> getInLinks() {
		return inLinks;
	}
	public void setInLinks(ArrayList<String> inLinks) {
		this.inLinks = inLinks;
	}
	public int getOutLinks() {
		return outLinks;
	}
	public void setOutLinks(int outLinks) {
		this.outLinks = outLinks;
	}
	public double getPageRank() {
		return pageRank;
	}
	public void setPageRank(double newpageRank) {
		this.pageRank = newpageRank;
	}
}
