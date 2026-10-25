package com.example.vanillam16;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(VanillaM16.MODID)
public final class VanillaM16 {
    public static final String MODID = "vanilla_m16";
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> M16 = ITEMS.register("m16",
            () -> new M16Item(new Item.Properties().stacksTo(1).durability(30)));
    public static final DeferredItem<Item> MAGAZINE = ITEMS.register("magazine",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public VanillaM16(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(M16);
            event.accept(MAGAZINE);
        }
    }
}
