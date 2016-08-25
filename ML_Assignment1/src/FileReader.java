import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FileReader {
	
	public ArrayList<ArrayList<String>> getDataSet(String fileName) throws IOException {
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>> ();
		File file = new File(fileName);
		Scanner in;
		
		in = new Scanner(file);
		while(in.hasNext()) {
			String[] row = in.next().split(",");
			data.add(new ArrayList<String>(Arrays.asList(row)));
		}
		in.close();
		return data;
	}
	
	public static void main(String args[]) throws IOException {
		FileReader fr = new FileReader();
		Scanner in = new Scanner(System.in);
		
		System.out.println("Enter the file name");
		String fileName = in.next();
		
		ArrayList<ArrayList<String>> data = fr.getDataSet(fileName);
		System.out.println(data);
		in.close();
	}
}
