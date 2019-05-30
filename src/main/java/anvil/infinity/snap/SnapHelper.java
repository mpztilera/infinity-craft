package anvil.infinity.snap;

import anvil.infinity.config.ConfigHandler;
import anvil.infinity.data.EntityData;
import anvil.infinity.data.GauntletUserInformation;
import anvil.infinity.helpers.GauntelHelper;
import anvil.infinity.registry.Effects;
import lucraft.mods.lucraftcore.infinity.ModuleInfinity;
import lucraft.mods.lucraftcore.superpowers.abilities.supplier.IAbilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnapHelper {

    static MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

    public static boolean snap(EntityLivingBase entity) {
        EntityData data = GauntletUserInformation.getDataByEntity(entity);
        if (GauntelHelper.hasFullGauntlet(entity)) {
            WorldServer[] worlds = server.worlds;
            if (data.selectedSnapResult == SnapResult.KILLHALF) {
                List<Entity> entities = new ArrayList<Entity>();
                for (int i = 0; i < worlds.length; i++) {
                    entities.addAll(worlds[i].loadedEntityList);
                }
                boolean kill = false;

                PlayerList players = server.getPlayerList();

                TextComponentString msg = new TextComponentString(entity.getName() + ": ");
                msg.appendSibling(new TextComponentTranslation("infinity.snap.text"));
                msg.getStyle().setColor(TextFormatting.DARK_PURPLE);
                msg.getStyle().setBold(true);
                players.sendMessage(msg);
                Random random = new Random();


                for (int i = 0; i < entities.size(); i++) {
                    if (entities.get(i) != entity && entities.get(i) instanceof EntityLivingBase && kill && !(entities.get(i) instanceof EntityRabbit)) {
                        EntityLivingBase e = ((EntityLivingBase) entities.get(i));
                        e.addPotionEffect(new PotionEffect(Effects.snapEffect, random.nextInt((1200 - 10) + 1) - 10));
                    }
                    kill = !kill;
                }
                return true;
            } else if (data.selectedSnapResult == SnapResult.DESTROYSTONES) {
                    entity.setHealth(1);
                    Item mainHand = entity.getHeldItemMainhand().getItem();
                    Item offHand = entity.getHeldItemOffhand().getItem();
                    if (mainHand instanceof IAbilityProvider) {
                        entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(mainHand));
                    }
                    if (offHand instanceof IAbilityProvider) {
                        entity.setHeldItem(EnumHand.OFF_HAND, new ItemStack(offHand));
                    }
                    entity.attackEntityFrom(DamageSource.MAGIC, (entity.getHealth() / 10) - 0.01f);


            } else if (data.selectedSnapResult == SnapResult.CREATIVE) {
                if (ConfigHandler.snapCreative) {
                    if (entity instanceof EntityPlayer) {

                        if (((EntityPlayer) entity).capabilities.isCreativeMode) {
                            ((EntityPlayer) entity).setGameType(GameType.SURVIVAL);
                        } else {
                            ((EntityPlayer) entity).setGameType(GameType.CREATIVE);
                        }
                        return true;
                    }
                }
            } else if (data.selectedSnapResult == SnapResult.BRINGBACK) {

            } else if (data.selectedSnapResult == SnapResult.RECREATE) {

            }
        }
        return false;
    }

}
