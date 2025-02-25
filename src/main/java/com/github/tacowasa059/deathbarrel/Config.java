/*
 * このファイルは Death Chest Mod (LGPL 2.1) を基に改変されています。
 * 改変者: tacowasa059
 * 改変日: 2025/02/25
 * 変更内容: その他コンフィグの追加
 */
package com.github.tacowasa059.deathbarrel;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;



@Mod.EventBusSubscriber(modid = DeathBarrel.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue hasSkull;
    public static ForgeConfigSpec.BooleanValue lockChest;
    public static ForgeConfigSpec.BooleanValue giveJournal;
    public static ForgeConfigSpec.BooleanValue journalPos;

    public static ForgeConfigSpec.IntValue timeToErase;

    public static ForgeConfigSpec.BooleanValue EmptyErase;

    public static ForgeConfigSpec.BooleanValue Breakable;

    public static ForgeConfigSpec.BooleanValue dropItems;

    public static ForgeConfigSpec.ConfigValue<List<String>> BLACKLIST_ITEMS;

    static {
        builder.push("general");
        Config.hasSkull = builder.comment("Whether a death barrel spawns with a skull above it.\n"
                        + "death barrelの上にプレイヤーヘッドを生成するかどうか。")
                .define("hasSkull", false);
        Config.giveJournal = builder.comment("Whether players should receive a journal of their death.\n"
                        + "プレイヤーが死亡した時に本を受け取るかどうか。")
                .define("giveJournal", false);
        Config.lockChest = builder.comment("Whether death barrels should be locked so that only their owner can open them.\n"
                        + "death barrelを所有者のみが開けられるようにロックするかどうか。")
                .define("lockChest", false);
        Config.journalPos = builder.comment("Whether the journal shows the death position.\n"
                        + "Only works if giveJournal is set to true.\n"
                        + "本に死亡位置を表示するかどうか。\n"
                        + "giveJournal が true の場合のみ機能する。")
                .define("journalPos", false);
        Config.timeToErase = builder.comment("Number of seconds until the death barrel is deleted.\n"
                        + "death barrelが削除されるまでの秒数。 -1のとき、無効化される。")
                .defineInRange("timeToErase", 300, -1, Integer.MAX_VALUE);
        Config.EmptyErase = builder.comment("Erase death barrels when all slots are empty.\n"
                        + "スロットがすべて空の場合にdeath barrelを削除する。")
                .define("EmptyErase", true);
        Config.Breakable = builder.comment("Whether death barrels can be broken by players.\n"
                        + "death barrelをプレイヤーが破壊可能にするかどうか。")
                .define("Breakable", false);
        Config.dropItems = builder.comment("When Breakable is true, whether items should drop upon breaking.\n"
                        + "Breakable が true の場合、death barrelを破壊したときに、中のアイテムをドロップするかどうか。")
                .define("dropItems", false);

        BLACKLIST_ITEMS = builder.comment("List of blacklisted item IDs (e.g., 'minecraft:diamond', 'modid:custom_sword')\n"+
                        "ブラックリストのアイテムIDリスト (例) 'minecraft:diamond', 'modid:custom_sword'")
                .define("blacklistItems", new ArrayList<>(List.of(
                        "minecraft:barrier"
                )));

        builder.pop();
        SPEC = builder.build();
    }
}

