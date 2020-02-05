[![Build Status](https://travis-ci.com/downdrown/eventrouter.svg?branch=master)](https://travis-ci.com/downdrown/eventrouter)

# EventRouter
Tiny, lightweight eventing engine.

## What?
EventRouter is designed to be a lightweight "Event-Bus-Like" construct that provides a simple and user-friendly api to 
simplify event-driven architectures. It was designed to work with small to medium size projects, altough it might work
well with larger projects too .

## Why?
Simple. It is really really easy to use:

```java
final EventRouter eventRouter = new EventRouter();
eventrouter.on(MyEventType.class, event -> doSomething());
eventRouter.trigger(new MyEventType());
```

done.


The above code sample adds a `Callback<MyEventType>` to the `eventRouter` and triggers it afterwards. Since you can add
`Callback`s and trigger `Event`s from anywhere in your codebase it makes it pretty easy to couple several components or
even modules with this mechanism.

## How?
You simply instantiate a `EventRouter` and keep this instance for as long as the bus should be alive. The  method 
`EventRouter#on` returns you a `Registration` which lets you unregister the `Callback` easily using the 
`Registration#unregister` method.

If you have a closer look at the `EventRouter#on` method, you'll see that the first parameter is the type, or to be more
specific, the class of the event you want to observe. The second parameter is the `Callback` which get's triggered when
an event of the specified type gets triggerd - typically a lambda expression which represents the passed `Event` instance.

## Samples
If you are looking for some code samples have a look on the `EventRouterTest` Unit-Test.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Versioning

I use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/downdrown/eventrouter/tags). 

## Authors

* **Manfred Huber** - *Initial work* - [downdrown](https://github.com/downdrown)