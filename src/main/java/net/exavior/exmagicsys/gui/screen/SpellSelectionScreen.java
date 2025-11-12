package net.exavior.exmagicsys.gui.screen;

import net.exavior.exmagicsys.api.EMSMagicApi;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.api.spell.SpellClassification;
import net.exavior.exmagicsys.network.client.toserverpackets.ServerSetEquippedSpellPacket;
import net.exavior.exmagicsys.registry.EMSDataAttachments;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpellSelectionScreen extends Screen {
    private final List<ResourceLocation> knownSpells;
    private final Registry<Spell> spellRegistry;
    public static Set<String> activeFilters = new HashSet<>();

    protected int searchBoxX = 10;
    protected int searchBoxY = 20;
    protected int searchBoxWidth = 150;
    protected int searchBoxHeight = 20;
    protected int classButtonX = 10;
    protected int classButtonY = 45;
    protected int classButtonWidth = 98;
    protected int classButtonHeight = 20;
    protected int listTop = 75;
    protected int listBottomPadding = 10;
    protected int listEntryHeight = 25;
    protected int listWidth = 160;
    protected int titleY = 8;
    protected int searchLabelX = 10;
    protected int searchLabelY = 10;
    protected int propBoxX = 0;
    protected int propBoxY = 75;
    protected int propBoxWidth = 120;
    protected int propTextPadding = 10;
    protected int propLineHeight = 12;

    protected EditBox searchBox;
    protected SpellListWidget spellListWidget;
    protected final List<SpellSlotWidget> spellSlotWidgets = new ArrayList<>();
    protected Button trashButton;

    private boolean ignoreNextChar = true;
    @Nullable
    private ResourceLocation pickedUpSpell = null;
    private Component pickedUpSpellName = null;

    protected static final Component SEARCH_LABEL = Component.translatable("exmagicsys.gui.select_spell.search");
    protected static final Component CLASSIFICATIONS_LABEL = Component.translatable("exmagicsys.gui.classifications");
    private static final Component EMPTY_SLOT_TEXT = Component.translatable("exmagicsys.gui.slot.empty");
    private static final Component CURSOR_PICKUP_TOOLTIP = Component.translatable("exmagicsys.gui.slot.pickup_tooltip");
    private static final Component CURSOR_PLACE_TOOLTIP = Component.translatable("exmagicsys.gui.slot.place_tooltip");
    private static final Component PROP_CLASSIFICATION = Component.translatable("exmagicsys.gui.prop.classification");
    private static final Component PROP_MANA_COST = Component.translatable("exmagicsys.gui.prop.mana_cost");
    private static final Component PROP_COOLDOWN = Component.translatable("exmagicsys.gui.prop.cooldown");
    private static final Component PROP_CHARGE_TIME = Component.translatable("exmagicsys.gui.prop.charge_time");
    private static final Component PROP_CAST_TIME = Component.translatable("exmagicsys.gui.prop.cast_time");
    private static final Component PROP_ACTIVE_TIME = Component.translatable("exmagicsys.gui.prop.active_time");
    private static final Component PROP_ACTIVE_MANA_COST = Component.translatable("exmagicsys.gui.prop.active_mana_cost");
    private static final Component PROP_SECONDS = Component.translatable("exmagicsys.gui.prop.seconds");
    private static final Component PROP_TICKS = Component.translatable("exmagicsys.gui.prop.ticks");
    private static final Component TRASH_TEXT = Component.translatable("exmagicsys.gui.slot.trash");
    private static final Component TRASH_TOOLTIP = Component.translatable("exmagicsys.gui.slot.trash_tooltip");

    public SpellSelectionScreen() {
        super(Component.translatable("exmagicsys.gui.select_spell.title"));
        this.knownSpells = new ArrayList<>(EMSMagicApi.getKnownSpells(Minecraft.getInstance().player));
        this.spellRegistry = Minecraft.getInstance().player.level().registryAccess()
                .registryOrThrow(EMSRegistries.SPELL_REGISTRY_KEY);
    }

    @Override
    protected void init() {
        super.init();
        this.spellSlotWidgets.clear();

        this.searchBox = new EditBox(this.font, this.searchBoxX, this.searchBoxY, this.searchBoxWidth, this.searchBoxHeight, SEARCH_LABEL);
        this.searchBox.setResponder(this::onSearchBoxChanged);
        this.addRenderableWidget(this.searchBox);
        this.setFocused(this.searchBox);
        this.addRenderableWidget(
                Button.builder(CLASSIFICATIONS_LABEL, (button) -> {
                    this.minecraft.setScreen(new SpellClassificationScreen(this));
                }).bounds(this.classButtonX, this.classButtonY, this.classButtonWidth, this.classButtonHeight).build()
        );

        int listX = (this.width / 2) - this.listWidth - 10;
        int listHeight = this.height - this.listTop - this.listBottomPadding;
        this.spellListWidget = new SpellListWidget(listX, this.listTop, this.listWidth, listHeight);
        this.addRenderableWidget(this.spellListWidget);
        int slotX = this.width / 2 + 10;
        int slotY = this.listTop;
        int slotWidth = 150;
        int slotHeight = 20;
        int slotPadding = 5;
        for (int i = 0; i < 4; i++) {
            SpellSlotWidget slotWidget = new SpellSlotWidget(slotX, slotY + (i * (slotHeight + slotPadding)), slotWidth, slotHeight, i);
            this.spellSlotWidgets.add(slotWidget);
            this.addRenderableWidget(slotWidget);
        }

        int trashY = slotY + (4 * (slotHeight + slotPadding));
        this.trashButton = Button.builder(TRASH_TEXT, (button) -> {
                    this.pickedUpSpell = null;
                    this.pickedUpSpellName = null;
                })
                .bounds(slotX, trashY, slotWidth, slotHeight)
                .tooltip(Tooltip.create(TRASH_TOOLTIP))
                .build();

        this.addRenderableWidget(this.trashButton);

        this.propBoxX = slotX + slotWidth + 10;

        this.rebuildSpellList();
        this.updateSlotWidgets();

        this.trashButton.visible = false;
    }

    private void onSearchBoxChanged(String searchText) { this.rebuildSpellList(); }
    private void updateSlotWidgets() {
        List<ResourceLocation> equippedSpells = EMSMagicApi.getEquippedSpells(this.minecraft.player);
        int activeSlot = EMSMagicApi.getActiveSpellSlot(this.minecraft.player);
        for (SpellSlotWidget widget : this.spellSlotWidgets) {
            if (equippedSpells != null && equippedSpells.size() == 4) {
                widget.update(equippedSpells.get(widget.slotIndex), widget.slotIndex == activeSlot);
            }
        }
    }
    private void rebuildSpellList() {
        List<SpellEntry> newEntries = new ArrayList<>();
        String searchText = this.searchBox.getValue().toLowerCase().trim();
        for (ResourceLocation spellId : this.knownSpells) {
            Spell spell = spellRegistry.get(spellId);
            if (spell == null) continue;
            if (!activeFilters.isEmpty()) {
                Holder<SpellClassification> classificationHolder = spell.getClassification();
                String spellClassPath = classificationHolder.unwrapKey()
                        .map(key -> key.location().getPath())
                        .orElse("none");
                if (!activeFilters.contains(spellClassPath)) {
                    continue;
                }
            }
            Component spellName = Component.translatable("spell." + spellId.getNamespace() + "." + spellId.getPath());
            String translatedName = spellName.getString().toLowerCase();
            if (translatedName.contains(searchText)) {
                newEntries.add(new SpellEntry(spellId, spellName));
            }
        }
        this.spellListWidget.updateEntries(newEntries);
    }
    private void setEquippedSpell(int slot, @Nullable ResourceLocation spellId) {
        PacketDistributor.sendToServer(new ServerSetEquippedSpellPacket(slot, spellId));
        List<ResourceLocation> equipped = new ArrayList<>(EMSMagicApi.getEquippedSpells(this.minecraft.player));
        if (equipped.size() == 4) {
            equipped.set(slot, spellId);
            this.minecraft.player.setData(EMSDataAttachments.EQUIPPED_SPELLS.get(), equipped);
        }
        this.updateSlotWidgets();
    }


    @Override
    public void tick() {
        super.tick();
        this.updateSlotWidgets();

        if (this.trashButton != null) {
            this.trashButton.visible = (this.pickedUpSpell != null);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.spellListWidget.render(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.titleY, 0xFFFFFF);
        guiGraphics.drawString(this.font, SEARCH_LABEL, this.searchLabelX, this.searchLabelY, 0xA0A0A0);

        if (this.pickedUpSpell != null) {
            if (this.pickedUpSpellName != null) {
                guiGraphics.renderTooltip(this.font, this.pickedUpSpellName, mouseX, mouseY);
            }
            this.renderSpellProperties(guiGraphics, this.pickedUpSpell);
        }
    }
    protected void renderSpellProperties(GuiGraphics guiGraphics, ResourceLocation spellId) {
        Spell spell = this.spellRegistry.get(spellId);
        if (spell == null) {
            return;
        }
        int x = this.propBoxX;
        int y = this.propBoxY;
        int yOffset = y;
        Component spellName = Component.translatable("spell." + spellId.getNamespace() + "." + spellId.getPath());
        Component className = spell.getClassification().isBound() ?
                spell.getClassification().value().description() :
                Component.literal("Unknown");

        int boxHeight = this.propLineHeight * 6;
        if (spell.getActiveTimeTicks() > 0) {
            boxHeight += this.propLineHeight * 2;
        }
        guiGraphics.fill(x - 5, y - 5, x + this.propBoxWidth, y + boxHeight, 0x90000000);
        guiGraphics.drawString(this.font, spellName, x, yOffset, 0xFFFFFF, true);
        yOffset += this.propLineHeight + 2;
        guiGraphics.drawString(this.font, PROP_CLASSIFICATION.copy().append(": ").append(className), x, yOffset, 0xAAAAAA, false);
        yOffset += this.propLineHeight;
        guiGraphics.drawString(this.font, PROP_MANA_COST.copy().append(": " + spell.getManaCost()), x, yOffset, 0xAAAAAA, false);
        yOffset += this.propLineHeight;
        guiGraphics.drawString(this.font, PROP_COOLDOWN.copy().append(": " + (spell.getCooldownTicks() / 20.0f) + "s"), x, yOffset, 0xAAAAAA, false);
        yOffset += this.propLineHeight;
        guiGraphics.drawString(this.font, PROP_CHARGE_TIME.copy().append(": " + (spell.getChargeTimeTicks() > 0 ? (spell.getChargeTimeTicks() / 20.0f) + "s" : "0")), x, yOffset, 0xAAAAAA, false);
        yOffset += this.propLineHeight;
        guiGraphics.drawString(this.font, PROP_CAST_TIME.copy().append(": " + (spell.getCastTimeTicks() > 0 ? (spell.getCastTimeTicks() / 20.0f) + "s" : "0")), x, yOffset, 0xAAAAAA, false);
        yOffset += this.propLineHeight;

        if (spell.getActiveTimeTicks() > 0) {
            guiGraphics.drawString(this.font, PROP_ACTIVE_TIME.copy().append(": " + (spell.getActiveTimeTicks() / 20.0f) + "s"), x, yOffset, 0xAAAAAA, false);
            yOffset += this.propLineHeight;
            guiGraphics.drawString(this.font, PROP_ACTIVE_MANA_COST.copy().append(": " + spell.getManaCostPerActiveTick()), x, yOffset, 0xAAAAAA, false);
        }
    }


    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.ignoreNextChar) { this.ignoreNextChar = false; return true; }
        if (this.searchBox.charTyped(codePoint, modifiers)) { return true; }
        return super.charTyped(codePoint, modifiers);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (this.spellListWidget.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override public boolean isPauseScreen() { return false; }

    protected class SpellListWidget extends ContainerObjectSelectionList<SpellEntry> {
        public SpellListWidget(int x, int y, int width, int height) {
            super(SpellSelectionScreen.this.minecraft, width, height, y, SpellSelectionScreen.this.listEntryHeight);
            this.setX(x);
            this.setRenderHeader(false, 0);
        }
        public void updateEntries(List<SpellEntry> entries) {
            this.clearEntries();
            for(SpellEntry entry : entries) {
                this.addEntry(entry);
            }
        }
        @Override public int getScrollbarPosition() { return this.getX() + this.width - 6; }
        @Override public int getRowWidth() { return this.width - 10; }
    }

    protected class SpellEntry extends ContainerObjectSelectionList.Entry<SpellEntry> {
        protected final Button button;
        protected final ResourceLocation spellId;
        protected final Component spellName;
        public SpellEntry(ResourceLocation spellId, Component spellName) {
            this.spellId = spellId;
            this.spellName = spellName;
            this.button = Button.builder(spellName, (b) -> {
                SpellSelectionScreen.this.pickedUpSpell = this.spellId;
                SpellSelectionScreen.this.pickedUpSpellName = this.spellName;
            }).bounds(0, 0, 150, 20).build();
        }
        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            this.button.setX(left);
            this.button.setY(top);
            this.button.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        @Override public List<? extends GuiEventListener> children() { return List.of(this.button); }
        @Override public List<? extends NarratableEntry> narratables() { return List.of(this.button); }
    }

    protected class SpellSlotWidget extends Button {
        public final int slotIndex;
        private boolean isSlotActive = false;
        public SpellSlotWidget(int x, int y, int width, int height, int slotIndex) {
            super(x, y, width, height, EMPTY_SLOT_TEXT, (button) -> {
                if (pickedUpSpell != null) {
                    setEquippedSpell(slotIndex, pickedUpSpell);
                    pickedUpSpell = null;
                    pickedUpSpellName = null;
                } else {
                    List<ResourceLocation> spells = EMSMagicApi.getEquippedSpells(minecraft.player);
                    if (spells.size() == 4 && spells.get(slotIndex) != null) {
                        pickedUpSpell = spells.get(slotIndex);
                        pickedUpSpellName = button.getMessage();
                        setEquippedSpell(slotIndex, null);
                    }
                }
            }, Button.DEFAULT_NARRATION);
            this.slotIndex = slotIndex;
        }
        public void update(ResourceLocation spellId, boolean isActive) {
            if (spellId != null) {
                this.setMessage(Component.translatable("spell." + spellId.getNamespace() + "." + spellId.getPath()));
            } else {
                this.setMessage(EMPTY_SLOT_TEXT);
            }
            if (pickedUpSpell != null) {
                this.setTooltip(Tooltip.create(CURSOR_PLACE_TOOLTIP));
            } else if (spellId != null) {
                this.setTooltip(Tooltip.create(CURSOR_PICKUP_TOOLTIP));
            } else {
                this.setTooltip(null);
            }
            this.isSlotActive = isActive;
        }
        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.active = true;
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            if (this.isSlotActive) {
                guiGraphics.renderOutline(this.getX() - 2, this.getY() - 2, this.width + 4, this.height + 4, 0xFFFFFF00);
            }
        }
    }
}