package dev.arrowsinfo.mixin;

import dev.arrowsinfo.Constants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 790)
public abstract class GuiMixin {

  @Shadow
  @Final
  private static ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE, HOTBAR_OFFHAND_RIGHT_SPRITE;

  @Shadow
  private Player getCameraPlayer() {
    return null;
  }

  @Shadow
  private void renderSlot(GuiGraphics guiGraphics, int i, int j, DeltaTracker deltaTracker, Player player, ItemStack stack, int k) {
  }

  @Inject(method = "renderItemHotbar", at = @At("RETURN"))
  private void cr$renderHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    Player player = this.getCameraPlayer();
    if (player == null) {
      return;
    }
    ItemStack offHandStack = player.getOffhandItem(), arrows = player.getProjectile(player.getMainHandItem());
    if (arrows.isEmpty()) {
      arrows = player.getProjectile(offHandStack);
    }
    if (arrows.isEmpty() || arrows == offHandStack) {
      return;
    }
    Constants.Placement placement = Constants.placement;
    HumanoidArm arm = player.getMainArm();
    if (placement == Constants.Placement.OFFHAND) {
      arm = arm.getOpposite();
    }
    int x;
    if (arm == HumanoidArm.LEFT) {
      x = guiGraphics.guiWidth() / 2 - 91 - 29;
      if (placement == Constants.Placement.OFFHAND && !offHandStack.isEmpty()) {
        x -= 23;
      }
    }
    else {
      x = guiGraphics.guiWidth() / 2 + 91;
      if (placement == Constants.Placement.OFFHAND && !offHandStack.isEmpty()) {
        x += 23;
      }
    }
    int y = guiGraphics.guiHeight() - 23;
    if (FabricLoader.getInstance().getObjectShare().get("raised:hud") instanceof Integer distance) {
      y -= distance;
    }
    RenderSystem.defaultBlendFunc();
    RenderSystem.enableBlend();
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(0.0F, 0.0F, -90.0F);
    if (arm == HumanoidArm.LEFT) {
      guiGraphics.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, x, y, 29, 24);
    }
    else {
      guiGraphics.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, x, y, 29, 24);
    }
    guiGraphics.pose().popPose();
    if (arm == HumanoidArm.LEFT) {
      this.renderSlot(guiGraphics, x + 3, y + 4, deltaTracker, player, arrows, 1);
    }
    else {
      this.renderSlot(guiGraphics, x + 10, y + 4, deltaTracker, player, arrows, 1);
    }
    RenderSystem.disableBlend();
  }
}
