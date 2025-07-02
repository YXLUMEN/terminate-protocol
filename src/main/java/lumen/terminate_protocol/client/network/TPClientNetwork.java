package lumen.terminate_protocol.client.network;


public class TPClientNetwork {
    public static void registryNetworks() {
        ReceiveWeaponActions.register();
        FlashEffectClientPacket.register();
        BatterySoundsPacket.register();
    }
}
