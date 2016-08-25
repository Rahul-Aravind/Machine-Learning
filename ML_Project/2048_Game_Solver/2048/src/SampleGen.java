import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SampleGen {

	/**
	 * @param args
	 *            the command line arguments
	 */
	static void writeGame(LinkedList<String> statesList, int i) throws IOException {
		String fileName = new String();
		if (statesList.getLast().contains("11")) {
			fileName = "Output-" + i + "-win.txt";
		} else {
			fileName = "Output-" + i + "-loss.txt";
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		// String s;
		for (String s : statesList) {
			bw.write(s);
			bw.newLine();
		}
		bw.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		System.setProperty("webdriver.chrome.driver", "libs\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.get("http://www.caseyrule.com/projects/2048-AI/");

		WebElement stepButton = driver.findElement(By.id("step-button"));

		WebElement retryButton = driver.findElement(By.xpath("//*[@id=\"content\"]/article/div/div/div[3]/div[1]/div")); // driver.findElement(By.id("retry-button"));

		for (int k = 1; k <= 100; k++) {
			LinkedList<String> statesList = new LinkedList<>();
			// int k=0;
			while (true) {
				Thread.sleep(100);
				WebElement rootWebElement = driver.findElement(By.className("tile-container"));
				List<WebElement> children = rootWebElement.findElements(By.xpath("./*"));
				int mat[][] = new int[4][4];
				StringBuilder boardState = new StringBuilder();
				// int ic = 0;
				for (WebElement we : children) {
					/*
					 * System.out.println(ic++);
					 * System.out.println(we.isDisplayed());
					 */ List<WebElement> grandChildren = we.findElements(By.xpath("./*"));

					String className = we.getAttribute("class");
					// System.out.println(className);
					String classsNameSplit[] = className.split(" ");
					String positionSplit[] = classsNameSplit[2].split("-");

					int j = Integer.parseInt(positionSplit[2]);
					int i = Integer.parseInt(positionSplit[3]);
					j--;
					i--;

					String res = grandChildren.get(0).getText();

					int num = 1;
					if (!res.equals(""))
						num = Integer.parseInt(res);

					mat[i][j] = (int) (Math.log(num) / Math.log(2));
				}
				// System.out.println();
				for (int i = 0; i < 4; i++) {
					for (int j = 0; j < 4; j++) {
						// System.out.print(mat[i][j]+" ");
						boardState.append(mat[i][j]);
						boardState.append(" ");
					}
					// System.out.println();
				}
				// System.out.println(boardState);
				// System.out.println("99999");
				statesList.add(boardState.toString());
				if (!retryButton.isDisplayed())
					stepButton.click();
				else
					break;
				// k++;
			}
			writeGame(statesList, k);
			retryButton.click();
		}
	}

}
