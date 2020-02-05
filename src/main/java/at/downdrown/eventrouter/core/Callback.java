package at.downdrown.eventrouter.core;

/**
 * Callback that will be triggered when an event of the Callback's type {@code T} gets triggered.
 *
 * @param <T> the generic type of the event this callback handles.
 * @author downdrown
 * @since 1.0.0
 */
@FunctionalInterface
public interface Callback<T extends Event> {

    /**
     * Trigger this callback.
     * @param event the payload, defined as subtype of {@link Event}.
     */
    void trigger(T event);

}
