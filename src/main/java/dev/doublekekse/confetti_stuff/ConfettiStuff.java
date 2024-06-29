package dev.doublekekse.confetti_stuff;

import dev.doublekekse.confetti_stuff.block.ConfettiCannon;
import dev.doublekekse.confetti_stuff.block.ConfettiLayer;
import dev.doublekekse.confetti_stuff.item.Broom;
import dev.doublekekse.confetti_stuff.item.PartyPopper;
import dev.doublekekse.confetti_stuff.packet.BroomKickEntityPacket;
import dev.doublekekse.confetti_stuff.registry.SoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ConfettiStuff implements ModInitializer {
    public static final PartyPopper PARTY_POPPER = new PartyPopper(new Item.Properties().stacksTo(1));
    public static final Broom BROOM = new Broom(new Item.Properties().stacksTo(1));
    public static final ConfettiCannon CONFETTI_CANNON = new ConfettiCannon(BlockBehaviour.Properties.of().strength(1).noOcclusion().sound(SoundType.METAL));
    public static final BlockItem CONFETTI_CANNON_ITEM = Registry.register(BuiltInRegistries.ITEM, id("confetti_cannon"), new BlockItem(CONFETTI_CANNON, new Item.Properties()));
    public static final CarpetBlock CONFETTI_LAYER = new ConfettiLayer(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.1F).pushReaction(PushReaction.DESTROY).noOcclusion().sound(SoundType.WOOL).ignitedByLava());
    public static final BlockItem CONFETTI_LAYER_ITEM = Registry.register(BuiltInRegistries.ITEM, id("confetti_layer"), new BlockItem(CONFETTI_LAYER, new Item.Properties()));

    public static final CreativeModeTab CONFETTI_STUFF_TAB = FabricItemGroup.builder()
        .icon(() -> new ItemStack(PARTY_POPPER))
        .title(Component.translatable("itemGroup.confetti_stuff.stuff"))
        .displayItems((context, entries) -> {
            entries.accept(PARTY_POPPER);
            entries.accept(BROOM);
            entries.accept(CONFETTI_CANNON_ITEM);
            entries.accept(CONFETTI_LAYER_ITEM);
        })
        .build();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(BroomKickEntityPacket.TYPE, BroomKickEntityPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(BroomKickEntityPacket.TYPE, BroomKickEntityPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(BroomKickEntityPacket.TYPE, BroomKickEntityPacket::handleServer);

        Registry.register(BuiltInRegistries.ITEM, id("party_popper"), PARTY_POPPER);
        Registry.register(BuiltInRegistries.ITEM, id("broom"), BROOM);
        Registry.register(BuiltInRegistries.BLOCK, id("confetti_cannon"), CONFETTI_CANNON);
        Registry.register(BuiltInRegistries.BLOCK, id("confetti_layer"), CONFETTI_LAYER);

        SoundEvents.register();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.accept(CONFETTI_CANNON_ITEM);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(content -> {
            content.accept(CONFETTI_LAYER_ITEM);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.accept(PARTY_POPPER);
        });


        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id("stuff"), CONFETTI_STUFF_TAB);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("confetti_stuff", path);
    }
}
