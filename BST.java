// (Nearly) Optimal Binary Search Tree
// Bongki Moon (bkmoon@snu.ac.kr)

import java.util.Map;
import java.util.TreeMap;

import static java.lang.Math.max;

public class BST { // Binary Search Tree implementation
  private final TreeMap<String, Node> nodesCache = new TreeMap<>();
  protected boolean NOBSTified = false;
  protected boolean OBSTified = false;
  protected boolean shouldRecalculateLevel = false;
  public Node root;

  public BST() {  }

  public int size() {
    return nodesCache.size();
  }

  protected int height(Node node) {
    if (node == null) return -1;
    return node.height;
  }

  protected void updateHeight(Node node) {
    if (node != null) {
      node.height = 1 + Math.max(height(node.left), height(node.right));
    }
  }

  protected Node insertNode(String key) {
    // 실제로 Key가 없을 경우 Node를 삽입하고 삽입된 Node를 리턴한다
    if (root == null) {
      this.root = new Node(key);
      return this.root;
    }
    Node node = this.root;
    while (true) {
      if (node.isLeaf()) break;

      Node nextNode = node.getNext(key);

      if (nextNode == null) break;

      node = nextNode;
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

  private Node findNode(Node node, String key) {
    if (node == null) return null;

    int comp = node.value.compareTo(key);
    node.addAccessCnt();

    if (comp == 0) {
      return node;
    } else if (comp < 0) {
      return findNode(node.right, key);
    } else {
      return findNode(node.left, key);
    }
  }

  public boolean find(String key) {
    Node foundNode = findNode(this.root, key);
    return foundNode != null;
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
    if (shouldRecalculateLevel) {
      calculateLevels(this.root, 0);
    }
    return nodesCache.values().stream().reduce(0, (acc, node) -> (acc + node.frequency * (1 + node.level)), Integer::sum);
  }

  public void calculateLevels(Node node, int level) {
    if (node == null) return;

    node.level = level;
    calculateLevels(node.left, level + 1);
    calculateLevels(node.right, level + 1);
  }

  public void nobst() {
    NOBSTified = true;
    shouldRecalculateLevel = true;
    Node[] allNodes = nodesCache.values().toArray(new Node[0]);
    this.root = getMinWeightDiffNode(0, size(), sumFreq(), allNodes);
    if (this.root != null) {
      this.root.parent = null;
    }
  }

  private Node getMinWeightDiffNode(int left, int right, int totalSum, Node[] sortedNodeArr) {
    if (left >= right) {
      return null;
    } else if (left + 1 == right) {
      Node resultNode = sortedNodeArr[left];
      resultNode.left = null;
      resultNode.right = null;
      return resultNode;
    }
    var wrapper = new Object() {
      int resultIdx = left;
      int leftTotalSum = 0;
      int rightTotalSum = 0;
    };
    int minDiff = Integer.MAX_VALUE;
    int leftSum = 0;
    int rightSum = totalSum;

    for (int i=left; i<right; i++) {
      Node curNode = sortedNodeArr[i];
      rightSum -= curNode.frequency;
      int diff = Math.abs(leftSum - rightSum);
      if (diff < minDiff) {
        minDiff = diff;
        wrapper.resultIdx = i;
        wrapper.leftTotalSum = leftSum;
        wrapper.rightTotalSum = rightSum;
      }
      leftSum += curNode.frequency;
    }
    Node resultNode = sortedNodeArr[wrapper.resultIdx];
    resultNode.setLeft(getMinWeightDiffNode(left, wrapper.resultIdx, wrapper.leftTotalSum, sortedNodeArr));
    resultNode.setRight(getMinWeightDiffNode(wrapper.resultIdx+1, right, wrapper.rightTotalSum, sortedNodeArr));
    return resultNode;
  }

  public void obst() {
    OBSTified = true;
    shouldRecalculateLevel = true;

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
        for (int r=i; r <= j; r++) {
          freqSum += allNodes[r].frequency;

          int lhs = r-1 < 0 ? 0 : costTable[i][r-1];
          int rhs = r+1 >= size ? 0 : costTable[r+1][j];
          int subtreeCost =  lhs + rhs;

          if (subtreeCost < minimum) {
            minimum = subtreeCost;
            rootTable[i][j] = r;
          }
        }
        costTable[i][j] = freqSum + minimum;
      }
    }

    this.root = buildObst(0, size-1, rootTable, allNodes);
    if (this.root != null) {
      this.root.parent = null;
    }
  }

  private Node buildObst(int left, int right, int[][] rootTable, Node[] allNodes) {
    if (left > right) {
      return null;
    }
    int idx = rootTable[left][right];
    Node node = allNodes[idx];
    node.setLeft(buildObst(left, idx-1, rootTable, allNodes));
    node.setRight(buildObst(idx+1, right, rootTable, allNodes));

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
    protected int height = 0;
    public String value;
    public Node parent;
    public Node left;
    public Node right;

    public Node(String value) {
      this.value = value;
    }

    public Node(String value, Node parent) {
      this.value = value;
      this.parent = parent;
    }

    public Node(String value, Node parent, int level) {
      this.value = value;
      this.parent = parent;
      this.level = level;
    }

    public void setLeft(Node leftNode) {
      this.left = leftNode;
      if (leftNode != null) {
        leftNode.parent = this;
      }
    }

    public void setRight(Node rightNode) {
      this.right = rightNode;
      if (rightNode != null) {
        rightNode.parent = this;
      }
    }

    public Node insertChild(String value) {
      Node node = new Node(value,this, level + 1);

      int comparator = this.value.compareTo(value);

      if (comparator > 0) {
        setLeft(node);
      } else if (comparator < 0) {
        setRight(node);
      } else {
        throw new IllegalArgumentException("기존값과 동일한 value가 insertChild에 들어왔습니다");
      }
      return node;
    }

    public void addFreq() {
      this.frequency += 1;
    }

    public void addAccessCnt() {
      this.accessCnt += 1;
    }

    public void reset() {
      this.accessCnt = 0;
      this.frequency = 0;
    }

    public Node getNext(String val) {
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

