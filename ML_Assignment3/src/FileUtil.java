import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtil {
	
	public static ArrayList<String> listFiles(String file, String category) {
		ArrayList<String> fileList = new ArrayList<>();
		File fileObj = new File(file + category);
		
		String[] files = fileObj.list();
		
		for(String f : files) {
			
			if(!f.endsWith(".txt")) continue;
			
			fileList.add(f);
		}
		
		return fileList;
	}
	
	public static String readTextFromFile(String fileName) throws IOException {
	    
		StringBuffer sbuf = new StringBuffer();
		
		//System.out.println("DEBUG " + fileName);

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		
		while ((line = br.readLine()) != null) {
			sbuf.append(line + " ");
		}
		br.close();

		return sbuf.toString();
	}
	
	/**
	 * test Driver program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Input input = new Input();
		
		ArrayList<String> fileList = listFiles(input.getTrainingDataDir(), "spam");
		System.out.println(fileList);
	}

}
