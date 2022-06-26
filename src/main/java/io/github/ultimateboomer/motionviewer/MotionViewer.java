package io.github.ultimateboomer.motionviewer;

import io.github.ultimateboomer.motionviewer.mixin.MinecraftClientAccessor;
import io.github.ultimateboomer.motionviewer.mixin.RenderTickCounterAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MotionViewer implements ModInitializer {

	public static final String MODID = "motionviewer";
	public static final String MODNAME = "Motion Viewer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MotionViewer.MODID);

	private static MotionViewer INSTANCE;

	private MotionStatHandler motionStatHandler;

	public static MotionViewer getInstance() {
		return INSTANCE;
	}

	public MotionStatHandler getMotionStatHandler() {
		return motionStatHandler;
	}

	@Override
	public void onInitialize() {
		INSTANCE = this;
		initHandler();

		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

		LOGGER.info("{} initialized", MODNAME);
	}

	private void initHandler() {
		int bufferLength = 20;
		RenderTickCounter renderTickCounter = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getRenderTickCounter();
		float tickTime = ((RenderTickCounterAccessor) renderTickCounter).getTickTime();
		motionStatHandler = new MotionStatHandler(bufferLength, tickTime);
		LOGGER.info("Buffer length: {}; Tick interval: {}", bufferLength, tickTime);
	}

	private void onClientTick(MinecraftClient client) {
		if (client.player == null) return;
		motionStatHandler.pushData(client.player.getPos());
	}
}
