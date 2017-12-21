#### 0.8.3
 - Added `customizeBuilder` to `DaggerMock` Kotlin class to wrap Java method
 - Fix to throw an exception only when constructor is annotated with `@inject` and a module methods doesn't exist 

#### 0.8.2
 - Fixed [Robolectric issue](https://github.com/fabioCollini/DaggerMock/issues/55) when used with Dagger-Android

#### 0.8.1
 - Added [Dagger-Android](https://google.github.io/dagger/android.html) support in Espresso tests
 - New Kotlin module to simplify rule definition

#### 0.7.0
 - Improved Kotlin support (thanks [lenguyenthanh](https://github.com/lenguyenthanh) for the pull request)
 - New Kotlin examples using [mock-maker](http://hadihariri.com/2016/10/04/Mocking-Kotlin-With-Mockito/)/
 [dexopener](https://github.com/tmurakami/dexopener) and
 [kotlin-allopen](https://kotlinlang.org/docs/reference/compiler-plugins.html#all-open-compiler-plugin)

#### 0.6.6
 - Added check to show clear error message when trying to mock static @Provides methods
 (thanks [mikovali](https://github.com/mikovali))

#### 0.6.5
 - Support for @Subcomponent.Builder
 - Support for inheritance in test classes with InjectFromComponent annotation
 - Support for lazy fields in InjectFromComponent annotation

#### 0.6.4
 - Fix to ignore static fields of test classes
 - Removed fallback on not annotated fields to avoid problems with Named annotation
 - Added check to throw exceptions on module final methods
 - Exposed init method to not depend on Test annotation to use DaggerMock with frameworks like Cucumber
 (thanks [danielocampo2](https://github.com/danielocampo2))
 - Support for inheritance in test classes

#### 0.6.3
 - Qualifier annotations support
 - Added sources jar in distribution

#### 0.6.2
- Improved nested dependent component support (thanks [ChrisZou](https://github.com/ChrisZou) for the report)
- Support for Provider fields in InjectFromComponent definition

#### 0.6.1
- Improved dependent component support (thanks [plastiv](https://github.com/plastiv) for the report)
- Improved errors management on InjectFromComponent annotation processing

#### 0.6
- SubComponent support
- InjectFromComponent annotation

#### 0.5
- Added support for component dependencies (thanks [jvanderwee](https://github.com/jvanderwee)!)