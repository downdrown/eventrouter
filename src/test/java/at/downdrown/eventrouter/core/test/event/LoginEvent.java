package at.downdrown.eventrouter.core.test.event;

/** Just a Demo-Dummy */
public class LoginEvent {

    private String userId;

    public LoginEvent() {
        this(null);
    }

    public LoginEvent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
