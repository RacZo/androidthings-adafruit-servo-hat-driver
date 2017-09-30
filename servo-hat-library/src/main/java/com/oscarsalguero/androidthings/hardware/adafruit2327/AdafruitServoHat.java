package com.oscarsalguero.androidthings.hardware.adafruit2327;

import android.util.Log;

import com.zugaldia.robocar.hardware.adafruit2348.AdafruitPwm;

import java.util.ArrayList;
import java.util.List;

/**
 * Port of Adafruit 16-Channel Servo Hat for Raspberry Pi to Android Things.
 * See https://learn.adafruit.com/adafruit-16-channel-pwm-servo-hat-for-raspberry-pi/overview
 * and https://github.com/adafruit/Adafruit_Python_PCA9685/blob/master/examples/simpletest.py
 * <p>
 * Created by Oscar Salguero on 6/25/17.
 */
public class AdafruitServoHat {

    private static final String LOG_TAG = AdafruitServoHat.class.getSimpleName();
    private static final int DEFAULT_SERVO_FREQUENCY = 40;
    private boolean DEBUG = false;
    private AdafruitPwm mPwm;
    private List<AdafruitServo> servos;

    /**
     * Initializes an empty Servo Hat connected to an I2C bus and an i2C slave device
     *
     * @param i2cAddress an integer with the I2C slave device address
     */
    public AdafruitServoHat(String i2cName, int i2cAddress) {

        mPwm = new AdafruitPwm(i2cName, i2cAddress);
        mPwm.setPwmFreq(DEFAULT_SERVO_FREQUENCY);

        servos = new ArrayList<>();
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
        servos.add(null);
    }

    /**
     * Adds servos to the desired index
     *
     * @param servo an {@link AdafruitServo} object representing the servo we want to add to the hat
     */
    public void addServo(AdafruitServo servo) {
        int index = servo.getIndex();
        if ((index < 0) || (index > 15)) {
            throw new RuntimeException("Servo index must be between 0 and 15 inclusive");
        }
        servos.add(servo);
    }

    /**
     * Rotates a servo to the desired angle
     *
     * @param servo the servo you want to rotate
     * @param angle the desired angle
     */
    public void rotateToAngle(AdafruitServo servo, int angle) throws IllegalArgumentException {
        servo.setAngle(angle);
        if (DEBUG) {
            Log.d(LOG_TAG, "Moving servo on index " + servo.getIndex() +
                    " to angle " + servo.getAngle() +
                    " with a frequency of " + servo.getFrequencyHz() + " Hz," +
                    " and a pulse of " + servo.getPulse() + " ms");
        }
        run(servo);
    }

    /**
     * Moves the servo sending the duty cycle via PWM
     *
     * @param servo the servo you want to move
     */
    public void run(AdafruitServo servo) {
        if (mPwm == null) {
            return;
        }
        mPwm.setPwmFreq(servo.getFrequencyHz());
        if (DEBUG) {
            Log.d(LOG_TAG, "Duty cycle: " + servo.getDutyCycle());
        }
        mPwm.setPwm(servo.getIndex(), 0, servo.getDutyCycle());
    }

    /**
     * For closing the PWM connection and free resources
     */
    public void close() {
        mPwm.close();
    }

}
