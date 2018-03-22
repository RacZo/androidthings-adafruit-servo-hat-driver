# Adafruit 16 Channel Servo Hat

[![Download](https://api.bintray.com/packages/raczo/maven/androidthings-adafruit-servo-hat-driver/images/download.svg?version=0.0.5) ](https://bintray.com/raczo/maven/androidthings-adafruit-servo-hat-driver/0.0.5/link) [![Release](https://img.shields.io/badge/maven--central-v0.0.5-green.svg?style=flat-square)](http://mvnrepository.com/artifact/com.oscarsalguero/androidthings-adafruit-servo-hat-driver/0.0.5)

Gradle library. 

Android Things Driver for the Adafruit 16 Channel Servo Hat (https://www.adafruit.com/product/2327).


## Dependencies

This library depends on Antonio Zugaldia's AdafruitPwm class (I've have included it in this project as a convenience; in the future ideally, it will come out from the Android Robocar's project as an independent gradle library).

AdafruitPwm has the following dependencies:

- Jake Wharton's Timber 4.5.1 https://github.com/JakeWharton/timber

```groovy
    compile "com.jakewharton.timber:timber:4.5.1"
```

Both Antonio's AdafruitPwm driver class and this 16 Channel Servo Hat driver have the following dependencies:

- Android Things Developer Preview 0.7 https://developer.android.com/things/sdk/index.html

If not, add the following to your app's build.gradle:

```groovy
    implementation "com.google.android.things:androidthings:0.7-devpreview"
```


## Installation

Via Gradle, by putting the following in your build.gradle:

```groovy
    compile "com.oscarsalguero:androidthings-adafruit-servo-hat-driver:0.0.5"
```

Or Maven:

```xml
<dependency>
  <groupId>com.oscarsalguero</groupId>
  <artifactId>androidthings-adafruit-servo-hat-driver</artifactId>
  <version>0.0.5</version>
  <type>pom</type>
</dependency>
```

## Usage

1. Find out the name of your I2C bus (usually called "I2C") and the I2C address of your servo hat (in my case it was 0x41, but depends how you configure it) and declare it as a constant:

    ```java
    // I2C Bus Name
    public static final String I2C_DEVICE_NAME = "I2C1";
    // Adafruit Servo Hat
    private static final int SERVO_HAT_I2C_ADDRESS = 0x41;
    ```

2. Declare your servos and preferably name them after the piece of hardware they actually control:

    ```java
    private AdafruitServo mPanServo;
    private AdafruitServo mTiltServo;
    ```
    
   In this case, the servos control a pan/tilt bracket used to move a camera left/right and up/down, so the first one is for tilting and the second one is for panning.

3. Configure your servos following their specifications. Usually, the manufacturer would provide this info, if not... well, you will have to "google it".

    ```java
    // Initialize your servo hat with the I2C bus name and the hat's I2C address
    mServoHat = new AdafruitServoHat(I2C_DEVICE_NAME, SERVO_HAT_I2C_ADDRESS);

    // Configuring servos according to their specs
    double minAngle = 20.0;
    double maxAngle = 150.0; // Specs at https://www.sparkfun.com/products/9065 says it is ~160, but I'm setting it to less to prevent servo reset
    double minPulseDurationRange = 0.7; // ms
    double maxPulseDurationRange = 1.50; // ms
    int frequency = 40;

    // Add the servos in the order they are connected from index 0 to index 15 (16 max)
    mPanServo = new AdafruitServo(0, frequency);
    mPanServo.setPulseDurationRange(minPulseDurationRange, maxPulseDurationRange);
    mPanServo.setAngleRange(minAngle, maxAngle);
    mPanServo.setAngle(90);
    mServoHat.addServo(mPanServo);

    mTiltServo = new AdafruitServo(1, frequency);
    mTiltServo.setPulseDurationRange(minPulseDurationRange, maxPulseDurationRange);
    mTiltServo.setAngleRange(minAngle, maxAngle);
    mTiltServo.setAngle(90);
    mServoHat.addServo(mTiltServo);

    // You can now rotate your servos to an agle
    mServoHat.rotateToAngle(mPanServo, 90);
    mServoHat.rotateToAngle(mTiltServo, 90);
    ```
    
At this point you should be all set!

### Contributing

1. Fork this repo and clone your fork. Create an empty file called "bintray.gradle" in the root of your project.
2. Make your desired changes
3. Add tests for your new feature and ensure all tests are passing
4. Commit and push
5. Submit a Pull Request through Github's interface and I'll review your changes to see if they make it to the next release.


### Issues

Use this repo's github issues.


### Inspired By

Antonio Zugaldia's Adafruit DC Motor Hat driver in the Android Robocar project.


License
=======

    Copyright 2018 Oscar Salguero

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.