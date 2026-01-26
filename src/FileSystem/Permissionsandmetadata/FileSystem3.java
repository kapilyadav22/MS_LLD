package FileSystem.Permissionsandmetadata;

import java.util.*;

// -------------------- Permission --------------------
enum Permission {
    READ, WRITE, EXECUTE
}

// -------------------- Metadata --------------------
class Metadata {
    long createdAt;
    long modifiedAt;
    long size;

    Metadata() {
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.modifiedAt = now;
        this.size = 0;
    }

    void updateSize(long size) {
        this.size = size;
        this.modifiedAt = System.currentTimeMillis();
    }
}

// -------------------- Node --------------------
abstract class Node {
    protected String name;
    protected Metadata metadata;
    protected Map<String, Set<Permission>> permissions;

    Node(String name) {
        this.name = name;
        this.metadata = new Metadata();
        this.permissions = new HashMap<>();
    }

    abstract boolean isFile();

    void grantPermission(String user, Permission permission) {
        permissions
                .computeIfAbsent(user, k -> new HashSet<>())
                .add(permission);
    }

    boolean hasPermission(String user, Permission permission) {
        return permissions.containsKey(user)
                && permissions.get(user).contains(permission);
    }
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
        metadata.updateSize(content.length());
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

// -------------------- FileSystem3 --------------------
public class FileSystem3 {

    private final DirectoryNode root;

    public FileSystem3() {
        root = new DirectoryNode("/");
        // root accessible to all
        root.grantPermission("admin", Permission.READ);
        root.grantPermission("admin", Permission.WRITE);
        root.grantPermission("admin", Permission.EXECUTE);
    }

    // -------------------- ls --------------------
    public List<String> ls(String path, String user) {
        Node node = traverse(path);

        if (!node.hasPermission(user, Permission.READ)) {
            throw new SecurityException("Read permission denied");
        }

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

    // -------------------- mkdir --------------------
    public void mkdir(String path, String user) {
        traverseAndCreate(path, false, user);
    }

    // -------------------- addFile --------------------
    public void addFile(String path, String content, String user) {
        FileNode file = (FileNode) traverseAndCreate(path, true, user);
        file.addContent(content);
    }

    // -------------------- readFile --------------------
    public String readFile(String path, String user) {
        Node node = traverse(path);

        if (!node.isFile()) {
            throw new IllegalArgumentException("Not a file");
        }
        if (!node.hasPermission(user, Permission.READ)) {
            throw new SecurityException("Read permission denied");
        }
        return ((FileNode) node).read();
    }

    // -------------------- grantPermission --------------------
    public void grantPermission(String path, String owner, String targetUser, Permission permission) {
        Node node = traverse(path);

        if (!node.hasPermission(owner, Permission.WRITE)) {
            throw new SecurityException("Write permission required");
        }
        node.grantPermission(targetUser, permission);
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

    private Node traverseAndCreate(String path, boolean isFile, String user) {
        String[] parts = path.split("/");
        DirectoryNode curr = root;

        for (int i = 1; i < parts.length; i++) {
            if (!curr.hasPermission(user, Permission.WRITE)) {
                throw new SecurityException("Write permission denied");
            }

            String part = parts[i];
            curr.children.putIfAbsent(
                    part,
                    (i == parts.length - 1 && isFile)
                            ? new FileNode(part)
                            : new DirectoryNode(part)
            );

            Node next = curr.children.get(part);
            if (next.isFile() && i != parts.length - 1) {
                throw new IllegalArgumentException("Invalid path");
            }
            curr = (DirectoryNode) next;
        }
        return curr;
    }
}
