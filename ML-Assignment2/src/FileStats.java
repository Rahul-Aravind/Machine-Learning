
import java.util.HashMap;

public class FileStats {

	private String fileName;
	private HashMap<String,Integer> tokenMap;
	private int className;
		
	public FileStats(String file,HashMap<String,Integer> map,int classValue){
		this.setFileName(file);
		this.setTokenCountMap(map);
		this.setClassValue(classValue);
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setTokenCountMap(HashMap<String,Integer> tokenCountMap) {
		this.tokenMap = tokenCountMap;
	}

	public HashMap<String,Integer> getTokenCountMap() {
		return tokenMap;
	}

	public void setClassValue(int classValue) {
		this.className = classValue;
	}

	public int getClassValue() {
		return className;
	}
}
