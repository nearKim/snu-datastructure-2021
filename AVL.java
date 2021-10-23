// AVL Binary Search Tree
// Bongki Moon (bkmoon@snu.ac.kr)


public class AVL extends BST
{
  public static final int LEFT = 0;
  public static final int RIGHT = 1;
  public static final int LEFT_RIGHT = 2;
  public static final int RIGHT_LEFT = 3;

  public AVL() { }

  public void insert(String key) {
    super.insert(key);
  }

  @Override
  protected Node insertNode(String key) {
    Node insertedNode = super.insertNode(key);

    // Level 1까지는 Rotation을 할 필요가 없다
    if (insertedNode.level < 2) return insertedNode;

    // X: 현재 Node, Y: 부모 Node, Z: 조부모 Node
    Node x = insertedNode;
    Node y = x.parent;
    Node z = y.parent;
    boolean unbalancedNodeFound = false;

    // insertedNode 기준으로 parent를 찾으면서 unbalanced node(Z)를 찾는다
    while (true) {
      int grandParentBF = getBalanceFactor(z);
      if (grandParentBF > 1 || grandParentBF < -1) {
        unbalancedNodeFound = true;
        break;
      }
      if (z == root) break;

      // 손자(X), 자식(Y) node를 저장한다
      x = y;
      y = z;
      z = z.parent;
    }

    if (unbalancedNodeFound) {
      // X, Y, Z의 생김새에 따라 Rotate을 시킨다
      int rotationStrategy = selectRotationStrategy(x, y, z);
      switch (rotationStrategy){
        case LEFT:
          rotateLeft(y, z);
          break;
        case RIGHT:
          rotateRight(y, z);
          break;
        case LEFT_RIGHT:
          rotateLeftRight(x, y, z);
          break;
        case RIGHT_LEFT:
          rotateRightLeft(x, y, z);
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + rotationStrategy);
      }
    }
    return insertedNode;
  }
  private void rotateLeft(Node y, Node z) {
    y.parent = z.parent;
    Node tmp = y.left;
    y.setLeft(z);
    z.setRight(tmp);
  }

  private void rotateRight(Node y, Node z) {
    y.parent = z.parent;
    Node tmp = y.right;
    y.setRight(z);
    z.setLeft(tmp);
  }

  private void rotateLeftRight(Node x, Node y, Node z) {
    // 가장 아래 2개인 x < y Rotate
    rotateLeft(x, y);
    // x가 올라오므로 위의 2개인 x < z Rotate
    rotateRight(x, z);
  }
  private void rotateRightLeft(Node x, Node y, Node z) {
    rotateRight(x, y);
    rotateLeft(x, z);
  }

  private int selectRotationStrategy(Node x, Node y, Node z) {
    // 현재, 부모, 조부모 Node의 key를 이용하여 회전방향을 정한다
    String x_val = x.value;
    String y_val = y.value;
    String z_val = z.value;
    int z_y_cmp = z_val.compareTo(y_val);
    int y_x_cmp = y_val.compareTo(x_val);

    if (z_y_cmp == 0 || y_x_cmp == 0) {
      throw new IllegalArgumentException("X, Y, Z 중 동일한 값이 들어왔습니다.");
    }

    if (z_y_cmp < 0) {
      // Z < Y: Y가 Z 오른쪽
      if (y_x_cmp < 0) {
        // Y < X: X가 Y 오른쪽
        // Y를 중심으로 Left Rotation
        return LEFT;
      } else {
        // Y > X: X가 Y 왼쪽
        // Y를 중심으로 Right Rotation + 새로운 X 중심으로 Left Rotation
        return RIGHT_LEFT;
      }
    } else {
      //
      if (y_x_cmp < 0) {
        // y_val < x_val: x가 y 오른쪽
        return LEFT_RIGHT;
      } else {
        return RIGHT;
      }
    }
  }

  public int getBalanceFactor(Node node) {
    if (node == null) return 0;

    int leftHeight = node.left == null ? 0 : node.left.height;
    int rightHeight = node.right == null ? 0 : node.right.height;
    return leftHeight - rightHeight;
  }
}

