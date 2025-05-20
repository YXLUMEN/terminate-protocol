package lumen.terminate_protocol.network;

public class TPClientNetwork {
    public static void registryNetworks() {
        ReceiveWeaponActions.register();
        FlashEffectClientPacket.register();
        BatterySoundsPacket.register();
    }
}
