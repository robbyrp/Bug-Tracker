package notification;

public interface Subscriber {
    /**
     * Update method called by users and devs
     * @param notification
     */
    void update(String notification);
}
