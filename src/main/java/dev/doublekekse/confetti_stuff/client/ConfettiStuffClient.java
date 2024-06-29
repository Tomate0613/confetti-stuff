package dev.doublekekse.confetti_stuff.client;

import dev.doublekekse.confetti_stuff.ConfettiStuff;
import dev.doublekekse.confetti_stuff.packet.BroomKickEntityPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.renderer.RenderType;

public class ConfettiStuffClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ConfettiStuff.CONFETTI_CANNON, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ConfettiStuff.CONFETTI_LAYER, RenderType.cutout());

        ClientPlayNetworking.registerGlobalReceiver(BroomKickEntityPacket.TYPE, BroomKickEntityPacket::handleClient);
    }
}
