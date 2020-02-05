package at.downdrown.eventrouter.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class is the <b>primary</b> interface of this util.
 * It takes care of all added {@link Callback}s and also triggers them.
 * It is basically designed to be {@code thread safe}, but please consider your project setup when using it.
 *
 * WARNING
 * There is no guarantee that the registered {@link Callback}s get invoked in the same order they were added.
 * So keep this in mind while setting up your architecture - {@link Event}s and their subscribed {@link Callback}
 * do not have a specified calling order!
 *
 * The {@link EventRouter} uses SLF4J to log out some debugging informations, if you want to see them in your
 * log just enable the {@code debug} logging level in your logger configuration.
 *
 * @author downdrown
 * @since 1.0.0
 */
public final class EventRouter {

    private static final Logger logger = LoggerFactory.getLogger(EventRouter.class);
    private static final Set<CallbackRegistry<? extends Event>> registrations = new HashSet<>();

    public static synchronized <T extends Event> Registration on(Class<T> eventClass, Callback<T> callback) {

        // Prepare entry for stack
        final CallbackRegistry<T> callbackRegistry = new CallbackRegistry<>(eventClass, asId(eventClass), callback);

        // Register
        logger.debug("Registering CallbackRegistry #{}", callbackRegistry.getCallbackId());
        registrations.add(callbackRegistry);

        // Unregister
        return () -> registrations.remove(callbackRegistry);
    }

    /**
     * Triggers all registered {@link CallbackRegistry} elements whoms {@link CallbackRegistry#eventClass} is
     * equal to the class of the {@code event} parameter.
     *
     * @param <T> the generic type of the event you want to trigger.
     * @param event the event you want to trigger.
     */
    public static synchronized <T extends Event> void trigger(T event) {
        if (event == null) {
            throw new IllegalArgumentException("event may not be null.");
        }
        fromStack(event)
            .forEach(callbackRegistry -> {
                logger.debug("Triggering Callback for EventRegistry #{}", callbackRegistry.getCallbackId());
                callbackRegistry.getEventCallback().trigger(event);
            });
    }

    /**
     * Generates a unique ID for callback registration.
     *
     * @param <T> the generic selector for {@link Event} and subclasses.
     * @param eventClass the actual class.
     * @return a unique {@link UUID} prefixed with the {@code eventClass}'s 'simple name'.
     */
    private static <T extends Event> String asId(Class<T> eventClass) {
        return String.join(
            "-",
            eventClass.getSimpleName(),
            UUID.randomUUID().toString());
    }

    /**
     * Queries the {@link EventRouter#registrations} for matching registrations.
     *
     * @param <T> the generic selector for {@link Event} and subclasses.
     * @param event the actual class.
     * @return a {@link Set} with all contemplable {@link CallbackRegistry} entries.
     */
    private static <T extends Event> Set<CallbackRegistry<T>> fromStack(T event) {
        return registrations.stream()
            .filter(item -> item.getEventClass().equals(event.getClass()))
            .map(item -> (CallbackRegistry<T>) item)
            .collect(Collectors.toSet());
    }

    /**
     * Represents a {@link Callback} that has been added to the {@code registry}.
     * @param <T> the generic selector for {@link Event} and subclasses.
     */
    private static final class CallbackRegistry<T extends Event> {

        private final Class<T> eventClass;
        private final String callbackId;
        private final Callback<T> eventCallback;

        public CallbackRegistry(Class<T> eventClass, String callbackId, Callback<T> eventCallback) {
            this.eventClass = eventClass;
            this.callbackId = callbackId;
            this.eventCallback = eventCallback;
        }

        public Class<T> getEventClass() {
            return eventClass;
        }

        public String getCallbackId() {
            return callbackId;
        }

        public Callback<T> getEventCallback() {
            return eventCallback;
        }
    }
}
