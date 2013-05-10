# AppdataPreferences Bootstrap

AppdataPreferences is a tiny sample Android application that demonstrates the features of the [AppdataPreferences for Android SDK](https://github.com/googledrive/appdatapreferences-android). The application has a single activity that is being rendered according to the values stored in a SharedPreferences backend. Values are synced with a remote file on Google Drive's Application Data folder. Authorization and configuration is 

## Setup
* Clone AppdataPreferences for Android SDK and import it into Eclipse as an Android Application project from existing source code.

      git clone https://github.com/googledrive/appdatapreferences-android

* Clone AppdataPreferences Bootstrap and import in into the Eclipse, similar to the library.

      git clone https://github.com/googledrive/appdatapreferences-android-bootstrap
      
* Go to [APIs console](https://code.google.com/apis/console) and create a project if you haven't already. On the "API Access" tab, create a new client ID for installed apps and select Android. Provide your package name and certificate finger print. You can extract SHA1 footprint by executing the following command:

      $ keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v

That's it. Launch the application on your device, give the permissions it's asking. For more fun, launch the sample application on another device and see how changes on a single device is being synced with the other.

## License

Copyright 2013 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
