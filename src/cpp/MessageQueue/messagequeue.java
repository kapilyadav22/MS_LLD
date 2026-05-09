import java.util.*;
import java.util.concurrent.locks.*;

/************** Message *****************/
class Message {
    int id;
    String payload;

    public Message(int id, String payload) {
        this.id = id;
        this.payload = payload;
    }
}

/************** Topic *****************/
class Topic {
    private String name;
    private List<Message> messages;
    private Lock lock;

    public Topic(String name) {
        this.name = name;
        this.messages = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    public void publish(Message msg) {
        lock.lock();
        try {
            messages.add(msg);
        } finally {
            lock.unlock();
        }
    }

    public Message getMessage(int offset) {
        lock.lock();
        try {
            if (offset < messages.size()) {
                return messages.get(offset);
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return messages.size();
        } finally {
            lock.unlock();
        }
    }

    public String getName() {
        return name;
    }
}

/************** Consumer *****************/
class Consumer {
    private String name;
    private Map<String, Integer> offsets;

    public Consumer(String name) {
        this.name = name;
        this.offsets = new HashMap<>();
    }

    public void consume(Topic topic) {
        String tname = topic.getName();

        offsets.putIfAbsent(tname, 0);

        int offset = offsets.get(tname);
        Message msg = topic.getMessage(offset);

        if (msg != null) {
            System.out.println(name + " consumed from " + tname + ": " + msg.payload);
            offsets.put(tname, offset + 1);
        } else {
            System.out.println(name + " no new messages in " + tname);
        }
    }
}

/************** Producer *****************/
class Producer {
    private String name;

    public Producer(String name) {
        this.name = name;
    }

    public void publish(Topic topic, int id, String payload) {
        Message msg = new Message(id, payload);
        topic.publish(msg);

        System.out.println(name + " published to " + topic.getName() + ": " + payload);
    }
}

/************** MessageQueue *****************/
class MessageQueue {
    private Map<String, Topic> topics;

    public MessageQueue() {
        topics = new HashMap<>();
    }

    public Topic createTopic(String name) {
        topics.putIfAbsent(name, new Topic(name));
        return topics.get(name);
    }

    public Topic getTopic(String name) {
        return topics.get(name);
    }
}

/************** MAIN *****************/

public class Main {
    public static void main(String[] args) {

        MessageQueue mq = new MessageQueue();

        // Create Topic
        Topic orders = mq.createTopic("orders");

        // Create Producers
        Producer p1 = new Producer("p1");

        // Create Consumers
        Consumer c1 = new Consumer("c1");
        Consumer c2 = new Consumer("c2");

        // Publish messages
        p1.publish(orders, 1, "orderA");
        p1.publish(orders, 2, "orderB");

        System.out.println();

        // Consumer 1 consumes
        c1.consume(orders);
        c1.consume(orders);
        c1.consume(orders); // no new message

        System.out.println();

        // Consumer 2 consumes (independent offset)
        c2.consume(orders);
        c2.consume(orders);
    }
}

/*
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        MessageQueue mq = new MessageQueue();
        Map<String, Producer> producers = new HashMap<>();
        Map<String, Consumer> consumers = new HashMap<>();

        int q = sc.nextInt();

        while (q-- > 0) {
            String cmd = sc.next();

            if (cmd.equals("CREATE_TOPIC")) {
                String topic = sc.next();
                mq.createTopic(topic);
                System.out.println("Topic created: " + topic);
            }

            else if (cmd.equals("CREATE_PRODUCER")) {
                String name = sc.next();
                producers.put(name, new Producer(name));
                System.out.println("Producer created: " + name);
            }

            else if (cmd.equals("CREATE_CONSUMER")) {
                String name = sc.next();
                consumers.put(name, new Consumer(name));
                System.out.println("Consumer created: " + name);
            }

            else if (cmd.equals("PUBLISH")) {
                String pname = sc.next();
                String topic = sc.next();
                int id = sc.nextInt();
                String payload = sc.next();

                Topic t = mq.getTopic(topic);
                if (t != null && producers.containsKey(pname)) {
                    producers.get(pname).publish(t, id, payload);
                }
            }

            else if (cmd.equals("CONSUME")) {
                String cname = sc.next();
                String topic = sc.next();

                Topic t = mq.getTopic(topic);
                if (t != null && consumers.containsKey(cname)) {
                    consumers.get(cname).consume(t);
                }
            }
        }

        sc.close();
    }
}

 */