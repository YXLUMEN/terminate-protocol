package lumen.terminate_protocol;

import lumen.terminate_protocol.effect.TPEffects;
import lumen.terminate_protocol.entity.TPEntities;
import lumen.terminate_protocol.item.TPItems;
import lumen.terminate_protocol.network.ServerFireHandler;
import lumen.terminate_protocol.network.TPNetwork;
import lumen.terminate_protocol.sound.TPSoundEvents;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerminateProtocol implements ModInitializer {
    public static final String MOD_ID = "terminate-protocol";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing TerminateProtocol");

        TPNetwork.registerNetwork();

        TPItemsGroup.registerItemsGroup();
        TPComponentTypes.registerComponent();

        TPItems.registerItems();
        TPEntities.registerEntities();
        TPEffects.registerEffects();
        TPSoundEvents.registerSounds();

        ServerFireHandler.register();
    }
}