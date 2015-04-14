package base;

public interface Listenable {
    void addListener(Listener listener);

    void removeListener(Listener listener);
}
