# SpaceXDemoApp
A demo app using public apis to pool information about space x.

- Supported Android API versions: 21 and above
- Programming language: Kotlin
- Supported device types: Phone only

## Architecture
Using MVVM design pattern, great for its modularity and code separation and recommended by Google and has direct support in the platform.
For asynchronous service calls I kotlin coroutines to make api calls through Retrofit on the model side.
Used LiveData for observing the viewmodels on the view side making it lifecycle aware of these components

## Dependency Injection
Dependency injection to reduce coupling and simplify testing.
For dependency injection I used Koin. A service locator framework that is much simpler and does not require
code generation and complex set up like dagger. Dagger-Hilt is another new option that I would consider.

## Project Structure
Used android clean architecture approach separating it UI, Domain and data layer.

## Testing
Wrote some tests for my VieModel and Repository classes.

## Build And Run
The easiest way to build/run application is to open project folder in Android Studio or Intelij with appropriate plug ins
Run the App module on a configured emulator.

## Testing
To run tests go to appropriate test folders and run tests
I used the standard Mockito framework. Also 'com.nhaarman.mockitokotlin2:mockito' a framework that provides useful
helper functions for working with Mockito in Kotlin

## Assumptions
- Falcon 9 is a rocket name associated to different launches. In order to search for launches associated to this rocket
 I need to query its rocket ID from a separate API call then use that ID to filter through the relevant launches.