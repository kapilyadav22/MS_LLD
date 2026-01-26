package FileSystem.SimpleOne;



import java.util.*;

// -------------------- Node --------------------
abstract class Node {
    protected String name;

    Node(String name) {
        this.name = name;
    }

    abstract boolean isFile();
}

// -------------------- File --------------------
class FileNode extends Node {
    private StringBuilder content;

    FileNode(String name) {
        super(name);
        this.content = new StringBuilder();
    }

    @Override
    boolean isFile() {
        return true;
    }

    void addContent(String data) {
        content.append(data);
    }

    String read() {
        return content.toString();
    }
}

// -------------------- Directory --------------------
class DirectoryNode extends Node {
    Map<String, Node> children;

    DirectoryNode(String name) {
        super(name);
        this.children = new HashMap<>();
    }

    @Override
    boolean isFile() {
        return false;
    }
}

// -------------------- FileSystem --------------------
public class FileSystem {

    private final DirectoryNode root;

    public FileSystem() {
        root = new DirectoryNode("/");
    }

    // ls
    public List<String> ls(String path) {
        Node node = traverse(path);
        List<String> result = new ArrayList<>();

        if (node.isFile()) {
            result.add(node.name);
        } else {
            DirectoryNode dir = (DirectoryNode) node;
            result.addAll(dir.children.keySet());
            Collections.sort(result);
        }
        return result;
    }

    // mkdir
    public void mkdir(String path) {
        traverseAndCreate(path, false);
    }

    // addFile
    public void addFile(String path, String content) {
        FileNode file = (FileNode) traverseAndCreate(path, true);
        file.addContent(content);
    }

    // readFile
    public String readFile(String path) {
        Node node = traverse(path);
        if (!node.isFile()) {
            throw new IllegalArgumentException("Path is not a file");
        }
        return ((FileNode) node).read();
    }

    // -------------------- Helpers --------------------

    private Node traverse(String path) {
        String[] parts = path.split("/");
        Node curr = root;

        for (int i = 1; i < parts.length; i++) {
            if (!(curr instanceof DirectoryNode)) {
                throw new IllegalArgumentException("Invalid path");
            }
            curr = ((DirectoryNode) curr).children.get(parts[i]);
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
            String part = parts[i];

            curr.children.putIfAbsent(
                    part,
                    (i == parts.length - 1 && isFile)
                            ? new FileNode(part)
                            : new DirectoryNode(part)
            );

            Node next = curr.children.get(part);

            // if we are at the last segment, return it
            if (i == parts.length - 1) {
                return next;
            }

            // intermediate nodes MUST be directories
            if (next.isFile()) {
                throw new IllegalArgumentException(
                        "Invalid path: file in the middle of path"
                );
            }

            curr = (DirectoryNode) next;
        }
        return curr;
    }

}

