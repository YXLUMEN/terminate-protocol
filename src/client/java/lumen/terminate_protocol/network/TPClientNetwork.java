package lumen.terminate_protocol.network;

public class TPClientNetwork {
    public static void registryNetworks() {
        ServerFireSyncAccept.register();
        FlashEffectClientPacket.register();
        BatterySoundsPacket.register();
    }
}
