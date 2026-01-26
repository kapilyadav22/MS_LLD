package FileSystem.CRUDAllOperations;


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

    void overwriteContent(String data) {
        content.setLength(0);
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

// -------------------- FileSystem2 --------------------
public class FileSystem2 {

    private final DirectoryNode root;

    public FileSystem2() {
        root = new DirectoryNode("/");
    }

    // -------------------- CREATE --------------------

    public void mkdir(String path) {
        traverseAndCreate(path, false);
    }

    public void addFile(String path, String content) {
        FileNode file = (FileNode) traverseAndCreate(path, true);
        file.addContent(content);
    }

    // -------------------- READ --------------------

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

    public String readFile(String path) {
        Node node = traverse(path);
        if (!node.isFile()) {
            throw new IllegalArgumentException("Not a file");
        }
        return ((FileNode) node).read();
    }

    // -------------------- UPDATE --------------------

    // overwrite file content
    public void updateFile(String path, String newContent) {
        Node node = traverse(path);
        if (!node.isFile()) {
            throw new IllegalArgumentException("Not a file");
        }
        ((FileNode) node).overwriteContent(newContent);
    }

    // rename file or directory
    public void rename(String path, String newName) {
        ParentAndNode pan = getParent(path);
        DirectoryNode parent = pan.parent;
        Node node = pan.node;

        parent.children.remove(node.name);
        node.name = newName;
        parent.children.put(newName, node);
    }

    // -------------------- DELETE --------------------

    public void delete(String path) {
        ParentAndNode pan = getParent(path);
        pan.parent.children.remove(pan.node.name);
        // GC will take care of recursive cleanup
    }

    // -------------------- Helpers --------------------

    private Node traverse(String path) {
        if (path.equals("/")) return root;

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


    // helper to find parent + node
    private ParentAndNode getParent(String path) {
        if (path.equals("/")) {
            throw new IllegalArgumentException("Cannot operate on root");
        }

        int idx = path.lastIndexOf("/");
        String parentPath = (idx == 0) ? "/" : path.substring(0, idx);
        String name = path.substring(idx + 1);

        DirectoryNode parent = (DirectoryNode) traverse(parentPath);
        Node node = parent.children.get(name);

        if (node == null) {
            throw new IllegalArgumentException("Path does not exist");
        }
        return new ParentAndNode(parent, node);
    }
}

// -------------------- Helper DTO --------------------
class ParentAndNode {
    DirectoryNode parent;
    Node node;

    ParentAndNode(DirectoryNode parent, Node node) {
        this.parent = parent;
        this.node = node;
    }
}

/*
✅ CRUD Scope (clear & reasonable)

We will support:

CREATE

    mkdir(path)
    addFile(path, content) (create if not exists)

READ
    ls(path)
    readFile(path)

UPDATE
    updateFile(path, newContent) → overwrite
    rename(path, newName)

DELETE
    delete(path)
    deletes file
    deletes directory recursively
    No over-engineering. No OS semantics.
 */
