import java.util.ArrayList;
import java.util.Random;

public class DecisionTreePruner {
	
	private TreeNode bestPruneTree;
	private TreeNode tempPruneTree;
	private DecisionTreeBuilder decisionTreeBuilder;
	
	DecisionTreePruner() {
		decisionTreeBuilder = new DecisionTreeBuilder();
	}
	
	public void copyTree(TreeNode tree, TreeNode bestPruneTree) {
		bestPruneTree.setIsLeaf(tree.isLeaf());
		bestPruneTree.setName(tree.getName());
		bestPruneTree.setLeafName(tree.getLeafName());
		
		if(!tree.isLeaf()) {
			bestPruneTree.setLeftNode(new TreeNode());
			bestPruneTree.setRightNode(new TreeNode());
			copyTree(tree.getLeftNode(), bestPruneTree.getLeftNode());
			copyTree(tree.getRightNode(), bestPruneTree.getRightNode());
		}
		
	}
	
	public void replaceNode(TreeNode root, int P) {
		if(!root.isLeaf()) {
			if(root.getNodeNo() == P) {
				String 	modifiedLeafName = decisionTreeBuilder.getMajorityClass(root);
				root.setLeafName(modifiedLeafName);
				root.setIsLeaf(true);
				root.setLeftNode(null);
				root.setRightNode(null);
			}
			else {
				replaceNode(root.getLeftNode(), P);
				replaceNode(root.getRightNode(), P);
			}
		}
	}
	
	public TreeNode pruneTree(TreeNode tree, int L, int K, ArrayList<ArrayList<String>> validationData) {
		bestPruneTree = new TreeNode();
		copyTree(tree, bestPruneTree);
		double accuracyBestPruneTree = decisionTreeBuilder.computeAccuracy(bestPruneTree, validationData);
		
		tempPruneTree = new TreeNode();
		for(int i = 1; i <= L; i++) {
			copyTree(tree, tempPruneTree);
			Random random = new Random();
			int M = 1 + random.nextInt(K);
			for(int j = 0; j <= M; j++) {
				int N = decisionTreeBuilder.getNonLeafNodeCount(tempPruneTree);
				//System.out.println("-----" + N + "-------");
				if(N > 1) {
					int P = random.nextInt(N) + 1;
					replaceNode(tempPruneTree, P);
				} else {
					break;
				}
			}
			
			double accuracyTempTree = decisionTreeBuilder.computeAccuracy(tempPruneTree, validationData);
			//System.out.println("Temp Tree" + accuracyTempTree);
			if(accuracyTempTree > accuracyBestPruneTree) {
				accuracyBestPruneTree = accuracyTempTree;
				System.out.println("Best Tree" + accuracyBestPruneTree);
				copyTree(tempPruneTree, bestPruneTree);
			}
		}
		
		return bestPruneTree;
	}

}
