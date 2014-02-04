package io.snw.entityapi.entity;

import io.snw.entityapi.EntityManager;
import io.snw.entityapi.api.ControllableEntityType;
import io.snw.entityapi.api.EntitySound;
import org.bukkit.entity.Zombie;

public class ControllableZombie extends ControllableAttackingBaseEntity<Zombie> {

    public ControllableZombie(int id, EntityManager entityManager) {
        super(id, ControllableEntityType.ZOMBIE, entityManager);
    }

    public ControllableZombie(int id, ControllableZombieEntity entityHandle, EntityManager entityManager) {
        super(id, ControllableEntityType.ZOMBIE, entityManager);
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