// AVL Binary Search Tree
// Bongki Moon (bkmoon@snu.ac.kr)


public class AVL extends BST
{
  public static final int LEFT = 0;
  public static final int RIGHT = 1;
  public static final int LEFT_RIGHT = 2;
  public static final int RIGHT_LEFT = 3;
  public static final int DO_NOTHING = -1;

  public AVL() { }

  public void insert(String key) {
    super.insert(key);
  }

  private int getRebalanceStrategy(Node node) {
    if (getBalanceFactor(node) == -2) {
      if (getBalanceFactor(node.right) < 0) {
        return LEFT;
      } else {
        return RIGHT_LEFT;
      }
    } else if (getBalanceFactor(node) == 2) {
      if (getBalanceFactor(node.left) > 0) {
        return RIGHT;
      } else {
        return LEFT_RIGHT;
      }
    }
    return DO_NOTHING;
  }

  private void rebalance(Node node) {
    updateHeight(node);

    switch (getRebalanceStrategy(node)) {
      case LEFT:
        node = rotateLeft(node);
        break;
      case RIGHT:
        node = rotateRight(node);
        break;
      case LEFT_RIGHT:
        node = rotateLeftRight(node);
        break;
      case RIGHT_LEFT:
        node = rotateRightLeft(node);
        break;
      case DO_NOTHING:
        break;
    }

    if (node.parent == null) {
      this.root = node;
      return;
    }
    rebalance(node.parent);
  }

  private Node rotateLeft(Node upper) {
    this.shouldRecalculateLevel = true;
    Node lower = upper.right;
    processGrandparentLeftRight(lower, upper);

    upper.setRight(lower.left);
    lower.setLeft(upper);

    updateHeight(upper);
    updateHeight(lower);

    // 새로운 upper 노드
    return lower;
  }

  private Node rotateRight(Node upper) {
    this.shouldRecalculateLevel = true;
    Node lower = upper.left;
    processGrandparentLeftRight(lower, upper);

    upper.setLeft(lower.right);
    lower.setRight(upper);

    updateHeight(upper);
    updateHeight(lower);

    // 새로운 upper 노드
    return lower;
  }

  private Node rotateLeftRight(Node upper) {
    upper.left = rotateLeft(upper.left);
    return rotateRight(upper);
  }

  private Node rotateRightLeft(Node upper) {
    upper.right = rotateRight(upper.right);
    return rotateLeft(upper);
  }

  @Override
  protected Node insertNode(String key) {
    Node insertedNode = super.insertNode(key);

    if (insertedNode.level > 1) {
      rebalance(insertedNode);
    }

    return insertedNode;
  }

  private void processGrandparentLeftRight(Node y, Node z) {
    if (z == this.root) {
      y.parent = null;
      this.root = y;
    } else {
      Node zParent = z.parent;
      y.parent = zParent;

      if (zParent.left == z) {
        zParent.left = y;
      } else {
        zParent.right = y;
      }
    }
  }

  public int getBalanceFactor(Node node) {
    if (node == null) return -1;

    int leftHeight = node.left == null ? -1 : node.left.height;
    int rightHeight = node.right == null ? -1 : node.right.height;
    return leftHeight - rightHeight;
  }
}

