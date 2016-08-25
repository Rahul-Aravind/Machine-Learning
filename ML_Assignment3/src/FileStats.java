import java.util.HashMap;

public class FileStats {
	
	private String fileName;
	private HashMap<String, Integer> tokenMap;
	private double category;
	
	public FileStats(String fileName, HashMap<String, Integer> tokenMap, double category) {
		this.fileName = fileName;
		this.tokenMap = tokenMap;
		this.category = category;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public HashMap<String, Integer> getTokenMap() {
		return tokenMap;
	}

	public void setTokenMap(HashMap<String, Integer> tokenMap) {
		this.tokenMap = tokenMap;
	}

	public double getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
	
	

}
