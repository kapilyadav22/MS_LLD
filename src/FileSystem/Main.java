package FileSystem;


import FileSystem.CRUDAllOperations.FileSystem2;
import FileSystem.SimpleOne.FileSystem;

public class Main {

    public static void main(String[] args) {

        FileSystem fs = new FileSystem();

//        FileSystem2 fs = new FileSystem2();
        // -------- CREATE --------
        fs.mkdir("/a");
        fs.mkdir("/a/b");
        fs.addFile("/a/b/c.txt", "hello");

        // -------- READ --------
        System.out.println(fs.ls("/"));        // [a]
        System.out.println(fs.ls("/a/b"));     // [c.txt]
        System.out.println(fs.ls("/a/b/c.txt")); // [c.txt]
        System.out.println(fs.readFile("/a/b/c.txt")); // hello
        /*

        // -------- UPDATE --------
        fs.updateFile("/a/b/c.txt", "hello world");
        System.out.println(fs.readFile("/a/b/c.txt")); // hello world

        fs.rename("/a/b/c.txt", "d.txt");
        System.out.println(fs.ls("/a/b"));     // [d.txt]

        // -------- DELETE --------
        fs.delete("/a/b/d.txt");
        System.out.println(fs.ls("/a/b"));     // []

        fs.delete("/a"); // recursive delete
        System.out.println(fs.ls("/"));        // []

         */
    }
}

