package at.downdrown.eventrouter.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is the <b>primary</b> interface of this util.
 * It takes care of all registered {@link Callback}s and also triggers them.
 * It is basically designed to be {@code thread safe}, but please consider your project setup when using it.
 *
 * WARNING
 * There is no guarantee that the registered {@link Callback}s get invoked in the same order they were added.
 * So keep this in mind while setting up your architecture - events and their subscribed {@link Callback}s
 * do <b>not</b> have a specified calling order!
 *
 * The {@link EventRouter} uses SLF4J to log out some debugging informations, if you want to see them in your
 * log just enable the {@code debug} logging level in your logger configuration.
 *
 * @author downdrown
 * @since 1.0.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class EventRouter {

    private static final Logger logger = LoggerFactory.getLogger(EventRouter.class);

    // Holds registered callbacks to their corresponding event types
    private final Map<Class<?>, List<Callback>> stack = Collections.synchronizedMap(new HashMap<>());

    public <T> Registration on(Class<T> eventClass, Callback<T> callback) {

        // Prepare entry for stack
        final String callbackId = asId(eventClass);

        // Register
        logger.debug("Registering Callback #{}", callbackId);
        stack.putIfAbsent(eventClass, Collections.synchronizedList(new ArrayList<>()));
        stack.get(eventClass).add(callback);

        // Unregister
        return () -> {
            logger.debug("Unregistering Callback #{}", callbackId);
            stack.get(eventClass).remove(callback);
            if (stack.get(eventClass) == null || stack.get(eventClass).isEmpty()) {
                stack.remove(eventClass);
            }
        };
    }

    /**
     * Triggers all registered {@link Callback} elements whoms registered event class is
     * equal to the class of the {@code event} parameter.
     *
     * @param event the event you want to trigger.
     */
    public void trigger(Object event) {
        if (event == null) {
            throw new IllegalArgumentException("event may not be null.");
        }
        stack.getOrDefault(event.getClass(), Collections.emptyList())
                .forEach(callback -> callback.trigger(event));
    }

    /**
     * Generates a unique ID for callback registration.
     *
     * @param eventClass the event class.
     * @return a unique {@link UUID} prefixed with the {@code eventClass}'s 'simple name'.
     */
    private static String asId(Class<?> eventClass) {
        return String.join(
            "-",
            eventClass.getSimpleName(),
            UUID.randomUUID().toString());
    }
}
