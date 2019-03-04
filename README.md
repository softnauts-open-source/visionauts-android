# Visionauts

### This is a Visionauts Android Application Repository

Visionauts is an open source project created by Softnauts (https://www.softnauts.com/) to help visually impaired users. The project includes a mobile application for Android and iOS and a web application - CMS for managing beacons and their content.

The user, when moving around, receives information about his location based on beacons in his vicinity. The content of beacons must be configured in CMS. Every beacon must have an assigned information in CMS that will be read to the user by mobile application when he is near beacon. The application recognizes a specific beacon based on its uuid, minor and major.

The application should work with all beacons supporting the iBeacon standard, but use of kontakt.io (https://kontakt.io/) beacons is recommended. Visionauts uses kontakt.io SDK but you can easily replace it with different one.

![ScreenShot](https://raw.github.com/softnauts-open-source/visionauts-android/master/screenshots/sc1.png)
![ScreenShot](https://raw.github.com/softnauts-open-source/visionauts-android/master/screenshots/sc2.png)
![ScreenShot](https://raw.github.com/softnauts-open-source/visionauts-android/master/screenshots/sc3.png)

### I. How to use it

##### 1. Open project in Android Studio

##### 2. Setup your CMS API url:
in build.gradle set API_URL for both staging and production configutarions.
```
 productFlavors {
        staging {
            dimension "server"
            buildConfigField "String", "API_URL", '"http://yourStagingApiUrl.com/"'
            applicationIdSuffix ".staging"
        }

        production {
            dimension "server"
            buildConfigField "String", "API_URL", '"http://yourProductionApiUrl.com/"'
        }
    }
```
##### 3. Setup Kontakt.io API_KEY in VisionautsApp.kt if using Kontakt.io beacons
```kotlin
 private fun initializeBeaconsSDK() {
        KontaktSDK.initialize("YOUR_API_KEY") // paste your API KEY here, if using Kontakt.io
    }
```
##### 4. Add your custom text for "help" screen in strings.xml .
##### 5. Run project

### II. Using your own beacons SDK

1. Add sdk dependencies in build.gradle
2. Initialize SDK in VisionautsApp.kt in initializeBeaconsSDK() method.
3. Create custom service that extends BeaconScanService.kt
4. Add yours service ContributesAndroidInjector in AndroidBindingModule.kt
5. Setup your service intent in MainActivity checkPermissionsAndStartBackgroundService() method.

### III. BeaconScanService
If you are use your own SDK and follow instructions above, in your service that extends BeaconScanService.kt:

1. When your sdk detect new beacon nearby call onDeviceDiscovered(uuid: String, minor: String, major: String) method from BeaconScanService.kt
2. When your sdk lost beacon call onDeviceLost(uuid: String, minor: String, major: String) method from BeaconScanService.kt

### IV. Tech

Visionauts uses a number of libraries to work properly:

* [Dagger] - A fast dependency injector for Android and Java.
* [Retrofit] - Type-safe HTTP client for Android and Java by Square, Inc.
* [RxJava] - Reactive Extensions for the JVM
* [RxAndroid] - Reactive Extensions for Android
* [Kontakt.io] - Kontakt.io Android SDK
* [Timber] - Logger
* [LeakCanary] - A memory leak detection library for Android and Java
* [EventBus] - EventBus is a publish/subscribe event bus for Android and Java.

### V. Authors

[![N|Solid](https://www.softnauts.com/assets/images/homepage/softnauts_logo_vertical.svg?v7)](https://www.softnauts.com/)

----

### VI. License

The MIT License

Copyright 2019 Softnauts

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

----
**Free Software, Hell Yeah!**

   [Dagger]: <https://github.com/google/dagger>
   [RxAndroid]: <https://github.com/ReactiveX/RxAndroid>
   [Retrofit]: <https://github.com/square/retrofit>
   [RxJava]: <https://github.com/ReactiveX/RxJava>
   [Kontakt.io]: <https://github.com/kontaktio/kontakt-android-sdk>
   [Timber]: <https://github.com/JakeWharton/timber>
   [LeakCanary]: <https://github.com/square/leakcanary>
   [EventBus]: <https://github.com/greenrobot/EventBus>
