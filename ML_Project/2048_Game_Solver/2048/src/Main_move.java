import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main_move {

	public static void main(String[] args) throws AWTException, InterruptedException {
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		WebDriver driver = openLink("http://www.caseyrule.com/projects/2048-AI/");
		WebElement retryButton = getRetryButton(driver);
		Robot robot = new Robot();
		int[][] prev_state = new int[4][4];
		int[] moves = new int[] { KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT };
		Random rand = new Random();
		while (!retryButton.isDisplayed()) {
			int[][] current_state = getCurrentState(driver);
			int move = 0;
			if (Arrays.deepEquals(current_state, prev_state))
				move = moves[rand.nextInt(4)];
			else
				move = moves[winMove(State.formBoardVector(current_state)) - 1];
			robot.keyPress(move);
			robot.keyRelease(move);
			prev_state = current_state;
		}
	}

	public static WebDriver openLink(String link) {
		WebDriver driver = new ChromeDriver();
		driver.get(link);
		return driver;
	}

	public static WebElement getRetryButton(WebDriver driver) {
		return driver.findElement(By.xpath("//*[@id=\"content\"]/article/div/div/div[3]/div[1]/div"));
	}

	public static int[][] getCurrentState(WebDriver driver) {
		WebElement tileContainer = driver.findElement(By.className("tile-container"));
		List<WebElement> tiles = tileContainer.findElements(By.xpath("./*"));
		int board[][] = new int[4][4];
		for (WebElement tile : tiles) {
			String className = tile.getAttribute("class");
			String[] classsNameSplit = className.split(" ");
			String[] positionSplit = classsNameSplit[2].split("-");
			int j = Integer.parseInt(positionSplit[2]) - 1;
			int i = Integer.parseInt(positionSplit[3]) - 1;

			List<WebElement> tileDiv = tile.findElements(By.xpath("./*"));
			String res = tileDiv.get(0).getText();
			int num = 1;
			if (!res.equals(""))
				num = Integer.parseInt(res);
			board[i][j] = (int) (Math.log(num) / Math.log(2));
		}
		return board;
	}

	static List<String> cmd = new ArrayList<>();

	static {
		cmd.add("/usr/bin/octave");
		cmd.add("--path");
		cmd.add("./scripts/");
		cmd.add("--eval");
	}

	public static int winMove(int[] state) {
		Scanner in = null;
		Process oct;
		int move = 0;
		try {
			String code = "warning('off', 'all'); test_move(" + Arrays.toString(state) + ")";
			cmd.add(code);
			oct = new ProcessBuilder(cmd).start();
			in = new Scanner(oct.getInputStream());
			int[] moves = new int[4];
			int i = 0;
			while (in.hasNext()) {
				in.next();
				in.next();
				moves[i++] = in.nextInt();
			}
			move = maxVote(moves);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			cmd.remove(cmd.size() - 1);
			if (in != null)
				in.close();
		}
		return move;
	}

	public static int maxVote(int[] moves) {
		Map<Integer, Integer> votes = new HashMap<Integer, Integer>();
		for (int move : moves) {
			Integer freq = votes.get(move);
			votes.put(move, (freq == null) ? 1 : freq + 1);
		}

		int max = -1;
		int mostFrequent = -1;
		for (Map.Entry<Integer, Integer> e : votes.entrySet()) {
			if (e.getValue() > max) {
				mostFrequent = e.getKey();
				max = e.getValue();
			}
		}
		return mostFrequent;
	}
}