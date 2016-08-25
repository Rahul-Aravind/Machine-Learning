import java.io.File;

public class Input {
	
	private String trainingDataDir;
	private String testDataDir;
	private String stopWordFile;
	
	public Input() {
		trainingDataDir = new File("").getAbsolutePath() + "/train/";
		testDataDir = new File("").getAbsolutePath() + "/test/";
		stopWordFile = new File("").getAbsolutePath() + "/stopwords.txt";
	}

	public String getTrainingDataDir() {
		return trainingDataDir;
	}

	public void setTrainingDataDir(String trainingDataDir) {
		this.trainingDataDir = trainingDataDir;
	}

	public String getTestDataDir() {
		return testDataDir;
	}

	public void setTestDataDir(String testDataDir) {
		this.testDataDir = testDataDir;
	}

	public String getStopWordFile() {
		return stopWordFile;
	}

	public void setStopWordFile(String stopWordFile) {
		this.stopWordFile = stopWordFile;
	}

}
