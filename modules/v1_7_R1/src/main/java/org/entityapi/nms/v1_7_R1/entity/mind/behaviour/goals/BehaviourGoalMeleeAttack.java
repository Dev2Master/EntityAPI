/*
 * Copyright (C) EntityAPI Team
 *
 * This file is part of EntityAPI.
 *
 * EntityAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EntityAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EntityAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.entityapi.nms.v1_7_R1.entity.mind.behaviour.goals;

import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.PathEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.entityapi.api.entity.ControllableEntity;
import org.entityapi.api.entity.mind.behaviour.BehaviourType;
import org.entityapi.api.utils.EntityUtil;
import org.entityapi.nms.v1_7_R1.NMSEntityUtil;
import org.entityapi.nms.v1_7_R1.entity.mind.behaviour.BehaviourGoalBase;

public class BehaviourGoalMeleeAttack extends BehaviourGoalBase {

    private int attackTicks;
    private boolean ignoreSight;
    private PathEntity pathToAttack;
    private Class<? extends Entity> typeToAttack;
    private int moveTicks;
    private double targetX;
    private double targetY;
    private double targetZ;
    private double navigationSpeed;

    public BehaviourGoalMeleeAttack(ControllableEntity controllableEntity, Class<? extends org.bukkit.entity.Entity> typeToAttack, boolean ignoreSight, double navigationSpeed) {
        super(controllableEntity);
        this.ignoreSight = ignoreSight;
        this.typeToAttack = (Class<? extends Entity>) EntityUtil.getNmsClassFor(typeToAttack);
        if (this.typeToAttack == null || !(EntityLiving.class.isAssignableFrom(typeToAttack))) {
            throw new IllegalArgumentException("Could not find valid NMS class for " + typeToAttack.getSimpleName());
        }
        this.navigationSpeed = navigationSpeed;
    }

    @Override
    public BehaviourType getType() {
        return BehaviourType.ACTION;
    }

    @Override
    public String getDefaultKey() {
        return "Melee Attack";
    }

    @Override
    public boolean shouldStart() {
        if (this.getControllableEntity().getTarget() == null) {
            return false;
        }

        EntityLiving targetHandle = ((CraftLivingEntity) this.getControllableEntity().getTarget()).getHandle();

        if (!targetHandle.isAlive()) {
            return false;
        } else if (this.typeToAttack != null && !this.typeToAttack.isAssignableFrom(targetHandle.getClass())) {
            return false;
        } else {
            this.pathToAttack = NMSEntityUtil.getNavigation(this.getHandle()).a(targetHandle);
            return this.pathToAttack != null;
        }
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.getControllableEntity().getTarget();

        // CraftBukkit start
        EntityTargetEvent.TargetReason reason = this.getControllableEntity().getTarget() == null ? EntityTargetEvent.TargetReason.FORGOT_TARGET : EntityTargetEvent.TargetReason.TARGET_DIED;
        if (this.getControllableEntity().getTarget() == null || (this.getControllableEntity().getTarget() != null && !((CraftLivingEntity) this.getControllableEntity().getTarget()).getHandle().isAlive())) {
            org.bukkit.craftbukkit.v1_7_R1.event.CraftEventFactory.callEntityTargetEvent(this.getHandle(), null, reason);
        }
        // CraftBukkit end

        if (target == null) {
            return false;
        }
        EntityLiving targetHandle = ((CraftLivingEntity) target).getHandle();
        return !targetHandle.isAlive() ? false : (!this.ignoreSight ? !NMSEntityUtil.getNavigation(this.getHandle()).g() : NMSEntityUtil.isInHomeArea(this.getHandle(), MathHelper.floor(targetHandle.locX), MathHelper.floor(targetHandle.locY), MathHelper.floor(targetHandle.locZ)));
    }

    @Override
    public void start() {
        this.getControllableEntity().getNMSAccessor().navigateTo(this.pathToAttack, this.navigationSpeed > 0 ? this.navigationSpeed : this.getControllableEntity().getSpeed());
        this.moveTicks = 0;
    }

    @Override
    public void finish() {
        NMSEntityUtil.getNavigation(this.getHandle()).h();
    }

    @Override
    public void tick() {
        EntityLiving target = ((CraftLivingEntity) this.getControllableEntity().getTarget()).getHandle();

        NMSEntityUtil.getControllerLook(this.getHandle()).a(target, 30.0F, 30.0F);
        double dist = this.getHandle().e(target.locX, target.boundingBox.b, target.locZ);
        double minDist = (double) (this.getHandle().width * 2.0F * this.getHandle().width * 2.0F + target.width);

        --this.moveTicks;
        if ((this.ignoreSight || NMSEntityUtil.getEntitySenses(this.getHandle()).canSee(target)) && this.moveTicks <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || target.e(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.getHandle().aI().nextFloat() < 0.05F)) {
            this.targetX = target.locX;
            this.targetY = target.boundingBox.b;
            this.targetZ = target.locZ;

            this.moveTicks = 4 + this.getHandle().aI().nextInt(7);
            if (dist > 1024.0D) {
                this.moveTicks += 10;
            } else if (dist > 256.0D) {
                this.moveTicks += 5;
            }

            if (!this.getControllableEntity().getNMSAccessor().navigateTo(this.pathToAttack, this.navigationSpeed > 0 ? this.navigationSpeed : this.getControllableEntity().getSpeed())) {
                this.moveTicks += 15;
            }
        }

        this.attackTicks = Math.max(this.attackTicks - 1, 0);
        if (dist <= minDist && this.attackTicks <= 20) {
            this.attackTicks = 20;
            if (this.getHandle().be() != null) {
                this.getHandle().ba();
            }

            this.getHandle().m(target);
        }
    }
}