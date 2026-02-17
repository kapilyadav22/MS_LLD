package FileSystem.SimpleOne;

import java.util.*;

// ========================= Node =========================
abstract class Node {

    protected final String name;

    Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract List<String> ls();
}

// ========================= FileNode =========================
class FileNode extends Node {

    private final StringBuilder content = new StringBuilder();

    FileNode(String name) {
        super(name);
    }

    public void addContent(String data) {
        content.append(data);
    }

    public String read() {
        return content.toString();
    }

    @Override
    List<String> ls() {
        return List.of(name);
    }
}

// ========================= DirectoryNode =========================
class DirectoryNode extends Node {

    // TreeMap keeps children sorted automatically
    private final Map<String, Node> children = new TreeMap<>();

    DirectoryNode(String name) {
        super(name);
    }

    public Node getChild(String name) {
        return children.get(name);
    }

    public void addChild(Node node) {
        children.put(node.getName(), node);
    }

    public boolean contains(String name) {
        return children.containsKey(name);
    }

    @Override
    List<String> ls() {
        List<String> result = new ArrayList<>(children.keySet());
        Collections.sort(result);
        return result;
    }
}

// ========================= FileSystem =========================
public class FileSystem {

    private final DirectoryNode root = new DirectoryNode("/");

    // -------------------- ls --------------------
    public List<String> ls(String path) {
        return traverse(path).ls();
    }

    // -------------------- mkdir --------------------
    public void mkdir(String path) {
        traverseAndCreate(path, false);
    }

    // -------------------- addFile --------------------
    public void addFile(String path, String content) {
        Node node = traverseAndCreate(path, true);

        if (node instanceof FileNode file) {
            file.addContent(content);
        } else {
            throw new IllegalArgumentException("Path is not a file");
        }
    }

    // -------------------- readFile --------------------
    public String readFile(String path) {
        Node node = traverse(path);

        if (node instanceof FileNode file) {
            return file.read();
        }

        throw new IllegalArgumentException("Path is not a file");
    }

    // ================= Traversal =====================

    private Node traverse(String path) {

        if (path.equals("/")) {
            return root;
        }

        String[] parts = path.split("/");
        Node curr = root;

        for (int i = 1; i < parts.length; i++) {

            if (!(curr instanceof DirectoryNode dir)) {
                throw new IllegalArgumentException("Invalid path: file in middle");
            }

            curr = dir.getChild(parts[i]);

            if (curr == null) {
                throw new IllegalArgumentException("Path does not exist");
            }
        }

        return curr;
    }

    private Node traverseAndCreate(String path, boolean isFile) {

        String[] parts = path.split("/");
        DirectoryNode curr = root;

        for (int i = 1; i < parts.length; i++) {

            String currDir = parts[i];
            boolean isLast = (i == parts.length - 1);

            Node next = curr.getChild(currDir);

            if (next == null) {
                next = createNode(currDir, isLast && isFile);
                curr.addChild(next);
            }

            if (isLast) return next;

            if (!(next instanceof DirectoryNode dir)) {
                throw new IllegalArgumentException("File in middle of path");
            }

            curr = dir;
        }

        return curr;
    }

    private Node createNode(String name, boolean isFile) {
        return isFile ? new FileNode(name)
                : new DirectoryNode(name);
    }

}
