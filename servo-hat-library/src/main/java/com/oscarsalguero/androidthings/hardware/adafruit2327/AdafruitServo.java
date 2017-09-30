package com.oscarsalguero.androidthings.hardware.adafruit2327;

import android.util.Log;

/**
 * A class representing a servo connected to Adafruit's Servo Hat.
 * Partially based on https://github.com/androidthings/contrib-drivers/blob/master/pwmservo/src/main/java/com/google/android/things/contrib/driver/pwmservo/Servo.java
 * <p>
 * Created by Oscar Salguero on 6/25/17.
 */
public class AdafruitServo {

    private static final String LOG_TAG = AdafruitServo.class.getSimpleName();

    private static final float DEFAULT_MIN_PULSE_DURATION_MS = 1;
    private static final float DEFAULT_MAX_PULSE_DURATION_MS = 2;
    private static final float DEFAULT_MIN_ANGLE_DEG = 0;
    private static final float DEFAULT_MAX_ANGLE_DEG = 180;

    private double mMinPulseDuration = DEFAULT_MIN_PULSE_DURATION_MS; // milliseconds
    private double mMaxPulseDuration = DEFAULT_MAX_PULSE_DURATION_MS; // milliseconds
    private double mMinAngle = DEFAULT_MIN_ANGLE_DEG; // degrees
    private double mMaxAngle = DEFAULT_MAX_ANGLE_DEG; // degrees

    private long mPulseLength; // milliseconds
    private double mAngle = mMinAngle;

    private int mIndex;
    private int mFrequencyHz;
    private double mPulse;
    private int mDutyCycle;

    private boolean DEBUG = false;

    public AdafruitServo(int index, int frequencyHz) {
        this.mIndex = index;
        this.mFrequencyHz = frequencyHz;
        this.mPulseLength = (1000000 / this.mFrequencyHz) / 4096; // Per Adafruit PWM servo docs: 1,000,000 us per second, 12 bits of resolution
        if (DEBUG) {
            Log.d(LOG_TAG, "Pulse length: " + this.mPulseLength);
        }
        setPulse();
    }

    /**
     * Set the pulse duration range. These determine the duty cycle range, where {@code minMs}
     * corresponds to the minimum angle value and {@code maxMs} corresponds to the maximum angle
     * value. If the servo is enabled, it will update its duty cycle immediately.
     *
     * @param minMs the minimum pulse duration in milliseconds
     * @param maxMs the maximum pulse duration in milliseconds
     */
    public void setPulseDurationRange(double minMs, double maxMs) {
        if (minMs >= maxMs) {
            throw new IllegalArgumentException("Minimum pulse duration must be less than maximum pulse duration");
        }
        if (minMs < 0) {
            throw new IllegalArgumentException("Minimum pulse duration must be greater than zero");
        }
        this.mMinPulseDuration = minMs;
        this.mMaxPulseDuration = maxMs;
        setPulse();
    }

    /**
     * Set the range of angle values the servo accepts. If the servo is enabled and its current
     * position is outside this range, it will update its position to the new minimum or maximum,
     * whichever is closest.
     *
     * @param minAngle the minimum angle in degrees
     * @param maxAngle the maximum angle in degrees
     */
    public void setAngleRange(double minAngle, double maxAngle) {
        if (minAngle >= maxAngle) {
            throw new IllegalArgumentException("The minimum angle must be less than maximum angle");
        }
        this.mMinAngle = minAngle;
        this.mMaxAngle = maxAngle;
        // clamp mAngle to new range
        if (this.mAngle < this.mMinAngle) {
            this.mAngle = this.mMinAngle;
        } else if (this.mAngle > this.mMaxAngle) {
            this.mAngle = this.mMaxAngle;
        }
        setPulse();
    }

    /**
     * Set the angle position, calculates the pulse and duty cycle
     *
     * @param angle the angle position in degrees
     */
    public void setAngle(float angle) {
        if (angle < this.getMinimumAngle() || angle > this.getMaximumAngle()) {
            throw new IllegalArgumentException("Angle (" + angle + ") not in range [" + this.getMinimumAngle()
                    + " - " + this.getMaximumAngle() + "]");
        }
        this.mAngle = angle;
        if (DEBUG) {
            Log.d(LOG_TAG, "Angle set to: " + this.mAngle + " degrees");
        }
        setPulse();
    }

    /**
     * Sets the pulse
     */
    private void setPulse() {
        double pulse = interpolate(this.mAngle, this.mMinAngle, this.mMaxAngle, this.mMinPulseDuration, this.mMaxPulseDuration);
        if (pulse < this.mMinPulseDuration || pulse > this.mMaxPulseDuration) {
            throw new IllegalArgumentException("Pulse (" + pulse + ") not in range [" + this.mMinPulseDuration
                    + " - " + this.mMaxPulseDuration + "]");
        }
        this.mPulse = pulse;
        if (DEBUG) {
            Log.d(LOG_TAG, "Pulse set to: " + this.mPulse);
        }
        setDutyCycle();
    }

    /**
     * Sets the duty cycle
     */
    private void setDutyCycle() {
        this.mDutyCycle = (int) (this.mPulse * 1000 / this.mPulseLength);
        if (DEBUG) {
            Log.d(LOG_TAG, "Duty cycle set to: " + this.mDutyCycle);
        }
    }

    /**
     * Linearly interpolates angle between servo ranges to get a pulse width in milliseconds
     * More about interpolation at: https://en.wikipedia.org/wiki/Linear_interpolation
     */
    private double interpolate(double angle, double minAngle, double maxAngle, double minPulse, double maxPulse) {
        double normalizedAngleRatio = (angle - minAngle) / (maxAngle - minAngle);
        double pulseWidth = minPulse + (maxPulse - minPulse) * normalizedAngleRatio;
        if (DEBUG) {
            Log.d(LOG_TAG, "Pulse width: " + pulseWidth + " ms");
        }
        return pulseWidth;
    }

    /**
     * @return the current minimum pulse duration
     */
    public double getMinimumPulseDuration() {
        return this.mMinPulseDuration;
    }

    /**
     * @return the current maximum pulse duration
     */
    public double getMaximumPulseDuration() {
        return this.mMaxPulseDuration;
    }

    /**
     * @return the minimum angle in degrees
     */
    public double getMinimumAngle() {
        return this.mMinAngle;
    }

    /**
     * @return the maximum angle in degrees
     */
    public double getMaximumAngle() {
        return this.mMaxAngle;
    }

    /**
     * @return the current angle in degrees
     */
    public double getAngle() {
        return this.mAngle;
    }

    /**
     * Returns the pulse duration in ms
     */
    public double getPulse() {
        return this.mPulse;
    }

    /**
     * Returns the frequency in Hz
     */
    public int getFrequencyHz() {
        return this.mFrequencyHz;
    }

    /**
     * Returns the index of the servo in the hat
     */
    public int getIndex() {
        return this.mIndex;
    }

    /**
     * Returns the duty cycle
     *
     * @return
     */
    public int getDutyCycle() {
        return mDutyCycle;
    }
}