package net.exavior.exmagicsys.keymap;

import com.mojang.blaze3d.platform.InputConstants;
import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod(value = ExaviorMagicSystem.MODID, dist = Dist.CLIENT)
public class EMSClientKeyMaps {

    public static final String KEY_CATEGORY_MAGIC = "key.category." + ExaviorMagicSystem.MODID;
    public static final String KEY_CAST_SPELL = "key." + ExaviorMagicSystem.MODID + ".cast_spell";
    public static final String KEY_OPEN_GUI = "key." + ExaviorMagicSystem.MODID + ".open_gui";
    public static final String KEY_CYCLE_SPELL = "key." + ExaviorMagicSystem.MODID + ".cycle_spell";

    public static final KeyMapping CAST_SPELL_KEY = new KeyMapping(
            KEY_CAST_SPELL,
            InputConstants.Type.KEYSYM, // Default mapping is on the keyboard
            GLFW.GLFW_KEY_R, // Default key is R
            KEY_CATEGORY_MAGIC
    );

    public static final KeyMapping OPEN_SPELL_GUI_KEY = new KeyMapping(
            KEY_OPEN_GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X, // Default key is X
            KEY_CATEGORY_MAGIC
    );

    public static final KeyMapping CYCLE_SPELL_KEY = new KeyMapping(
            KEY_CYCLE_SPELL,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C, // Default key is C
            KEY_CATEGORY_MAGIC
    );

}
