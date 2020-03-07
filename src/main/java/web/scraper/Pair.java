package web.scraper;

public class Pair<K, V> {
    private K head;
    private V tail;

    public Pair(K head, V tail) {
        this.head = head;
        this.tail = tail;
    }

    public K head() {
        return this.head;
    }

    public V tail() {
        return this.tail;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", head.toString(), tail.toString());
    }
}