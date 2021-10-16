// (Nearly) Optimal Binary Search Tree
// Bongki Moon (bkmoon@snu.ac.kr)

import java.util.HashMap;
import java.util.Objects;

public class BST { // Binary Search Tree implementation
  private final HashMap<String, Node> nodesCache = new HashMap<>();
  protected boolean NOBSTified = false;
  protected boolean OBSTified = false;
  public Node root;

  public BST() {  }

  public int size() {
    return nodesCache.size();
  }

  private Node insertNode(String key) {
    // 실제로 Key가 없을 경우 Node를 삽입하고 삽입된 Node를 리턴한다
    if (root == null) {
      root = new Node(key);
      return root;
    }
    Node node = root;
    while (!node.isLeaf()) {
      node = node.getNext(key);
    }
    node = node.insertChild(key);
    return node;
  }

  public void insert(String key) {
    if (nodesCache.containsKey(key)){
      Node node = nodesCache.get(key);
      node.addFreq();
    } else {
      Node node = insertNode(key);
      nodesCache.put(key, node);
    }
  }

  private Node findNode(String key) {
    if (root == null) {
      return null;
    }
    Node result = root;

    while (!Objects.equals(result.value, key)) {
      result = result.getNext(key);
      if (result == null) {
        break;
      }
    }
    return result;
  }

  public boolean find(String key) {
    return this.findNode(key) != null;
  }

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

    public Node insertChild(String value) {
      Node node = new Node(value);
      int comparator = this.value.compareTo(value);

      if (comparator > 0) {
          this.left = node;
      } else if (comparator < 0) {
        this.right = node;
      } else {
        System.out.println("Freq를 올려야 합니다!");
      }
      return node;
    }

    public void addFreq() {
      this.frequency +=1;
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

