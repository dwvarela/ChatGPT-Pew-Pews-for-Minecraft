package com.example.vanillam16;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class M16Item extends Item {
    private static final int FIRE_COOLDOWN_TICKS = 3;
    public M16Item(Properties properties) { super(properties); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack rifle = player.getItemInHand(hand);
        if (player.isCrouching()) {
            if (level.isClientSide) return InteractionResultHolder.success(rifle);
            if (rifle.getDamageValue() == 0) {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("M16 magazine is already full."), true);
                return InteractionResultHolder.consume(rifle);
            }
            int slot = findMagazine(player);
            if (slot < 0) {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("Need an M16 magazine to reload."), true);
                return InteractionResultHolder.fail(rifle);
            }
            player.getInventory().getItem(slot).shrink(1);
            rifle.setDamageValue(0);
            player.playSound(SoundEvents.ARMOR_EQUIP_IRON.value(), 0.8F, 1.25F);
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("M16 reloaded: 30 rounds."), true);
            return InteractionResultHolder.consume(rifle);
        }
        if (rifle.getDamageValue() >= rifle.getMaxDamage()) {
            if (!level.isClientSide) player.displayClientMessage(net.minecraft.network.chat.Component.literal("M16 empty. Sneak + right-click to reload."), true);
            return InteractionResultHolder.fail(rifle);
        }
        if (!level.isClientSide) {
            Arrow bullet = new Arrow(EntityType.ARROW, level);
            bullet.setOwner(player);
            bullet.setPos(player.getX(), player.getEyeY() - 0.1D, player.getZ());
            bullet.setBaseDamage(5.0);
            bullet.setCritArrow(false);
            bullet.pickup = Arrow.Pickup.DISALLOWED;
            bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.2F, 1.0F);
            level.addFreshEntity(bullet);
            level.playSound(null, player.blockPosition(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 0.75F, 1.35F);
            rifle.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        player.getCooldowns().addCooldown(this, FIRE_COOLDOWN_TICKS);
        return InteractionResultHolder.sidedSuccess(rifle, level.isClientSide());
    }
    private static int findMagazine(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++)
            if (player.getInventory().getItem(i).is(VanillaM16.MAGAZINE.get())) return i;
        return -1;
    }
}
