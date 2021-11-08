package ru.luckoff.SplayTree;

import java.io.*;

class Node {
    long key;
    int address;
    Node parent;
    Node left;
    Node right;

    public Node(long key, int address) {
        this.key = key;
        this.address = address;
        this.parent = null;
        this.left = null;
        this.right = null;
    }
}

public class SplayTree {
    public static void main(String [] args) throws FileNotFoundException {
        SplayTree tree = new SplayTree();

        tree.insertFromFile();
        tree.output();
        System.out.println("\n");

        System.out.println("Адресс номера 89627156421 в файле = " + tree.searchTree(89627156421L).address);
        tree.output();
        System.out.println("\n");

        tree.deleteNode(89827421497L);
        tree.deleteNode(89359172451L);
        tree.deleteNode(89268725389L);
        tree.deleteNode(89266715862L); //отсутствует в дереве
        tree.output();
        System.out.println("\n");

        tree.deleteNode(89266715863L);
        tree.output();
        System.out.println("\n");

        System.out.println(searchInFileByAddress(tree.searchTree(89627156421L).address));
    }

    public static String searchInFileByAddress(int address) throws FileNotFoundException {
        String FILE_NAME = "Tree.txt";

        File file = new File(FILE_NAME);
        FileInputStream inputStream = new FileInputStream(file);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.split(" ")[0].equalsIgnoreCase(String.valueOf(address))) {
                    return line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertFromFile() throws FileNotFoundException {
        String FILE_NAME = "Tree.txt";

        File file = new File(FILE_NAME);
        FileInputStream inputStream = new FileInputStream(file);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                int address = Integer.parseInt(line.split(" ")[0]);
                long key = Long.parseLong(line.split(" ")[1]);
                this.insert(key, address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------//
    //--------------Реализация--Дерева--------------//
    //----------------------------------------------//

    private Node root;

    public SplayTree() {
        root = null;
    }


    public void output() {
        outputHelper(this.root, 0);
    }

    void outputHelper(Node node, int l) {
        if (node != null) {
            outputHelper(node.right, l + 1);
            for (int i = 1; i <= l; i++)
                System.out.print("  ");
            System.out.print(node.key + "\n");
            outputHelper(node.left, l + 1);
        }
    }


    public Node searchTree(long k) {
        Node x = searchTreeHelper(root, k);
        if (x != null) {
            splay(x);
        }
        return x;
    }

    private Node searchTreeHelper(Node node, long key) {
        if (node == null || key == node.key) {
            return node;
        }

        if (key < node.key) {
            return searchTreeHelper(node.left, key);
        }
        return searchTreeHelper(node.right, key);
    }


    void deleteNode(long data) {
        deleteNodeHelper(this.root, data);
    }

    private void deleteNodeHelper(Node node, long key) {
        Node x = null;
        Node t = null;
        Node s = null;
        while (node != null){
            if (node.key == key) {
                x = node;
            }

            if (node.key <= key) {
                node = node.right;
            } else {
                node = node.left;
            }
        }
        if (x == null) {
            System.out.println("Ключ " + key + " отсутствует в дереве");
            return;
        }
        splay(x);
        if (x.right != null) {
            t = x.right;
            t.parent = null;
        } else {
            t = null;
        }
        s = x;
        s.right = null;
        x = null;

        if (s.left != null){ // remove x
            s.left.parent = null;
        }
        root = join(s.left, t);
        s = null;
    }


    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != null) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            this.root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    private void splay(Node x) {
        while (x.parent != null) {
            if (x.parent.parent == null) {
                if (x == x.parent.left) {
                    // zig rotation
                    rightRotate(x.parent);
                } else {
                    // zag rotation
                    leftRotate(x.parent);
                }
            } else if (x == x.parent.left && x.parent == x.parent.parent.left) {
                // zig-zig rotation
                rightRotate(x.parent.parent);
                rightRotate(x.parent);
            } else if (x == x.parent.right && x.parent == x.parent.parent.right) {
                // zag-zag rotation
                leftRotate(x.parent.parent);
                leftRotate(x.parent);
            } else if (x == x.parent.right && x.parent == x.parent.parent.left) {
                // zig-zag rotation
                leftRotate(x.parent);
                rightRotate(x.parent);
            } else {
                // zag-zig rotation
                rightRotate(x.parent);
                leftRotate(x.parent);
            }
        }
    }


    private Node join(Node s, Node t){
        if (s == null) {
            return t;
        }

        if (t == null) {
            return s;
        }
        Node x = maximum(s);
        splay(x);
        x.right = t;
        t.parent = x;
        return x;
    }

    public Node maximum(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }


    public void insert(long key, int address) {
        Node node = new Node(key, address);
        Node y = null;
        Node x = this.root;

        while (x != null) {
            y = x;
            if (node.key < x.key) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        node.parent = y;
        if (y == null) {
            root = node;
        } else if (node.key < y.key) {
            y.left = node;
        } else {
            y.right = node;
        }
        splay(node);
    }
}
