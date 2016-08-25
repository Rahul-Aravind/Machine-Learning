import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtil {
	
	public static String readTextFromFile(String fileName) throws IOException, FileNotFoundException {
		StringBuffer buff = new StringBuffer();
		if (fileName.endsWith(".txt")) {
			buff = new StringBuffer();

			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				buff.append(line + " ");
			}
			br.close();

			return buff.toString();
		}
		return buff.toString();

	}

	public static ArrayList<String> getFilesList(String filePath, String targetclass) {

		ArrayList<String> files = new ArrayList<String>();
		File file = new File(filePath + targetclass);
		String[] fileNames = file.list();

		for (String s : fileNames) {
			if (s.endsWith(".txt")) {
				files.add(s);
			}
		}
		return files;
	}
	
	public static String getFileContents(File fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		StringBuilder sb = new StringBuilder();

		String str = "";
		while ((str = br.readLine()) != null) {
			sb.append(str);
		}

		return sb.toString();
	}
}
