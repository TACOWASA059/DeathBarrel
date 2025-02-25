# DeathBarrelMod
Forge1.20.1

サーバー・クライアントmod

## 概要
プレイヤーが死亡すると樽にアイテムが入るmod

## コンフィグ
| 設定キー	        | デフォルト値	 | 説明                                           |
|--------------|---------|----------------------------------------------|
| hasSkull	    | false	  | 樽の上にプレイヤーヘッドを生成するか                       |
| giveJournal  | false	  | プレイヤー死亡時に本を受け取るか                         |
| lockChest	   | false	  | 樽を所有者のみが開けられるようにするか                      |
| journalPos	  | false	  | 本に死亡位置を記録するか（giveJournal が true の場合のみ有効） |
| timeToErase	 | 300     | 樽が削除されるまでの秒数（-1 で無効化）                    |
| EmptyErase	  | true    | すべてのスロットが空になった場合に樽を削除するか                 |
| Breakable	   | false   | 樽をプレイヤーが破壊できるか                           |
| dropItems	   | false   | 破壊時に中のアイテムをドロップするか（Breakable が true の場合のみ有効） |
## コマンド
### コンフィグ設定値を取得
```
/deathbarrel <key>
```
### コンフィグ設定値を変更
```
/deathbarrel <key> <value>
```
例)
```
/deathbarrel timeToErase 600
```

## ライセンス
このMODの一部の実装（`Config.java`, `DeathBarrelEvent.java`）は  
[Death Chest Mod](https://github.com/SmileycorpMC/death-chest/tree/1.19) のコードを基に改変されています。

元の `death chest mod` は **GNU Lesser General Public License v2.1 (LGPL 2.1)** の下でライセンスされています。  
そのため、**これらの改変ファイル（`Config.java`, `DeathBarrelEvent.java`）も LGPL 2.1 に従います。**

**LGPL 2.1 の全文は `LICENSE` ファイルに含まれています。**

本MODのその他の部分についても、**LGPL 2.1 に従います。**  
ただし、LGPL 2.1 に基づくコードを含むため、本MODを再配布する際は LGPL 2.1 の要件に従う必要があります。

---

## License
Some parts of this mod's implementation (`Config.java`, `DeathBarrelEvent.java`)  
are modified from the code of [Death Chest Mod](https://github.com/SmileycorpMC/death-chest/tree/1.19).

The original *Death Chest Mod* is licensed under the **GNU Lesser General Public License v2.1 (LGPL 2.1)**.  
Therefore, **these modified files (`Config.java`, `DeathBarrelEvent.java`) also comply with LGPL 2.1**.

**The full text of LGPL 2.1 is included in the `LICENSE` file.**

All other parts of this mod are also licensed under **LGPL 2.1**.  
Since this mod includes code licensed under LGPL 2.1, any redistribution of this mod must comply with the requirements of LGPL 2.1.
