package net.exavior.exmagicsys.gui.screen;

import net.exavior.exmagicsys.api.spell.SpellClassification;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class SpellClassificationScreen extends Screen {
    protected int searchBoxX = 10;
    protected int searchBoxY = 20;
    protected int searchBoxWidth = 150;
    protected int searchBoxHeight = 20;
    protected int listTop = 50;
    protected int listBottomPadding = 60;
    protected int listEntryHeight = 25;
    protected int listWidth = 160;
    protected int applyButtonWidth = 100;
    protected int applyButtonHeight = 20;
    protected int applyButtonYPadding = 30;
    protected int titleY = 8;
    protected int searchLabelX = 10;
    protected int searchLabelY = 10;

    protected final Screen parent;
    protected Set<String> toggledFilters;
    protected final Map<String, Component> uniqueClassificationButtons;
    protected final Map<String, Set<String>> nameToPathsMap;

    protected EditBox searchBox;
    protected ClassificationListWidget classificationListWidget;
    protected static final Component SEARCH_LABEL = Component.translatable("exmagicsys.gui.select_spell.search");
    private static final Component CLEAR_FILTERS_TEXT = Component.translatable("exmagicsys.gui.clear_filters");
    private boolean ignoreNextChar = true;

    public SpellClassificationScreen(Screen parent) {
        super(Component.translatable("exmagicsys.gui.select_classification.title"));
        this.parent = parent;

        Registry<SpellClassification> classificationRegistry = Minecraft.getInstance().player.level().registryAccess()
                .registryOrThrow(EMSRegistries.CLASSIFICATION_REGISTRY_KEY);
        this.uniqueClassificationButtons = new LinkedHashMap<>();
        this.nameToPathsMap = new LinkedHashMap<>();
        String nonePath = EMSRegistries.CLASSIFICATION_NONE.getId().getPath();

        for (var entry : classificationRegistry.entrySet()) {
            String path = entry.getKey().location().getPath();
            if (path.equals(nonePath)) continue;
            Component description = entry.getValue().description();
            String translatedName = description.getString();
            this.uniqueClassificationButtons.putIfAbsent(translatedName, description);
            this.nameToPathsMap.computeIfAbsent(translatedName, k -> new HashSet<>()).add(path);
        }
        this.toggledFilters = new HashSet<>(SpellSelectionScreen.activeFilters);
    }

    @Override
    protected void init() {
        super.init();

        this.searchBox = new EditBox(this.font, this.searchBoxX, this.searchBoxY, this.searchBoxWidth, this.searchBoxHeight, SEARCH_LABEL);
        this.searchBox.setResponder(this::onSearchBoxChanged);
        this.addRenderableWidget(this.searchBox);
        this.setFocused(this.searchBox);

        int listX = (this.width - this.listWidth) / 2;
        int listHeight = this.height - this.listTop - this.listBottomPadding;
        this.classificationListWidget = new ClassificationListWidget(listX, this.listTop, this.listWidth, listHeight);
        this.addRenderableWidget(this.classificationListWidget);

        int bottomY = this.height - this.applyButtonYPadding;
        int applyX = this.width / 2 - (this.applyButtonWidth + 2);
        int cancelX = this.width / 2 + 2;

        int clearButtonY = bottomY - this.applyButtonHeight - 5;
        int clearButtonX = this.width / 2 - this.applyButtonWidth / 2;

        this.addRenderableWidget(
                Button.builder(CLEAR_FILTERS_TEXT, (button) -> {
                    this.toggledFilters.clear();
                    this.rebuildClassificationButtons();
                }).bounds(clearButtonX, clearButtonY, this.applyButtonWidth, this.applyButtonHeight).build()
        );

        this.addRenderableWidget(
                Button.builder(Component.translatable("exmagicsys.gui.apply"), (button) -> {
                    SpellSelectionScreen.activeFilters = new HashSet<>(this.toggledFilters);
                    this.minecraft.setScreen(this.parent);
                }).bounds(applyX, bottomY, this.applyButtonWidth, this.applyButtonHeight).build()
        );
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.cancel"), (button) -> {
                    this.minecraft.setScreen(this.parent);
                }).bounds(cancelX, bottomY, this.applyButtonWidth, this.applyButtonHeight).build()
        );

        this.rebuildClassificationButtons();
    }

    private void onSearchBoxChanged(String searchText) { this.rebuildClassificationButtons(); }
    private void rebuildClassificationButtons() {
        List<ClassificationEntry> newEntries = new ArrayList<>();
        String searchText = this.searchBox.getValue().toLowerCase().trim();

        this.classificationListWidget.updateEntries(new ArrayList<>());

        for (String translatedName : this.uniqueClassificationButtons.keySet()) {
            if (translatedName.toLowerCase().contains(searchText)) {
                newEntries.add(new ClassificationEntry(translatedName));
            }
        }
        this.classificationListWidget.updateEntries(newEntries);
    }
    private Component getButtonComponent(Set<String> associatedPaths, Component className) {
        if (this.toggledFilters.containsAll(associatedPaths)) {
            return Component.translatable("exmagicsys.gui.classification.enabled", className);
        } else {
            return Component.translatable("exmagicsys.gui.classification.disabled", className);
        }
    }
    @Override public boolean charTyped(char codePoint, int modifiers) {
        if (this.ignoreNextChar) { this.ignoreNextChar = false; return true; }
        if (this.searchBox.charTyped(codePoint, modifiers)) { return true; }
        return super.charTyped(codePoint, modifiers);
    }
    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (this.classificationListWidget.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.classificationListWidget.render(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.titleY, 0xFFFFFF);
        guiGraphics.drawString(this.font, SEARCH_LABEL, this.searchLabelX, this.searchLabelY, 0xA0A0A0);
    }
    @Override public boolean isPauseScreen() { return false; }
    @Override public void onClose() { this.minecraft.setScreen(this.parent); }

    protected class ClassificationListWidget extends ContainerObjectSelectionList<ClassificationEntry> {
        public ClassificationListWidget(int x, int y, int width, int height) {
            super(SpellClassificationScreen.this.minecraft, width, height, y, SpellClassificationScreen.this.listEntryHeight);
            this.setX(x);
            this.setRenderHeader(false, 0);
        }
        public void updateEntries(List<ClassificationEntry> entries) {
            this.clearEntries();
            for(ClassificationEntry entry : entries) {
                this.addEntry(entry);
            }
        }
        @Override public int getScrollbarPosition() { return this.getX() + this.width - 6; }
    }

    protected class ClassificationEntry extends ContainerObjectSelectionList.Entry<ClassificationEntry> {
        private final Button button;
        public ClassificationEntry(String buttonName) {
            Component className = uniqueClassificationButtons.get(buttonName);
            Set<String> associatedPaths = nameToPathsMap.get(buttonName);
            this.button = Button.builder(getButtonComponent(associatedPaths, className), (b) -> {
                boolean allEnabled = toggledFilters.containsAll(associatedPaths);
                if (allEnabled) {
                    toggledFilters.removeAll(associatedPaths);
                } else {
                    toggledFilters.addAll(associatedPaths);
                }
                rebuildClassificationButtons();
            }).bounds(0, 0, 150, 20).build();
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            this.button.setX(SpellClassificationScreen.this.width / 2 - this.button.getWidth() / 2);
            this.button.setY(top);
            this.button.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        @Override public List<? extends GuiEventListener> children() { return List.of(this.button); }
        @Override public List<? extends NarratableEntry> narratables() { return List.of(this.button); }
    }
}