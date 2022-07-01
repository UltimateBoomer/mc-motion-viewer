package io.github.ultimateboomer.motionviewer.mixin;

import io.github.ultimateboomer.motionviewer.MotionViewer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Locale;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getLeftText", at = @At(value = "RETURN"))
    protected void onGetLeftText(CallbackInfoReturnable<List<String>> cir) {
        var config = MotionViewer.getInstance().getConfig();
        if (!config.showVelocity || !config.showAccel) return;

        var motionStatHandler = MotionViewer.getInstance().getMotionStatHandler();
        List<String> content = new ObjectArrayList<>();

        if (config.showVelocity) {
            double vX = motionStatHandler.getCurrentSpeedOnAxis(Direction.Axis.X);
            double vY = motionStatHandler.getCurrentSpeedOnAxis(Direction.Axis.Y);
            double vZ = motionStatHandler.getCurrentSpeedOnAxis(Direction.Axis.Z);
            double vXYZ = motionStatHandler.getCurrentSpeed();
            double vXZ = motionStatHandler.getCurrentSpeedOnPlane(Direction.Axis.Y);
            content.add(String.format(Locale.ROOT, "vXYZ: %.3f / %.3f / %.3f (%.3f), vXZ: %.3f",
                    vX, vY, vZ, vXYZ, vXZ));
        }
        if (config.showAccel) {
            double aX = motionStatHandler.getCurrentAccelerationOnAxis(Direction.Axis.X);
            double aY = motionStatHandler.getCurrentAccelerationOnAxis(Direction.Axis.Y);
            double aZ = motionStatHandler.getCurrentAccelerationOnAxis(Direction.Axis.Z);
            double aXYZ = motionStatHandler.getCurrentAcceleration();
            double aXZ = motionStatHandler.getCurrentAccelerationOnPlane(Direction.Axis.Y);
            content.add(String.format(Locale.ROOT, "aXYZ: %.3f / %.3f / %.3f (%.3f), vXZ: %.3f",
                    aX, aY, aZ, aXYZ, aXZ));
        }

        int actualPos;
        if (config.debugListPos > 0)
            actualPos = config.debugListPos;
        else
            actualPos = Math.max(cir.getReturnValue().size() + config.debugListPos, 0);
        cir.getReturnValue().addAll(actualPos, content);
    }
}
