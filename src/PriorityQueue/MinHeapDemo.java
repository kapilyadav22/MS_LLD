package PriorityQueue;

/*
Create a Min Heap using PriorityQueue
Insert elements (add, offer)
Peek minimum element (peek, element)
Remove elements (poll, remove)
Check size & emptiness
Search elements (contains)
Iterate over heap
Convert to array
Clear heap
Custom comparator (for completeness)


| Method       | If insertion succeeds | If insertion fails |
| ------------ | --------------------- | ------------------ |
| `add(E e)`   | returns `true`        | ❌ throws exception |
| `offer(E e)` | returns `true`        | ❌ returns `false`  |


offer()
add()
remove()
clear()
size()
isEmpty()
removeIf();   boolean removeIf(Predicate<? super E> filter)
minHeap.peek();
minHeap.poll();

 */



import java.util.PriorityQueue;
import java.util.Iterator;
import java.util.Arrays;

public class MinHeapDemo {

    public static void main(String[] args) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        minHeap.add(10);
        minHeap.add(5);
        minHeap.add(30);
        minHeap.offer(2);
        minHeap.offer(8);

        // Peek minimum element
        System.out.println("Peek (min element): " + minHeap.peek());

        // Remove elements
        System.out.println("Poll (remove min): " + minHeap.poll());
        System.out.println("After poll: " + minHeap);

        // remove() without argument removes min
        System.out.println("Remove(): " + minHeap.remove());
        System.out.println("After remove(): " + minHeap);

        //Remove specific element
        minHeap.remove(10);
        System.out.println("After removing 10: " + minHeap);

        // Size and empty check
        System.out.println("Size: " + minHeap.size());
        System.out.println("Is Empty: " + minHeap.isEmpty());

        // Search element
        System.out.println("Contains 8: " + minHeap.contains(8));

        minHeap.removeIf(x -> x > 10);

        // Iterate over heap
        System.out.print("Iterating heap: ");
        Iterator<Integer> iterator = minHeap.iterator();

        while(iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();

        //  Convert to array
        Object[] array = minHeap.toArray();
        System.out.println("Heap as array: " + Arrays.toString(array));

//        minHeap.clear();
        System.out.println("After clear, is empty: " + minHeap.isEmpty());



    }
}



/*

        // element() throws exception if empty
        System.out.println("Element(): " + minHeap.element());


 */