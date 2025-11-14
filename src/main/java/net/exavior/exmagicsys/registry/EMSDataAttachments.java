package net.exavior.exmagicsys.registry;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import net.exavior.exmagicsys.ExaviorMagicSystem;
import net.exavior.exmagicsys.api.spell.SpellArm;
import net.exavior.exmagicsys.data.CastingState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EMSDataAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ExaviorMagicSystem.MODID);

    private static final StreamCodec<RegistryFriendlyByteBuf, Integer> INT_STREAM_CODEC = new StreamCodec<>() {
        @Override public Integer decode(RegistryFriendlyByteBuf buf) { return ByteBufCodecs.VAR_INT.decode(buf); }
        @Override public void encode(RegistryFriendlyByteBuf buf, Integer value) { ByteBufCodecs.VAR_INT.encode(buf, value); }
    };

    private static final StreamCodec<RegistryFriendlyByteBuf, Long> LONG_STREAM_CODEC = new StreamCodec<>() {
        @Override public Long decode(RegistryFriendlyByteBuf buf) { return ByteBufCodecs.VAR_LONG.decode(buf); }
        @Override public void encode(RegistryFriendlyByteBuf buf, Long value) { ByteBufCodecs.VAR_LONG.encode(buf, value); }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> REGISTRY_RL_STREAM_CODEC = new StreamCodec<>() {
        @Override public ResourceLocation decode(RegistryFriendlyByteBuf buf) { return ResourceLocation.STREAM_CODEC.decode(buf); }
        @Override public void encode(RegistryFriendlyByteBuf buf, ResourceLocation value) { ResourceLocation.STREAM_CODEC.encode(buf, value); }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> NULLABLE_RL_STREAM_CODEC =
            ByteBufCodecs.optional(REGISTRY_RL_STREAM_CODEC)
                    .map(opt -> opt.orElse(null), Optional::ofNullable);

    private static final StreamCodec<RegistryFriendlyByteBuf, Set<ResourceLocation>> SET_STREAM_CODEC =
            ByteBufCodecs.collection(ArrayList::new, REGISTRY_RL_STREAM_CODEC)
                    .map(HashSet::new, ArrayList::new);

    private static final StreamCodec<RegistryFriendlyByteBuf, List<ResourceLocation>> SPELL_LIST_STREAM_CODEC =
            ByteBufCodecs.collection(ArrayList::new, NULLABLE_RL_STREAM_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Long>> COOLDOWN_MAP_STREAM_CODEC =
            ByteBufCodecs.map(HashMap::new, REGISTRY_RL_STREAM_CODEC, LONG_STREAM_CODEC);

    private static final Codec<Either<ResourceLocation, Unit>> SAFE_NULLABLE_RL_CODEC =
            Codec.either(ResourceLocation.CODEC, Codec.unit(() -> Unit.INSTANCE));

    private static final Codec<List<Either<ResourceLocation, Unit>>> SAFE_NULLABLE_RL_LIST_CODEC =
            Codec.list(SAFE_NULLABLE_RL_CODEC);

    private static final Codec<Optional<ResourceLocation>> OPTIONAL_RL_CODEC =
            Codec.either(ResourceLocation.CODEC, Codec.unit(() -> Unit.INSTANCE))
                    .xmap(
                            either -> either.left(),
                            opt -> opt.map(rl -> Either.<ResourceLocation, Unit>left(rl))
                                    .orElse(Either.<ResourceLocation, Unit>right(Unit.INSTANCE))
                    );

    public static final Supplier<AttachmentType<Integer>> MANA_VALUE = ATTACHMENT_TYPES.register(
            "mana_value", () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT).sync(INT_STREAM_CODEC).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<Integer>> MAX_MANA_VALUE = ATTACHMENT_TYPES.register(
            "max_mana_value", () -> AttachmentType.builder(() -> 100)
                    .serialize(Codec.INT).sync(INT_STREAM_CODEC).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<Set<ResourceLocation>>> KNOWN_SPELLS = ATTACHMENT_TYPES.register(
            "known_spells", () -> AttachmentType.builder(
                            (Supplier<Set<ResourceLocation>>) () -> new HashSet<>()
                    )
                    .serialize(Codec.list(ResourceLocation.CODEC).xmap(
                            (List<ResourceLocation> list) -> new HashSet<>(list),
                            (Set<ResourceLocation> set) -> new ArrayList<>(set)
                    ))
                    .sync(SET_STREAM_CODEC)
                    .copyOnDeath()
                    .build()
    );

    public static final Supplier<AttachmentType<Map<ResourceLocation, Long>>> SPELL_COOLDOWNS = ATTACHMENT_TYPES.register(
            "spell_cooldowns", () -> AttachmentType.builder(
                            (Supplier<Map<ResourceLocation, Long>>) () -> new HashMap<>()
                    )
                    .serialize((Codec<Map<ResourceLocation, Long>>) Codec.unboundedMap(ResourceLocation.CODEC, Codec.LONG))
                    .sync(COOLDOWN_MAP_STREAM_CODEC)
                    .build()
    );

    public static final Supplier<AttachmentType<CastingState>> CASTING_STATE = ATTACHMENT_TYPES.register(
            "casting_state", () -> AttachmentType.builder(() -> CastingState.NONE).build()
    );

    public static final Supplier<AttachmentType<Boolean>> IS_CAST_KEY_HELD = ATTACHMENT_TYPES.register(
            "is_cast_key_held", () -> AttachmentType.builder(() -> false).build()
    );

    public static final Supplier<AttachmentType<List<ResourceLocation>>> EQUIPPED_SPELLS = ATTACHMENT_TYPES.register(
            "equipped_spells", () -> AttachmentType.builder(
                            (Supplier<List<ResourceLocation>>) () -> new ArrayList<>(Collections.nCopies(4, null))
                    )
                    .serialize(SAFE_NULLABLE_RL_LIST_CODEC.xmap(
                            (List<Either<ResourceLocation, Unit>> list) -> {
                                List<ResourceLocation> fixedList = new ArrayList<>(Collections.nCopies(4, null));
                                for(int i = 0; i < Math.min(list.size(), 4); i++) {
                                    fixedList.set(i, list.get(i).left().orElse(null));
                                }
                                return fixedList;
                            },
                            (List<ResourceLocation> list) -> list.stream()
                                    .map(rl -> (rl == null) ? Either.<ResourceLocation, Unit>right(Unit.INSTANCE) : Either.<ResourceLocation, Unit>left(rl))
                                    .collect(Collectors.toCollection(ArrayList::new))
                    ))
                    .sync(SPELL_LIST_STREAM_CODEC)
                    .copyOnDeath()
                    .build()
    );

    public static final Supplier<AttachmentType<Integer>> ACTIVE_SPELL_SLOT = ATTACHMENT_TYPES.register(
            "active_spell_slot", () -> AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT)
                    .sync(INT_STREAM_CODEC)
                    .copyOnDeath()
                    .build()
    );

    public static final Supplier<AttachmentType<HumanoidModel.ArmPose>> CLIENT_SPELL_ARM_POSE = ATTACHMENT_TYPES.register(
            "client_spell_arm_pose", () -> AttachmentType.builder(() -> HumanoidModel.ArmPose.EMPTY).build()
    );

    public static final Supplier<AttachmentType<SpellArm>> CLIENT_SPELL_ARM = ATTACHMENT_TYPES.register(
            "client_spell_arm", () -> AttachmentType.builder(() -> SpellArm.MAIN_HAND).build()
    );

    public static final Supplier<AttachmentType<Long>> MANA_REGEN_COOLDOWN_UNTIL = ATTACHMENT_TYPES.register(
            "mana_regen_cooldown_until", () -> AttachmentType.builder(() -> 0L)
                    .serialize(Codec.LONG)
                    .build()
    );

    public static final Supplier<AttachmentType<Long>> COMBAT_COOLDOWN_UNTIL = ATTACHMENT_TYPES.register(
            "combat_cooldown_until", () -> AttachmentType.builder(() -> 0L)
                    .serialize(Codec.LONG)
                    .sync(LONG_STREAM_CODEC)
                    .build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}