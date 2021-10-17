// AVL Binary Search Tree
// Bongki Moon (bkmoon@snu.ac.kr)
  
public class AVL extends BST
{
  public AVL() { }

  public void insert(String key) {
    super.insert(key);
  }

  @Override
  protected Node insertNode(String key) {
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
}

