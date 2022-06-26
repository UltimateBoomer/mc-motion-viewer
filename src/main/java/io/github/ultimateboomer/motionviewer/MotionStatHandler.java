package io.github.ultimateboomer.motionviewer;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Objects;

/**
 * Class for calculating motion information from discrete position values with constant time step
 */
public class MotionStatHandler {
    protected int bufferLength;
    protected double dt;
    protected List<Vec3d> posQ; // Positions
    protected List<Vec3d> vQ;   // Velocities

    /**
     * Initialize with length for tracking average data
     * @param bufferLength number of data points to use for statistics
     * @param dt timestep in ms
     */
    public MotionStatHandler(int bufferLength, double dt) {
        this.bufferLength = bufferLength;
        this.dt = dt;
        this.posQ = new CircularList<>(bufferLength);
        this.vQ = new CircularList<>(bufferLength);
    }

    /**
     * Push the current position vector to the buffer
     * @param newPos position
     */
    public void pushData(Vec3d newPos) {
        posQ.add(newPos);

        // Calculate velocities
        Vec3d vXYZ = Vec3d.ZERO;
        if (posQ.get(0) != null && posQ.get(1) != null) {
            vXYZ = posQ.get(0).subtract(posQ.get(1)).multiply(1000.0 / dt);
        }

        vQ.add(vXYZ);
    }

    /**
     * Get current XYZ speed based on position data
     * @return speed in m/s
     */
    public double getCurrentSpeed() {
        return Objects.requireNonNullElse(vQ.get(0), Vec3d.ZERO).length();
    }

    /**
     * Get mean XYZ speed in buffer interval based on position data
     * @return speed in m/s
     */
    public double getAverageSpeed() {
        return vQ.stream().mapToDouble(Vec3d::length).average().orElse(0);
    }

    /**
     * Get current speed on axis based on position data
     * @param axis axis
     * @return speed in m/s
     */
    public double getCurrentSpeedOnAxis(Direction.Axis axis) {
        return Objects.requireNonNullElse(vQ.get(0), Vec3d.ZERO).getComponentAlongAxis(axis);
    }

    /**
     * Get mean speed on axis in buffer interval based on position data
     * @param axis axis
     * @return speed in m/s
     */
    public double getAverageSpeedOnAxis(Direction.Axis axis) {
        return vQ.stream().mapToDouble(v -> v.getComponentAlongAxis(axis)).average().orElse(0);
    }

    /**
     * Get current speed on plane based on position data
     * @param axis normal axis
     * @return speed in m/s
     */
    public double getCurrentSpeedOnPlane(Direction.Axis axis) {
        return Objects.requireNonNullElse(vQ.get(0), Vec3d.ZERO).withAxis(axis, 0).length();
    }

    /**
     * Get mean speed on plane in buffer interval based on position data
     * @param axis normal axis
     * @return speed in m/s
     */
    public double getAverageSpeedOnPlane(Direction.Axis axis) {
        return vQ.stream().mapToDouble(v -> v.withAxis(axis, 0).length()).average().orElse(0);
    }
}
