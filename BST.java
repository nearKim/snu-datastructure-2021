// (Nearly) Optimal Binary Search Tree
// Bongki Moon (bkmoon@snu.ac.kr)

public class BST { // Binary Search Tree implementation

  protected boolean NOBSTified = false;
  protected boolean OBSTified = false;

  public BST() { }

  public int size() { }
  public void insert(String key) { }
  public boolean find(String key) { }

  public int sumFreq() { }
  public int sumProbes() { }
  public int sumWeightedPath() { }
  public void resetCounters() { }

  public void nobst() { }	// Set NOBSTified to true.
  public void obst() { }	// Set OBSTified to true.
  public void print() { }

  public static class Node {
    protected int frequency = 1;
    protected int accessCnt = 0;
    public String value;
    public Node left;
    public Node right;

    public Node(String value) {
      this.value = value;
    }

    public Node(String value, Node left, Node right) {
      this.value = value;
      this.left = left;
      this.right = right;
    }

    public void setLeft(Node left) {
      this.left = left;
    }
    public void setRight(Node right) {
      this.right = right;
    }

    public Node getNext(String val) {
      this.accessCnt += 1;
      if (this.value.equals(val)) {
        return this;
      } else if (this.value.compareTo(val) > 0) {
        return this.left;
      } else {
        return this.right;
      }
    }

    public boolean isLeaf() {
      return (this.left == null) && (this.right == null);
    }

  }
}

