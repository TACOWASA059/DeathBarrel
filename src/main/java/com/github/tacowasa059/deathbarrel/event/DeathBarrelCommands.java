package com.github.tacowasa059.deathbarrel.event;


import com.github.tacowasa059.deathbarrel.Config;
import com.github.tacowasa059.deathbarrel.DeathBarrel;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = DeathBarrel.MODID)
public class DeathBarrelCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("deathbarrel")
                .requires(source -> source.hasPermission(2)) // OP権限のみ
                .then(Commands.argument("key", StringArgumentType.string())
                        .suggests(CONFIG_KEY_SUGGESTION)
                        .executes(context -> {
                            String key = StringArgumentType.getString(context, "key");
                            CommandSourceStack source = context.getSource();
                            String value = getConfigValue(key);
                            if (value != null) {
                                source.sendSuccess(() -> Component.literal(ChatFormatting.GREEN + key + ": "+ ChatFormatting.AQUA + value), false);
                            } else {
                                source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid key: " + key));
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("value", StringArgumentType.string())
                                .suggests(CONFIG_VALUE_SUGGESTION)
                                .executes(context -> {
                                    String key = StringArgumentType.getString(context, "key");
                                    String value = StringArgumentType.getString(context, "value");
                                    CommandSourceStack source = context.getSource();
                                    if (setConfigValue(key, value)) {
                                        source.sendSuccess(() -> Component.literal(ChatFormatting.GREEN + "Updated " + key + " to " + ChatFormatting.AQUA + value), true);
                                    } else {
                                        source.sendFailure(Component.literal(ChatFormatting.RED + "Invalid key or value: " + key + " = " + value));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }

    private static final List<String> CONFIG_KEYS = List.of(
            "hasSkull", "giveJournal", "lockChest", "journalPos",
            "timeToErase", "EmptyErase", "Breakable", "dropItems"
    );

    private static final SuggestionProvider<CommandSourceStack> CONFIG_KEY_SUGGESTION = (context, builder) -> net.minecraft.commands.SharedSuggestionProvider.suggest(CONFIG_KEYS, builder);

    private static final SuggestionProvider<CommandSourceStack> CONFIG_VALUE_SUGGESTION = (context, builder) -> {
        try {
            String key = context.getArgument("key", String.class);
            if (List.of("hasSkull", "giveJournal", "lockChest", "journalPos", "EmptyErase", "Breakable", "dropItems").contains(key)) {
                return net.minecraft.commands.SharedSuggestionProvider.suggest(List.of("true", "false"), builder);
            } else if ("timeToErase".equals(key)) {
                return net.minecraft.commands.SharedSuggestionProvider.suggest(List.of("60", "120" , "300", "600", "1200", "-1"), builder);
            }
        } catch (IllegalArgumentException e) {
            return builder.buildFuture();
        }
        return builder.buildFuture();
    };


    private static String getConfigValue(String key) {
        return switch (key) {
            case "hasSkull" -> String.valueOf(Config.hasSkull.get());
            case "giveJournal" -> String.valueOf(Config.giveJournal.get());
            case "lockChest" -> String.valueOf(Config.lockChest.get());
            case "journalPos" -> String.valueOf(Config.journalPos.get());
            case "timeToErase" -> String.valueOf(Config.timeToErase.get());
            case "EmptyErase" -> String.valueOf(Config.EmptyErase.get());
            case "Breakable" -> String.valueOf(Config.Breakable.get());
            case "dropItems" -> String.valueOf(Config.dropItems.get());
            default -> null;
        };
    }

    private static boolean setConfigValue(String key, String value) {
        try {
            switch (key) {
                case "hasSkull" -> Config.hasSkull.set(Boolean.parseBoolean(value));
                case "giveJournal" -> Config.giveJournal.set(Boolean.parseBoolean(value));
                case "lockChest" -> Config.lockChest.set(Boolean.parseBoolean(value));
                case "journalPos" -> Config.journalPos.set(Boolean.parseBoolean(value));
                case "timeToErase" -> Config.timeToErase.set(Integer.parseInt(value));
                case "EmptyErase" -> Config.EmptyErase.set(Boolean.parseBoolean(value));
                case "Breakable" -> Config.Breakable.set(Boolean.parseBoolean(value));
                case "dropItems" -> Config.dropItems.set(Boolean.parseBoolean(value));
                default -> {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

