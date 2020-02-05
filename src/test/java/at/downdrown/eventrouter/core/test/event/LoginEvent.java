package at.downdrown.eventrouter.core.test.event;

import at.downdrown.eventrouter.core.Event;

/** Just a Demo-Dummy */
public class LoginEvent extends Event {

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
