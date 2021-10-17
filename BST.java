// (Nearly) Optimal Binary Search Tree
// Bongki Moon (bkmoon@snu.ac.kr)

import java.util.Map;
import java.util.TreeMap;

public class BST { // Binary Search Tree implementation
  private final TreeMap<String, Node> nodesCache = new TreeMap<>();
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

//  private Node findNode(String key) {
//    if (root == null) {
//      return null;
//    }
//    Node result = root;
//
//    while (!result.value.equals(key)) {
//      result = result.getNext(key);
//      if (result == null) {
//        break;
//      }
//    }
//    return result;
//  }

  public boolean find(String key) {
    return nodesCache.get(key) != null;
  }

  public int sumFreq() {
      return nodesCache.values().stream().mapToInt(node -> node.frequency).sum();
  }
  public int sumProbes() {
      return nodesCache.values().stream().mapToInt(node -> node.accessCnt).sum();
  }
  public void resetCounters() {
    nodesCache.values().forEach(Node::reset);
  }

  public int sumWeightedPath() {
      return nodesCache.values().stream().reduce(0, (acc, node) -> (acc + node.frequency * node.level), Integer::sum);
  }

  public void nobst() { }	// Set NOBSTified to true.
  public void obst() {
    OBSTified = true;
    int size = this.size();
    int[][] costTable = new int[size][size];
    int[][] rootTable = new int[size][size];
    Node[] allNodes = nodesCache.values().toArray(new Node[0]);

    for (int d=0; d<size; d++) {
      for (int i=0; i<size-d; i++) {
        int j=d+i;

        if (d == 0) {
          // 첫번째 대각선 기본값 처리
          costTable[i][j] = allNodes[i].frequency;
          rootTable[i][j] = i;
          continue;
        }

        int minimum = Integer.MAX_VALUE;
        int freqSum = 0;
        for (int r=i; r < j+1; r++) {
          freqSum += allNodes[r].frequency;
          int subtreeCost = costTable[i][r-1] + costTable[r+1][j];
          if (subtreeCost < minimum) {
            minimum = subtreeCost;
            rootTable[i][j] = r;
          }
        }
        costTable[i][j] = freqSum + minimum;
      }
    }

    root = buildObst(0, size, rootTable, allNodes);
  }

  private Node buildObst(int i, int j, int[][] rootTable, Node[] allNodes) {
    if (i == j) {
      return null;
    }
    int idx = rootTable[i][j];
    Node node = allNodes[idx];
    node.left = buildObst(i, idx-1, rootTable, allNodes);
    node.right = buildObst(idx+1, j, rootTable, allNodes);

    return node;
  }
  public void print() {
    for (Map.Entry<String, Node> entry : nodesCache.entrySet()) {
      String key = entry.getKey();
      Node node = entry.getValue();
      System.out.println("[" + key +":" + node.frequency + ":" + node.accessCnt +"]" );
    }
  }

  public static class Node {
    protected int frequency = 1;
    protected int accessCnt = 0;
    protected int level = 0;
    public String value;
    public Node left;
    public Node right;

    public Node(String value) {
      this.value = value;
    }
    public Node(String value, int level) {
      this.value = value;
      this.level = level;
    }

    public Node(String value, Node left, Node right) {
      this.value = value;
      this.left = left;
      this.right = right;
    }

    public Node insertChild(String value) {
      Node node = new Node(value, level + 1);
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

    public void reset() {
      this.accessCnt = 0;
      this.frequency = 0;
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

