import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DecisionTreeBuilder {
	
	private int nonLeafNodeCount = 0;
	
	public DecisionTreeBuilder() {
	}
	
	public TreeNode buildTree(ArrayList<String> attributeList, ArrayList<ArrayList<String>> dataSet, boolean isVarianceImpurity) {
		int zeros = 0; // zero count
		int ones = 0; // one count
		
		for(int i = 1; i < dataSet.size(); i++) {
			if(dataSet.get(i).get(dataSet.get(i).size() - 1).equalsIgnoreCase("1"))
				ones++;
			else
				zeros++;
		}
		
		if(attributeList.isEmpty() || zeros == dataSet.size() - 1) {
			return new TreeNode("0");
		}
		else if(attributeList.isEmpty() || ones == dataSet.size() - 1) {
			return new TreeNode("1");
		}
		else {
			InformationGain infoGain = new InformationGain();
			String bestAttr = infoGain.mostLikelyAttribute(dataSet, attributeList, isVarianceImpurity);
			
			//System.out.println("-----" + bestAttr + "------");
			HashMap<String, ArrayList<ArrayList<String>>> bestAttrMapData =  infoGain.getMapDataForBestAttr(bestAttr, dataSet);
			
			//remove the attribute from the attribute list for which you have already constructed the node.
			ArrayList<String> newAttributeList = new ArrayList<String>();
			for(String attr : attributeList) {
				if(!attr.equalsIgnoreCase(bestAttr)) {
					newAttributeList.add(attr);
				}
			}
			
			if(bestAttrMapData.size() < 2) {
				String name = "0";
				if(ones > zeros) {
					name = "1";
				}
				
				return new TreeNode(name);
			}	
			TreeNode left = buildTree(newAttributeList, bestAttrMapData.get("0"), isVarianceImpurity);
			TreeNode right = buildTree(newAttributeList, bestAttrMapData.get("1"), isVarianceImpurity);
			return new TreeNode(bestAttr, left, right);
		}
	}
	
	public boolean checkOutputforDataFromTree(TreeNode tree, ArrayList<String> rowData, ArrayList<String> attributeList) {
		TreeNode cur = tree;
		
		while(true) {
			if(cur.isLeaf()) {
				if(cur.getLeafName().equalsIgnoreCase(rowData.get(rowData.size() - 1))) return true;
				else return false;
			}
			
			int idx = attributeList.indexOf(cur.getName());
			String val = rowData.get(idx);
			if(val.equalsIgnoreCase("0")) {
				cur = cur.getLeftNode();
			} else {
				cur = cur.getRightNode();
			}
		}
	}
	
	public double computeAccuracy(TreeNode tree, ArrayList<ArrayList<String>> dataSet) {
		int pos = 0;
		ArrayList<String> attributeList = dataSet.get(0);
		
		for(ArrayList<String> rowData : dataSet.subList(1, dataSet.size())) {
			boolean chk = checkOutputforDataFromTree(tree, rowData, attributeList);
			
			if(chk) pos++;
		}
		double accuracy;
		accuracy = ((double) pos / (double) (dataSet.size() - 1)) * 100.00;
		return accuracy;
	}
	
	public void calculateNonLeafNodeCount(TreeNode root) {
		if(!root.isLeaf()) {
			nonLeafNodeCount++;
			//System.out.println("Inside Non-leaf function " + nonLeafNodeCount);
			root.setNodeNo(nonLeafNodeCount);
			calculateNonLeafNodeCount(root.getLeftNode());
			calculateNonLeafNodeCount(root.getRightNode());
		}
	}
	
	public int getNonLeafNodeCount(TreeNode root) {
		calculateNonLeafNodeCount(root);
		int N = nonLeafNodeCount;
		nonLeafNodeCount = 0;
		return N;
	}
	
	public List<TreeNode> getLeafNodes(TreeNode root) {
		List<TreeNode> leafNodeList = new ArrayList<TreeNode>();
		if(root.isLeaf()) {
			leafNodeList.add(root);
		}
		
		else {
			if(!root.getLeftNode().isLeaf()) {
				getLeafNodes(root.getLeftNode());
			}
			if(!root.getRightNode().isLeaf()) {
				getLeafNodes(root.getRightNode());
			}
		}
		return leafNodeList;
	}
	
	public String getMajorityClass(TreeNode root) {
		int zeros = 0;
		int ones = 0;
		
		List<TreeNode> leafNodesList = getLeafNodes(root);
		for(TreeNode node : leafNodesList) {
			if(node.getLeafName().equalsIgnoreCase("1")) ones++;
			else zeros++;
		}
		
		String majority = "0";
		if(ones > zeros) {
			majority = "1";
		}
		
		return majority;
	}

}
