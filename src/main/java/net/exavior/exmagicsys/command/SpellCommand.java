package net.exavior.exmagicsys.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.exavior.exmagicsys.api.EMSMagicApi;
import net.exavior.exmagicsys.api.spell.Spell;
import net.exavior.exmagicsys.registry.EMSRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class SpellCommand {
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_SPELL = new DynamicCommandExceptionType(
            (spellId) -> Component.translatable("exmagicsys.command.error.unknown_spell", spellId.toString())
    );
    private static final DynamicCommandExceptionType ERROR_SPELL_UNLEARNABLE = new DynamicCommandExceptionType(
            (spellId) -> Component.translatable("exmagicsys.command.error.unlearnable", spellId.toString())
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("spell")
                        .requires(source -> source.hasPermission(2))

                        .then(Commands.literal("learn")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("spell", ResourceLocationArgument.id())
                                                .suggests((context, builder) -> {
                                                    Registry<Spell> spellRegistry = context.getSource().registryAccess()
                                                            .registryOrThrow(EMSRegistries.SPELL_REGISTRY_KEY);
                                                    return net.minecraft.commands.SharedSuggestionProvider.suggest(
                                                            spellRegistry.keySet().stream().map(ResourceLocation::toString),
                                                            builder
                                                    );
                                                })
                                                .executes(context -> learnSpell(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        ResourceLocationArgument.getId(context, "spell")
                                                ))
                                        )
                                )
                        )

                        .then(Commands.literal("unlearn")
                                .then(Commands.argument("targets", EntityArgument.players())

                                        .then(Commands.literal("all")
                                                .executes(context -> unlearnAllSpells(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets")
                                                ))
                                        )

                                        .then(Commands.argument("spell", ResourceLocationArgument.id())
                                                .suggests((context, builder) -> {
                                                    Registry<Spell> spellRegistry = context.getSource().registryAccess()
                                                            .registryOrThrow(EMSRegistries.SPELL_REGISTRY_KEY);
                                                    return net.minecraft.commands.SharedSuggestionProvider.suggest(
                                                            spellRegistry.keySet().stream().map(ResourceLocation::toString),
                                                            builder
                                                    );
                                                })
                                                .executes(context -> unlearnSpell(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        ResourceLocationArgument.getId(context, "spell")
                                                ))
                                        )
                                )
                        )
        );
    }

    private static int learnSpell(CommandSourceStack source, Collection<ServerPlayer> targets, ResourceLocation spellId) throws CommandSyntaxException {
        Spell spell = source.registryAccess()
                .registryOrThrow(EMSRegistries.SPELL_REGISTRY_KEY)
                .get(spellId);

        if (spell == null) {
            throw ERROR_UNKNOWN_SPELL.create(spellId);
        }

        if (spell.isUnlearnable()) {
            throw ERROR_SPELL_UNLEARNABLE.create(spellId);
        }

        Component spellName = Component.translatable("spell." + spellId.getNamespace() + "." + spellId.getPath());
        int successCount = (int) targets.stream().filter(player -> EMSMagicApi.learnSpell(player, spellId)).count();

        if (successCount == 1) {
            source.sendSuccess(() -> Component.translatable("exmagicsys.command.learn.success.single", spellName, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("exmagicsys.command.learn.success.multiple", spellName, successCount), true);
        }
        return successCount;
    }

    private static int unlearnSpell(CommandSourceStack source, Collection<ServerPlayer> targets, ResourceLocation spellId) {
        Component spellName = Component.translatable("spell." + spellId.getNamespace() + "." + spellId.getPath());
        int successCount = (int) targets.stream().filter(player -> EMSMagicApi.unlearnSpell(player, spellId)).count();

        if (successCount == 0) {
            source.sendFailure(Component.translatable("exmagicsys.command.unlearn.failure", spellName, targets.size()));
            return 0;
        }

        if (successCount == 1) {
            source.sendSuccess(() -> Component.translatable("exmagicsys.command.unlearn.success.single", spellName, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("exmagicsys.command.unlearn.success.multiple", spellName, successCount), true);
        }
        return successCount;
    }

    private static int unlearnAllSpells(CommandSourceStack source, Collection<ServerPlayer> targets) {
        for (ServerPlayer player : targets) {
            EMSMagicApi.unlearnAllSpells(player);
        }

        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable("exmagicsys.command.unlearn_all.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("exmagicsys.command.unlearn_all.success.multiple", targets.size()), true);
        }
        return targets.size();
    }
}