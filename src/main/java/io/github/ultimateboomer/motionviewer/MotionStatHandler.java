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
    protected List<Vec3d> aQ;   // Accelerations

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
        this.aQ = new CircularList<>(bufferLength);
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
        // Calculate accelerations
        Vec3d aXYZ = Vec3d.ZERO;
        if (vQ.get(0) != null && vQ.get(1) != null) {
            aXYZ = vQ.get(0).subtract(vQ.get(1)).multiply(1000.0 / dt);
        }
        aQ.add(aXYZ);
    }

    public double getCurrentSpeed() {
        return Objects.requireNonNullElse(vQ.get(0), Vec3d.ZERO).length();
    }

    public double getAverageSpeed() {
        return vQ.stream().mapToDouble(Vec3d::length).average().orElse(0);
    }

    public double getCurrentSpeedOnAxis(Direction.Axis axis) {
        return Objects.requireNonNullElse(vQ.get(0), Vec3d.ZERO).getComponentAlongAxis(axis);
    }

    public double getAverageSpeedOnAxis(Direction.Axis axis) {
        return vQ.stream().mapToDouble(v -> v.getComponentAlongAxis(axis)).average().orElse(0);
    }

    public double getCurrentSpeedOnPlane(Direction.Axis axis) {
        return Objects.requireNonNullElse(vQ.get(0), Vec3d.ZERO).withAxis(axis, 0).length();
    }

    public double getAverageSpeedOnPlane(Direction.Axis axis) {
        return vQ.stream().mapToDouble(v -> v.withAxis(axis, 0).length()).average().orElse(0);
    }

    public double getCurrentAcceleration() {
        return Objects.requireNonNullElse(aQ.get(0), Vec3d.ZERO).length();
    }

    public double getAverageAcceleration() {
        return aQ.stream().mapToDouble(Vec3d::length).average().orElse(0);
    }

    public double getCurrentAccelerationOnAxis(Direction.Axis axis) {
        return Objects.requireNonNullElse(aQ.get(0), Vec3d.ZERO).getComponentAlongAxis(axis);
    }

    public double getAverageAccelerationOnAxis(Direction.Axis axis) {
        return aQ.stream().mapToDouble(a -> a.getComponentAlongAxis(axis)).average().orElse(0);
    }

    public double getCurrentAccelerationOnPlane(Direction.Axis axis) {
        return Objects.requireNonNullElse(aQ.get(0), Vec3d.ZERO).withAxis(axis, 0).length();
    }

    public double getAverageAccelerationOnPlane(Direction.Axis axis) {
        return aQ.stream().mapToDouble(a -> a.withAxis(axis, 0).length()).average().orElse(0);
    }
}
