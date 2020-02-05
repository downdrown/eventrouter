package at.downdrown.eventrouter.core;

/**
 * A registry abject from which you can unregister an already added callback.
 *
 * @author downdrown
 * @since 1.0.0
 */
public interface Registration {

    /**
     * Remove the callback from the {@link EventRouter}.
     */
    void unregister();

}
