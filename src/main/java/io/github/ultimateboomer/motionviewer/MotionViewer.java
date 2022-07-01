package io.github.ultimateboomer.motionviewer;

import io.github.ultimateboomer.motionviewer.config.MotionViewerConfig;
import io.github.ultimateboomer.motionviewer.mixin.MinecraftClientAccessor;
import io.github.ultimateboomer.motionviewer.mixin.RenderTickCounterAccessor;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MotionViewer implements ModInitializer {

	public static final String MODID = "motionviewer";
	public static final String MODNAME = "Motion Viewer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MotionViewer.MODID);

	private static MotionViewer INSTANCE;

	private MotionViewerConfig config;

	private MotionStatHandler motionStatHandler;

	public static MotionViewer getInstance() {
		return INSTANCE;
	}

	public MotionStatHandler getMotionStatHandler() {
		return motionStatHandler;
	}

	public MotionViewerConfig getConfig() {
		return config;
	}

	@Override
	public void onInitialize() {
		INSTANCE = this;

		var configHolder = AutoConfig.register(MotionViewerConfig.class, GsonConfigSerializer::new);
		configHolder.registerSaveListener(this::onConfigSave);
		this.config = configHolder.getConfig();

		initHandler();

		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);

		LOGGER.info("{} initialized", MODNAME);
	}

	private ActionResult onConfigSave(ConfigHolder<MotionViewerConfig> configHolder, MotionViewerConfig config) {
		initHandler();
		return ActionResult.SUCCESS;
	}

	private void initHandler() {
		int bufferLength = config.avgInterval;
		var renderTickCounter = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getRenderTickCounter();
		float tickTime = ((RenderTickCounterAccessor) renderTickCounter).getTickTime();
		motionStatHandler = new MotionStatHandler(bufferLength, tickTime);
		LOGGER.info("Buffer length: {}; Tick interval: {}ms", bufferLength, tickTime);
	}

	private void onClientTick(MinecraftClient client) {
		if (client.player == null) return;
		motionStatHandler.pushData(client.player.getPos());
	}
}
