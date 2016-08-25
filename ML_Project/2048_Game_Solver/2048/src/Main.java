import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {

	public static void main(String[] args) throws AWTException, InterruptedException {
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		WebDriver driver = openLink("http://www.caseyrule.com/projects/2048-AI/");
		WebElement retryButton = getRetryButton(driver);
		Robot robot = new Robot();
		int[][] prev_state = new int[4][4];
		int[] moves = new int[] { KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_LEFT };
		Random rand = new Random();
		while (!retryButton.isDisplayed()) {
			int[][] current_state = getCurrentState(driver);
			List<State> nextStates = State.findNextBoardStates(current_state);
			int move = 0;
			if (Arrays.deepEquals(current_state, prev_state))
				move = moves[rand.nextInt(4)];
			else
				move = getBestMove(nextStates);
			robot.keyPress(move);
			robot.keyRelease(move);
			prev_state = current_state;
			Thread.sleep(100);
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

	public static int getBestMove(List<State> nextStates) {
		int move = KeyEvent.VK_DOWN;
		float prev_prob = 0;
		for (State state : nextStates) {
			float current_prob = winProb(State.formBoardVector(state.board));
			if (current_prob > prev_prob) {
				move = state.move;
				prev_prob = current_prob;
			}
		}
		return move;
	}

	static List<String> cmd = new ArrayList<>();

	static {
		cmd.add("/usr/bin/octave");
		cmd.add("--path");
		cmd.add("./scripts/");
		cmd.add("--eval");
	}

	public static float winProb(int[] state) {
		Scanner in = null;
		Process oct;
		float p = 0;
		try {
			String code = "warning('off', 'all'); test(" + Arrays.toString(state) + ")";
			cmd.add(code);
			oct = new ProcessBuilder(cmd).start();
			in = new Scanner(oct.getInputStream());
			if (in.hasNext()) {
				in.next();
				in.next();
				p = in.nextFloat();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			cmd.remove(cmd.size() - 1);
			if (in != null)
				in.close();
		}
		return p;
	}
}