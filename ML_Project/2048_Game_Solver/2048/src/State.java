import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class State {
	public int[][] board;
	int move;

	public State(int[][] board, int move) {
		this.board = board;
		this.move = move;
	}

	@Override
	public String toString() {
		return "State [board=" + Arrays.deepToString(board) + ", move=" + move + "]";
	}

	static int[] formBoardVector(int[][] board) {
		int[] vector = new int[16];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				vector[i * 4 + j] = board[i][j];
			}
		}
		return vector;
	}

	static int[][] duplicateBoardState(int board[][]) {
		int dBoard[][] = new int[4][4];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				dBoard[i][j] = board[i][j];
			}
		}
		return dBoard;
	}

	static void slideRight(int board[][]) {
		for (int i = 0; i < 4; i++) {
			int k = 3;
			for (int j = 3; j >= 0; j--) {
				if (board[i][j] != 0) {
					board[i][k] = board[i][j];
					k--;
				}
			}
			while (k >= 0) {
				board[i][k] = 0;
				k--;
			}
		}
	}

	static void slideLeft(int board[][]) {
		for (int i = 0; i < 4; i++) {
			int k = 0;
			for (int j = 0; j < 4; j++) {
				if (board[i][j] != 0) {
					board[i][k] = board[i][j];
					k++;
				}
			}
			while (k < 4) {
				board[i][k] = 0;
				k++;
			}
		}
	}

	static void slideLaterally(int board[][], boolean isRight) {
		for (int i = 0; i < 4; i++) {
			int current = -1;
			int lastJ = -1;
			for (int j = 3; j >= 0; j--) {
				if (board[i][j] != 0 && current == board[i][j]) {
					board[i][j] = board[i][j] + 1;
					board[i][lastJ] = 0;
					current = -1;
				} else if (board[i][j] != 0) {
					current = board[i][j];
					lastJ = j;
				}
			}
		}

		if (isRight) {
			slideRight(board);
		} else {
			slideLeft(board);
		}

	}

	static void slideDown(int board[][]) {
		for (int i = 0; i < 4; i++) {
			int k = 3;
			for (int j = 3; j >= 0; j--) {
				if (board[j][i] != 0) {
					board[k][i] = board[j][i];
					k--;
				}
			}
			while (k >= 0) {
				board[k][i] = 0;
				k--;
			}
		}
	}

	static void slideUp(int board[][]) {
		for (int i = 0; i < 4; i++) {
			int k = 0;
			for (int j = 0; j < 4; j++) {
				if (board[j][i] != 0) {
					board[k][i] = board[j][i];
					k++;
				}
			}
			while (k < 4) {
				board[k][i] = 0;
				k++;
			}
		}
	}

	static void slideVertically(int board[][], boolean isUp) {
		for (int i = 0; i < 4; i++) {
			int current = -1;
			int lastI = -1;
			for (int j = 3; j >= 0; j--) {
				if (board[j][i] != 0 && current == board[j][i]) {
					board[j][i] = board[j][i] + 1;
					board[lastI][i] = 0;
					current = -1;
				} else if (board[j][i] != 0) {
					current = board[j][i];
					lastI = j;
				}
			}
		}

		if (isUp) {
			slideUp(board);
		} else {
			slideDown(board);
		}

	}

	static List<State> findNextBoardStates(int current[][]) {
		List<State> nextStates = new LinkedList<>();

		int[][] next = duplicateBoardState(current);
		slideLaterally(next, true);
		nextStates.add(new State(next, KeyEvent.VK_RIGHT));

		next = duplicateBoardState(current);
		slideVertically(next, false);
		nextStates.add(new State(next, KeyEvent.VK_DOWN));

		next = duplicateBoardState(current);
		slideLaterally(next, false);
		nextStates.add(new State(next, KeyEvent.VK_LEFT));

		next = duplicateBoardState(current);
		slideVertically(next, true);
		nextStates.add(new State(next, KeyEvent.VK_UP));

		return nextStates;
	}
}