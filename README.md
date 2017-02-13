# DaggerMock
A JUnit rule to easily override Dagger 2 objects

[![Build Status](https://travis-ci.org/fabioCollini/DaggerMock.svg?branch=master)](https://travis-ci.org/fabioCollini/DaggerMock)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-DaggerMock-green.svg?style=true)](https://android-arsenal.com/details/1/2987)
[![](https://jitpack.io/v/fabioCollini/DaggerMock.svg)](https://jitpack.io/#fabioCollini/DaggerMock)

More info about testing using Dagger 2 and Mockito are available in this
[Medium post](https://medium.com/@fabioCollini/android-testing-using-dagger-2-mockito-and-a-custom-junit-rule-c8487ed01b56).

Overriding an object managed by Dagger 2 is not easy, you need to define a TestModule and, if you want
to inject your test object, a TestComponent.

Using a `DaggerMockRule` it's possible to override easily the objects defined in a Dagger module:

```java
public class MainServiceTest {

    @Rule public DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    mainService = component.mainService();
                }
            });

    @Mock RestService restService;

    @Mock MyPrinter myPrinter;

    MainService mainService;

    @Test
    public void testDoSomething() {
        when(restService.getSomething()).thenReturn("abc");

        mainService.doSomething();

        verify(myPrinter).print("ABC");
    }
}
```

When `DaggerMockRule` rule is instantiated, it looks for all @Mock annotated fields in your test class
and it replaces them with Mockito mocks if there is a provider method in your module for that class.
Then it uses all the test fields to override the objects defined in the Dagger configuration.

In this example
[MyModule](https://github.com/fabioCollini/DaggerMock/blob/master/app/src/main/java/it/cosenonjaviste/daggermock/demo/MyModule.java)
contains two methods to provide `RestService` and `MyPrinter` objects. Behind the scenes, the
`DaggerMockRule` rule dynamically creates a new module that overrides `MyModule`, it returns the mocks
for `restService` and `myPrinter` defined in the test instead of the real objects, like this:

```java
public class TestModule extends MyModule {
    @Override public MyPrinter provideMyPrinter() {
        return Mockito.mock(MyPrinter.class);
    }

    @Override public RestService provideRestService() {
        return Mockito.mock(RestService.class);
    }
}
```

DaggerMock can't override Dagger objects that are defined using `Inject` annotation, since version 0.6
 you get a runtime error if the test contains a field of a class that is not defined in a module. 

## Espresso support

A `DaggerMockRule` can also be used in an Espresso test:

```java
public class MainActivityTest {

    @Rule public DaggerMockRule<MyComponent> daggerRule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    App app = (App) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
                    app.setComponent(component);
                }
            });

    @Rule public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Mock RestService restService;

    @Mock MyPrinter myPrinter;

    @Test
    public void testCreateActivity() {
        when(restService.getSomething()).thenReturn("abc");

        activityRule.launchActivity(null);

        verify(myPrinter).print("ABC");
    }
}
```

## Robolectric support

In a similar way a `DaggerMockRule` can be used in a Robolectric test:

```java
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityTest {

    @Rule public final DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule())
            .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
                @Override public void setComponent(MyComponent component) {
                    ((App) RuntimeEnvironment.application).setComponent(component);
                }
            });

    @Mock RestService restService;

    @Mock MyPrinter myPrinter;

    @Test
    public void testCreateActivity() {
        when(restService.getSomething()).thenReturn("abc");

        Robolectric.setupActivity(MainActivity.class);

        verify(myPrinter).print("ABC");
    }
}
```

## InjectFromComponent annotation

In the first example we have used a ComponentSetter subclass to retrieve an object from the component: 

```java
@Rule public DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule())
        .set(new DaggerMockRule.ComponentSetter<MyComponent>() {
            @Override public void setComponent(MyComponent component) {
                mainService = component.mainService();
            }
        });

MainService mainService;
```

Since DaggerMock 0.6 this code can be written in an easier way using `InjectFromComponent` annotation:

```java
public class MainServiceTest {

    @Rule public final DaggerMockRule<MyComponent> rule = new DaggerMockRule<>(MyComponent.class, new MyModule());

    @Mock RestService restService;

    @Mock MyPrinter myPrinter;

    @InjectFromComponent MainService mainService;

    @Test
    public void testDoSomething() {
        when(restService.getSomething()).thenReturn("abc");

        mainService.doSomething();

        verify(myPrinter).print("ABC");
    }
}
```

Many objects managed by Dagger are only injected in other objects and are not exposed in a component.
For example if the MainService object is injected in MainActivity we can use the following annotation:

```java
@InjectFromComponent(MainActivity.class) MainService mainService;
```

A MainActivity object is created using reflection, the inject method is invoked on this object and then
the mainService field is extracted and used to populate the test field. 

## Custom rules

It's easy to create a `DaggerMockRule` subclass to avoid copy and paste and simplify the test code:

```java
public class MyRule extends DaggerMockRule<MyComponent> {
    public MyRule() {
        super(MyComponent.class, new MyModule());
        set(new DaggerMockRule.ComponentSetter<MyComponent>() {
            @Override public void setComponent(MyComponent component) {
                App app = (App) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
                app.setComponent(component);
            }
        });
    }
}
```

The final test uses the rule subclass:

```java
public class MainActivityTest {

    @Rule public MyRule daggerRule = new MyRule();

    @Rule public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Mock RestService restService;

    @Mock MyPrinter myPrinter;

    @Test
    public void testCreateActivity() {
        when(restService.getSomething()).thenReturn("abc");

        activityRule.launchActivity(null);

        verify(myPrinter).print("ABC");
    }
}
```

## Dagger Subcomponents

Since version 0.6 Dagger subcomponents are supported by DaggerMock with a limitation:
subcomponent module must be passed as parameter in subcomponent creation method.
For example if the subcomponent is defined as follows:

```java
@Subcomponent(modules = MainActivityModule.class)
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);
}
```

The method in the main component that creates the subcomponent must be defined using a module parameter: 

```java
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    MainActivityComponent activityComponent(MainActivityModule module);
}
```

Subcomponent support doesn't work on Dagger 2.0, you need to use Dagger version 2.1+.
A complete example is available [here](https://github.com/fabioCollini/DaggerMock/tree/master/RealWorldApp).

## JitPack configuration

DaggerMock is available on [JitPack](https://jitpack.io/#fabioCollini/DaggerMock/),
add the JitPack repository in your build.gradle (in top level dir):
```gradle
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}
```
and the dependency in the build.gradle of the module:

```gradle
dependencies {
    testCompile 'com.github.fabioCollini:DaggerMock:0.6.4'
    //and/or
    androidTestCompile 'com.github.fabioCollini:DaggerMock:0.6.4'
}
```

## License

    Copyright 2016 Fabio Collini

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
