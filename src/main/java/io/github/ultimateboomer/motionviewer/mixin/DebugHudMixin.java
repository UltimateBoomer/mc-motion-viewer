package io.github.ultimateboomer.motionviewer.mixin;

import io.github.ultimateboomer.motionviewer.MotionViewer;
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
        var motionStatHandler = MotionViewer.getInstance().getMotionStatHandler();
        double vX = motionStatHandler.getCurrentSpeedOnAxis(Direction.Axis.X);
        double vY = motionStatHandler.getCurrentSpeedOnAxis(Direction.Axis.Y);
        double vZ = motionStatHandler.getCurrentSpeedOnAxis(Direction.Axis.Z);
        double vXYZ = motionStatHandler.getCurrentSpeed();
        double vXZ = motionStatHandler.getCurrentSpeedOnPlane(Direction.Axis.Y);

        cir.getReturnValue().add(11, String.format(Locale.ROOT, "vXYZ: %.3f / %.3f / %.3f (%.3f), vXZ: %.3f",
                vX, vY, vZ, vXYZ, vXZ));
    }
}
