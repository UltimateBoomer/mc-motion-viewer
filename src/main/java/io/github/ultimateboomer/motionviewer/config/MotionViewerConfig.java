package io.github.ultimateboomer.motionviewer.config;

import io.github.ultimateboomer.motionviewer.MotionViewer;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = MotionViewer.MODID)
public class MotionViewerConfig implements ConfigData {
    public boolean showVelocity = true;
    public boolean showAccel = false;

    public int avgInterval = 20;
    public int debugListPos = 11;
}
