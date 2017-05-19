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