package io.snw.entityapi.entity;

import io.snw.entityapi.api.ControllableEntity;
import io.snw.entityapi.api.EntitySound;
import io.snw.entityapi.api.mind.attribute.Attribute;
import io.snw.entityapi.api.mind.attribute.RideAttribute;
import io.snw.entityapi.api.ControllableEntityHandle;
import net.minecraft.server.v1_7_R1.*;
import org.bukkit.craftbukkit.v1_7_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class ControllableCreeperEntity extends EntityCreeper implements ControllableEntityHandle {

    private final ControllableEntity controllableEntity;

    public ControllableCreeperEntity(World world, ControllableEntity controllableEntity) {
        super(world);
        this.controllableEntity = controllableEntity;
        if (this.controllableEntity instanceof ControllableBaseEntity) {
            ((ControllableBaseEntity) this.controllableEntity).clearNMSGoals(new PathfinderGoalSelector[]{this.goalSelector, this.targetSelector});
        }
    }

    public ControllableEntity getControllableEntity() {
        return this.controllableEntity;
    }

    // EntityInsentient - Most importantly stops NMS goal selectors from ticking
    @Override
    protected void bn() {
        ++this.aV;

        this.w();

        this.getEntitySenses().a();

        //this.targetSelector.a();
        //this.goalSelector.a();

        this.getNavigation().f();

        this.bp();

        this.getControllerMove().c();
        this.getControllerLook().a();
        this.getControllerJump().b();
    }

    @Override
    public void h() {
        super.h();
        if (this.controllableEntity != null) {
            this.controllableEntity.onTick();
            if (this.controllableEntity.shouldUpdateAttributes()) {
                this.controllableEntity.getMind().tick();
            }
        }
    }

    @Override
    public void collide(Entity entity) {
        if (this.controllableEntity == null) {
            super.collide(entity);
            return;
        }

        if (this.controllableEntity.onCollide(entity.getBukkitEntity())) {
            super.collide(entity);
        }
    }

    @Override
    public boolean a(EntityHuman entity) {
        if (this.controllableEntity == null || !(entity.getBukkitEntity() instanceof Player)) {
            return super.c(entity);
        }

        return controllableEntity.onInteract((Player) entity.getBukkitEntity(), true);
    }

    @Override
    public boolean damageEntity(DamageSource damageSource, float v) {
        if (this.controllableEntity != null && damageSource.getEntity() != null && damageSource.getEntity().getBukkitEntity() instanceof Player) {
            this.controllableEntity.onInteract((Player) damageSource.getEntity(), false);
        }
        return super.damageEntity(damageSource, v);
    }

    @Override
    public void e(float xMotion, float zMotion) {
        float[] motion = new float[]{xMotion, (float) this.motY, zMotion};
        if (this.controllableEntity != null) {
            if (this.controllableEntity.getMind().hasAttribute("RIDE")) {
                Attribute b = this.controllableEntity.getMind().getAttribute("RIDE");
                if (b instanceof RideAttribute) {
                    ((RideAttribute) b).onRide(motion);
                }
            }
        }
        this.motY = motion[1];
        super.e(motion[0], motion[2]);
    }

    @Override
    public void die(DamageSource damagesource) {
        if (this.controllableEntity != null) {
            this.controllableEntity.onDeath();
        }
        super.die(damagesource);
    }

    @Override
    public org.bukkit.Material getDefaultMaterialLoot() {
        return CraftMagicNumbers.getMaterial(this.getLoot());
    }

    @Override
    protected Item getLoot() {
        org.bukkit.Material lootMaterial = this.controllableEntity.getLoot();
        return this.controllableEntity == null ? super.getLoot() : lootMaterial == null ? super.getLoot() : CraftMagicNumbers.getItem(lootMaterial);
    }

    @Override
    protected String t() {
        return this.controllableEntity == null ? "mob.creeper.say" : this.controllableEntity.getSound(EntitySound.IDLE);
    }

    @Override
    protected String aU() {
        return this.controllableEntity == null ? "mob.creeper.death" : this.controllableEntity.getSound(EntitySound.DEATH);
    }

    protected void explode(int modifier) {
        if (!this.world.isStatic) {
            boolean flag = this.world.getGameRules().getBoolean("mobGriefing");

            float radius = this.isPowered() ? 6.0F : 3.0F;

            ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), radius, false);
            this.world.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.world.createExplosion(this, this.locX, this.locY, this.locZ, event.getRadius() * modifier, event.getFire(), flag);
                this.die();
            } else {
                //this.fuseTicks = 0;
            }
        }
    }
}