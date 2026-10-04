package net.happyspeed.iframestweaker.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.happyspeed.iframestweaker.config.ModConfigs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Objects;

@Mixin(value = LivingEntity.class, priority = 1400)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@ModifyConstant(method = "hurtServer", constant = @Constant(intValue = 20, ordinal = 0))
	public int changeIFrames(int constant, @Local(ordinal = 0) final DamageSource source, @Local(ordinal = 0) final float amount) {
		Entity entity2 = source.getEntity();
		int invulnerableTime;
		if (source.is(DamageTypes.PLAYER_ATTACK) && entity2 instanceof Player player) {
			int base = (int) player.getCurrentItemAttackStrengthDelay();
			if (base > 0) {
				int base_half = Math.round((float) base / 2);
				invulnerableTime = Math.min(ModConfigs.MAXIFRAMES, Math.max(ModConfigs.MINIFRAMES, (int) (base_half + (Math.round(base * ModConfigs.IFRAMESFACTOR)))));
			} else {
				invulnerableTime = 10;
			}
		} else if (source.is(DamageTypeTags.IS_PROJECTILE)) {
			invulnerableTime = ModConfigs.PROJECTILEIFRAMES;
		} else if (source.is(DamageTypeTags.IS_FALL)) {
			invulnerableTime = ModConfigs.FALLIFRAMES;
		} else if (source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
			invulnerableTime = 0;
		} else if (source.is(DamageTypeTags.IS_FIRE)) {
			invulnerableTime = ModConfigs.FIREIFRAMES;
		} else if (source.is(DamageTypes.INDIRECT_MAGIC)) {
			invulnerableTime = ModConfigs.INDIRECTMAGICIFRAMES;
		} else if (source.is(DamageTypes.MAGIC)) {
			invulnerableTime = ModConfigs.DIRECTMAGICIFRAMES;
		} else if (Objects.equals(source.type().msgId(), "slashdamage")) {
			invulnerableTime = 5;
		} else {
			invulnerableTime = ModConfigs.MISCIFRAMES;
		}
		return invulnerableTime;
	}

	@ModifyExpressionValue(method = "hurtServer", at = @At(value = "CONSTANT", args = "floatValue=10.0F", ordinal = 0))
	public float changeIFrames(float constant) {
		return constant - 10;
	}
}