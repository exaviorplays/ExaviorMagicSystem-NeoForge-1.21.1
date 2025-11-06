package net.exavior.exmagicsys.api.hud;

import net.exavior.exmagicsys.EMSConfig;
import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public class ManaBarOverlay {
    public static boolean USE_TEXTURE = false;
    public static ResourceLocation HUD_TEXTURE = ResourceLocation.fromNamespaceAndPath(ExaviorMagicSystem.MODID, "textures/gui/hud.png");
    public static int BAR_WIDTH = 81;
    public static int BAR_HEIGHT = 9;
    public static int X_OFFSET = 120;  // 10
    public static int Y_OFFSET = -11;  // -49
    public static int EMPTY_BAR_U = 0;
    public static int EMPTY_BAR_V = 0;
    public static int FULL_BAR_U = 0;
    public static int FULL_BAR_V = 9;
    public static int BACKGROUND_COLOR = 0xFF000000;
    public static boolean DRAW_TEXT = true;
    public static int TEXT_COLOR = 0xFFFFFFFF;
    public static int TEXT_X_OFFSET = 0;
    public static int TEXT_Y_OFFSET = 1;

    public static int SLOT_BOX_SIZE = 12; // Size of each of the 4 boxes
    public static int SLOT_BOX_PADDING = 2; // Padding between boxes
    public static int SLOT_BOX_Y_OFFSET = -25; //  -63 14px above mana bar (49 + 14)

    public static int ACTIVE_SPELL_NAME_X_OFFSET = 10;
    public static int ACTIVE_SPELL_NAME_Y_OFFSET = -36; // 11px above the boxes

    public static int COOLDOWN_COLOR = 0x80FFFFFF; // Semi-transparent white
    public static int READY_TINT_COLOR = 0x8000FF00; // Semi-transparent green
    public static int EMPTY_SLOT_COLOR = 0x80000000; // Semi-transparent black
    public static int ACTIVE_SLOT_BORDER_COLOR = 0xFFFFFF00; // Yellow

    public static final LayeredDraw.Layer OVERLAY = (guiGraphics, deltaTracker) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) {
            return;
        }

        Player player = mc.player;
        int mana = player.getData(EMSDataAttachments.MANA_VALUE.get());
        int maxMana = player.getData(EMSDataAttachments.MAX_MANA_VALUE.get());

        if (maxMana <= 0) {
            return;
        }

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        int barX = (screenWidth / 2) + X_OFFSET;
        int barY = screenHeight + Y_OFFSET;

        if (USE_TEXTURE) {
            guiGraphics.blit(HUD_TEXTURE, barX, barY, EMPTY_BAR_U, EMPTY_BAR_V, BAR_WIDTH, BAR_HEIGHT);
            float manaPercentage = (float)mana / (float)maxMana;
            int fillWidth = (int)(manaPercentage * BAR_WIDTH);
            if (fillWidth > 0) {
                guiGraphics.blit(HUD_TEXTURE, barX, barY, FULL_BAR_U, FULL_BAR_V, fillWidth, BAR_HEIGHT);
            }
        } else {
            int barColorFromConfig = EMSConfig.CLIENT.manaBarColor.get();

            guiGraphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, BACKGROUND_COLOR);

            float manaPercentage = (float)mana / (float)maxMana;
            int fillWidth = (int)(manaPercentage * (BAR_WIDTH - 2));

            if (fillWidth > 0) {
                guiGraphics.fill(barX + 1, barY + 1, barX + 1 + fillWidth, barY + BAR_HEIGHT - 1, barColorFromConfig);
            }
        }

        if (DRAW_TEXT) {
            Font font = mc.font;
            String manaText = String.valueOf(mana);
            int textWidth = font.width(manaText);
            int textX = (barX + (BAR_WIDTH / 2)) - (textWidth / 2) + TEXT_X_OFFSET;
            int textY = barY + TEXT_Y_OFFSET;
            guiGraphics.drawString(font, manaText, textX, textY, TEXT_COLOR, true);
        }

        List<ResourceLocation> spells = player.getData(EMSDataAttachments.EQUIPPED_SPELLS.get());
        int activeSlot = player.getData(EMSDataAttachments.ACTIVE_SPELL_SLOT.get());
        Map<ResourceLocation, Long> cooldowns = player.getData(EMSDataAttachments.SPELL_COOLDOWNS.get());
        Registry<Spell> spellRegistry = mc.player.level().registryAccess().registryOrThrow(EMSRegistries.SPELL_REGISTRY_KEY);
        long gameTime = mc.level.getGameTime();

        if (spells == null || spells.size() != 4) return; // Data not loaded yet

        int totalSlotsWidth = (SLOT_BOX_SIZE * 4) + (SLOT_BOX_PADDING * 3);
        int slotsX = barX + (BAR_WIDTH / 2) - (totalSlotsWidth / 2);
        int slotsY = screenHeight + SLOT_BOX_Y_OFFSET;

        ResourceLocation activeSpellId = null;

        for (int i = 0; i < 4; i++) {
            int x = slotsX + (i * (SLOT_BOX_SIZE + SLOT_BOX_PADDING));
            ResourceLocation spellId = spells.get(i);

            if(spellId == null) {
                guiGraphics.fill(x, slotsY, x + SLOT_BOX_SIZE, slotsY + SLOT_BOX_SIZE, EMPTY_SLOT_COLOR);
            } else {
                guiGraphics.fill(x, slotsY, x + SLOT_BOX_SIZE, slotsY + SLOT_BOX_SIZE, 0x80FFFFFF);

                Spell spell = spellRegistry.get(spellId);
                if(spell == null) continue;

                long expirationTime = cooldowns.getOrDefault(spellId, 0L);
                long ticksRemaining = expirationTime - gameTime;
                int totalCooldownTicks = spell.getCooldownTicks();

                if (ticksRemaining <= 0) {
                    guiGraphics.fill(x, slotsY, x + SLOT_BOX_SIZE, slotsY + SLOT_BOX_SIZE, READY_TINT_COLOR);
                } else if (totalCooldownTicks > 0) {
                    float percent = 1.0F - ((float)ticksRemaining / (float)totalCooldownTicks);
                    int fillHeight = (int)(percent * SLOT_BOX_SIZE);
                    int fillY = slotsY + (SLOT_BOX_SIZE - fillHeight);

                    guiGraphics.fill(x, fillY, x + SLOT_BOX_SIZE, slotsY + SLOT_BOX_SIZE, COOLDOWN_COLOR);
                }
            }

            if (i == activeSlot) {
                activeSpellId = spellId;
                guiGraphics.renderOutline(x - 1, slotsY - 1, SLOT_BOX_SIZE + 2, SLOT_BOX_SIZE + 2, ACTIVE_SLOT_BORDER_COLOR);
            }
        }

        if (activeSpellId != null) {
            Component spellName = Component.translatable("spell." + activeSpellId.getNamespace() + "." + activeSpellId.getPath());

            int nameXCenter = slotsX + (totalSlotsWidth / 2);
            int nameY = screenHeight + ACTIVE_SPELL_NAME_Y_OFFSET;
            guiGraphics.drawCenteredString(mc.font, spellName, nameXCenter, nameY, 0xFFFFFF);
        }
    };
}