import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {
	
	public static void main(String[] args) {
		FileReader fr = new FileReader();
		DecisionTreeBuilder decisionTreeBuilder = new DecisionTreeBuilder();
		DecisionTreePruner decisionTreePruner = new DecisionTreePruner();
		
		if(args.length < 6) {
			System.out.println("Usage : <L> <K> <training-set-filename> <validation-set-filename> <test-set-filename> <to-print>");
			System.out.println("Note: 1) Please give file names along with full directory path");
			System.out.println("      2) to-print will take values as either yes or no");
			System.exit(0);
		}
		
		
		int L = Integer.parseInt(args[0]);
		int K = Integer.parseInt(args[1]);
		
		String trainingFile = args[2];
		String validationFile = args[3];
		String testFile = args[4];
		String toPrint = args[5];
		
		ArrayList<ArrayList<String>> trainingSet = null;
		ArrayList<ArrayList<String>> validationSet = null;
		ArrayList<ArrayList<String>> testSet = null;
		
		//read the data from the file and store it in array list
		
		try {
			trainingSet = fr.getDataSet(trainingFile);
			validationSet = fr.getDataSet(validationFile);
			testSet = fr.getDataSet(testFile);
			
			//isVarianceImpurity = false => Information gain heuristic else otherwise
			boolean isVarianceImpurity = false;
			
			//get the attribute set
			ArrayList<String> attributeList = trainingSet.get(0);
			
			//build a decision tree out of the training data
			TreeNode treeFromIG = decisionTreeBuilder.buildTree(attributeList, trainingSet, isVarianceImpurity);
			
			if(toPrint.equalsIgnoreCase("yes")) {
				System.out.println();
				System.out.println("*********************************");
				System.out.println("Decision tree from Information gain heurisitc");
				System.out.println("*********************************");
			
				treeFromIG.printTree();
			}
			
			isVarianceImpurity = true;
			TreeNode treeFromVI = decisionTreeBuilder.buildTree(attributeList, trainingSet, isVarianceImpurity);
			
			if(toPrint.equalsIgnoreCase("yes")) {
				System.out.println();
				System.out.println("*********************************");
				System.out.println("Decision tree from variance Impurity heurisitc");
				System.out.println("*********************************");
			
				treeFromVI.printTree();
			}
			
			
			double accuracyIG = decisionTreeBuilder.computeAccuracy(treeFromIG, testSet);
			System.out.println();
			System.out.println("******************************************");
			System.out.println("Before Pruning : Accuracy from Information Gain heuristic " + accuracyIG);
			System.out.println("Non-Leaf Node count " + decisionTreeBuilder.getNonLeafNodeCount(treeFromIG) );
			System.out.println("******************************************");
			
			double accuracyVI = decisionTreeBuilder.computeAccuracy(treeFromVI, testSet);
			System.out.println();
			System.out.println("******************************************");
			System.out.println("Before Pruning : Accuracy from Variance Impurity heuristic " + accuracyVI);
			System.out.println("Non-Leaf Node count " + decisionTreeBuilder.getNonLeafNodeCount(treeFromVI) );
			System.out.println("******************************************");
			
			TreeNode prunedTreeFromIG = decisionTreePruner.pruneTree(treeFromIG, L, K, validationSet);
			
			if(toPrint.equalsIgnoreCase("yes")) {
				System.out.println();
				System.out.println("*********************************");
				System.out.println("After Pruning : Decision tree from Information gain heurisitc");
				System.out.println("*********************************");
			
				prunedTreeFromIG.printTree();
			}
			
			decisionTreePruner = new DecisionTreePruner();
			TreeNode prunedTreeFromVI = decisionTreePruner.pruneTree(treeFromVI, L, K, validationSet);
			
			if(toPrint.equalsIgnoreCase("yes")) {
				System.out.println();
				System.out.println("*********************************");
				System.out.println("After Pruning : Decision tree from Variance Impurity heurisitc");
				System.out.println("*********************************");
			
				prunedTreeFromVI.printTree();
			}
			
			double accuracyPruneIG = decisionTreeBuilder.computeAccuracy(prunedTreeFromIG, testSet);
			System.out.println();
			System.out.println("******************************************");
			System.out.println("After Pruning : Accuracy from Information Gain heuristic " + accuracyPruneIG);
			System.out.println("Non-Leaf Node count " + decisionTreeBuilder.getNonLeafNodeCount(prunedTreeFromIG) );
			System.out.println("******************************************");
			
			double accuracyPruneVI = decisionTreeBuilder.computeAccuracy(prunedTreeFromVI, testSet);
			System.out.println();
			System.out.println("******************************************");
			System.out.println("After Pruning : Accuracy from Variance Impurity heuristic " + accuracyPruneVI);
			System.out.println("Non-Leaf Node count " + decisionTreeBuilder.getNonLeafNodeCount(prunedTreeFromVI));
			System.out.println("******************************************");
			
			
		
		} catch (IOException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
	}

}
