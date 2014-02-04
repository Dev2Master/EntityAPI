package io.snw.entityapi.entity;

import io.snw.entityapi.EntityManager;
import io.snw.entityapi.api.ControllableEntityType;
import io.snw.entityapi.api.EntitySound;
import org.bukkit.entity.IronGolem;

public class ControllableIronGolem extends ControllableAttackingBaseEntity<IronGolem> {

    public ControllableIronGolem(int id, EntityManager entityManager) {
        super(id, ControllableEntityType.IRON_GOLEM, entityManager);
    }

    public ControllableIronGolem(int id, ControllableIronGolemEntity entityHandle, EntityManager entityManager) {
        super(id, ControllableEntityType.IRON_GOLEM, entityManager);
        this.handle = entityHandle;
        this.loot = entityHandle.getDefaultMaterialLoot();
    }

    @Override
    public void initSounds() {
        this.setSound(EntitySound.HURT, "mob.irongolem.hit");
        this.setSound(EntitySound.DEATH, "mob.irongolem.death");
        this.setSound(EntitySound.THROW, "mob.irongolem.throw");
    }
}