package lee.code.chunks.lists.chunksettings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.EntityType;

@AllArgsConstructor
public enum HostileEntities {

    CAVE_SPIDER(EntityType.CAVE_SPIDER),
    ENDERMAN(EntityType.ENDERMAN),
    PIGLIN(EntityType.PIGLIN),
    PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE),
    ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN),
    SPIDER(EntityType.SPIDER),
    BLAZE(EntityType.BLAZE),
    CREEPER(EntityType.CREEPER),
    DROWNED(EntityType.DROWNED),
    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN),
    ENDERMITE(EntityType.ENDERMITE),
    EVOKER(EntityType.EVOKER),
    GHAST(EntityType.GHAST),
    GUARDIAN(EntityType.GUARDIAN),
    HOGLIN(EntityType.HOGLIN),
    HUSK(EntityType.HUSK),
    MAGMA_CUBE(EntityType.MAGMA_CUBE),
    PHANTOM(EntityType.PHANTOM),
    PILLAGER(EntityType.PILLAGER),
    RAVAGER(EntityType.RAVAGER),
    SHULKER(EntityType.SHULKER),
    SILVERFISH(EntityType.SILVERFISH),
    SKELETON(EntityType.SKELETON),
    SLIME(EntityType.SLIME),
    STRAY(EntityType.STRAY),
    VEX(EntityType.VEX),
    VINDICATOR(EntityType.VINDICATOR),
    WITCH(EntityType.WITCH),
    WITHER_SKELETON(EntityType.WITHER_SKELETON),
    ZOGLIN(EntityType.ZOGLIN),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER),
    ZOMBIE(EntityType.ZOMBIE),
    ENDER_DRAGON(EntityType.ENDER_DRAGON),
    WITHER(EntityType.WITHER),
    GIANT(EntityType.GIANT),

    ;

    @Getter private final EntityType type;
}
