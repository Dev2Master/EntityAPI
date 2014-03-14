package org.entityapi.entity;

import org.bukkit.entity.Zombie;
import org.entityapi.EntityManager;
import org.entityapi.api.ControllableEntityType;
import org.entityapi.api.EntitySound;

public class ControllableZombie extends ControllableBaseEntity<Zombie, ControllableZombieEntity> {

    public ControllableZombie(int id, EntityManager manager) {
        super(id, ControllableEntityType.ZOMBIE, manager);
    }

    public ControllableZombie(int id, ControllableZombieEntity entityHandle, EntityManager manager) {
        this(id, manager);
        this.handle = entityHandle;
        this.loot = entityHandle.getDefaultMaterialLoot();
    }

    @Override
    public void initSounds() {
        this.setSound(EntitySound.IDLE, "mob.zombie.say");
        this.setSound(EntitySound.HURT, "mob.zombie.hurt");
        this.setSound(EntitySound.DEATH, "mob.zombie.death");
        this.setSound(EntitySound.STEP, "mob.zombie.step");
    }
}