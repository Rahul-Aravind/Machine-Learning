import java.util.Set;

public class TreeNode {
	private String name;
	private TreeNode left;
	private TreeNode right;
	private boolean isLeaf;
	private String leafname;
	private static int level = -1;
	private Set<String> attributes;
	private int nodeNo; //used in post pruning algorithm
	
	public TreeNode() {
		super();
	}
	
	public TreeNode(String leafname) {
		this.leafname = leafname;
		this.setIsLeaf(true);
	}
	
	public TreeNode(String name, TreeNode left, TreeNode right) {
		this.name = name;
		this.setIsLeaf(false);
		this.left = left;
		this.right = right;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(boolean val) {
		this.isLeaf = val;
	}
	
	public TreeNode getLeftNode() {
		return left;
	}
	
	public void setLeftNode(TreeNode left) {
		this.left = left;
	}
	
	public TreeNode getRightNode() {
		return right;
	}
	
	public void setRightNode(TreeNode right) {
		this.right = right;
	}
	
	public Set<String> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(Set<String> attributes) {
		this.attributes = attributes;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLeafName() {
		return leafname;
	}
	
	public void setLeafName(String leafname) {
		this.leafname = leafname;
	}
	
	public int getNodeNo() {
		return nodeNo;
	}
	
	public void setNodeNo(int nodeNo) {
		this.nodeNo = nodeNo;
	}
	
	public void printTree() {
		level++;
		if(this.name == null) {
			System.out.print(" : " + leafname);
		}
		else {
			System.out.println();
			for(int i = 0; i < level; i++) {
				System.out.print(" | ");
			}
			System.out.print(name + " = 0");
		}
		
		if(left != null) {
			left.printTree();
			if(this.name == null) {
				System.out.print(" : " + leafname);
			}
			else {
				System.out.println();
				for(int i = 0; i < level; i++) {
					System.out.print(" | ");
				}
				System.out.print(name + " = 1");
			}
			right.printTree();
		}
		level--;
	}

}
