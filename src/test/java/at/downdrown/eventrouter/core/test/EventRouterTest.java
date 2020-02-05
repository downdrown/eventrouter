package at.downdrown.eventrouter.core.test;


import at.downdrown.eventrouter.core.EventRouter;
import at.downdrown.eventrouter.core.Registration;
import at.downdrown.eventrouter.core.test.event.ButtonClickEvent;
import at.downdrown.eventrouter.core.test.event.LoginEvent;
import at.downdrown.eventrouter.core.test.event.MenuButtonClickEvent;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class EventRouterTest {

    @Test
    public void testCallbackRegistration() {

        // For evaluation
        final AtomicInteger callCount = new AtomicInteger();

        // given - user adds a single callback for the ButtonCLickEvent
        EventRouter.on(ButtonClickEvent.class, event -> callCount.getAndIncrement());

        // when - user triggers a new ButtonClickEvent
        EventRouter.trigger(new ButtonClickEvent());

        // then - we assume the callback has been triggered 1 times
        Assert.assertEquals(1, callCount.get());

    }

    @Test
    public void testCallbackUnregistration() {

        // For evaluation
        final AtomicInteger callCount = new AtomicInteger(0);

        // given - user adds a single callback for the ButtonCLickEvent
        Registration buttonClickRegistry = EventRouter.on(ButtonClickEvent.class, event -> callCount.getAndIncrement());

        // when - user triggers a new ButtonClickEvent
        EventRouter.trigger(new ButtonClickEvent());

        // then - we assume the callback has been triggered 1 times
        Assert.assertEquals(1, callCount.get());

        // given - user removes the prior added callback
        buttonClickRegistry.unregister();

        // when - user triggers a new ButtonClickEvent
        EventRouter.trigger(new ButtonClickEvent());

        // then - we still assume the callback counter to be 1
        Assert.assertEquals(1, callCount.get());

    }

    @Test
    public void testMultipleEventTypes() {

        final AtomicInteger buttonClickCallCount = new AtomicInteger(0);
        final AtomicInteger loginCallCount = new AtomicInteger(0);

        // given
        EventRouter.on(ButtonClickEvent.class, event -> buttonClickCallCount.getAndIncrement());
        EventRouter.on(LoginEvent.class, event -> loginCallCount.getAndIncrement());

        // when
        EventRouter.trigger(new ButtonClickEvent());
        EventRouter.trigger(new ButtonClickEvent());
        EventRouter.trigger(new ButtonClickEvent());
        EventRouter.trigger(new ButtonClickEvent());
        EventRouter.trigger(new ButtonClickEvent());
        EventRouter.trigger(new ButtonClickEvent());

        EventRouter.trigger(new LoginEvent());
        EventRouter.trigger(new LoginEvent());
        EventRouter.trigger(new LoginEvent());
        EventRouter.trigger(new LoginEvent());

        // then
        Assert.assertEquals(6, buttonClickCallCount.get());
        Assert.assertEquals(4, loginCallCount.get());

    }

    @Test
    public void testEventParameters() {

        // given & then
        EventRouter.on(LoginEvent.class, Assert::assertNotNull);
        EventRouter.on(LoginEvent.class, event -> Assert.assertEquals("sarah", event.getUserId()));

        // when
        EventRouter.trigger(new LoginEvent("sarah"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParameter() {
        EventRouter.trigger(null);
    }

    @Test
    public void testInheritedSubEvent() {

        // for evaluation
        final AtomicInteger buttonClickCallCount = new AtomicInteger(0);
        final AtomicInteger menuButtonClickCallCount = new AtomicInteger(0);

        // given
        EventRouter.on(ButtonClickEvent.class, event -> buttonClickCallCount.getAndIncrement());
        EventRouter.on(MenuButtonClickEvent.class, event -> menuButtonClickCallCount.getAndIncrement());

        // when
        EventRouter.trigger(new ButtonClickEvent());

        // then
        Assert.assertEquals(1, buttonClickCallCount.get());
        Assert.assertEquals(0, menuButtonClickCallCount.get());

        // when
        EventRouter.trigger(new MenuButtonClickEvent());
        EventRouter.trigger(new MenuButtonClickEvent());

        // then
        Assert.assertEquals(1, buttonClickCallCount.get());
        Assert.assertEquals(2, menuButtonClickCallCount.get());

    }
}
