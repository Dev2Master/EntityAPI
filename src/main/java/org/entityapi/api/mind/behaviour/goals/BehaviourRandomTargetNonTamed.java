package org.entityapi.api.mind.behaviour.goals;

import org.entityapi.api.ControllableEntity;

public class BehaviourRandomTargetNonTamed extends BehaviourMoveTowardsNearestAttackableTarget {

    public BehaviourRandomTargetNonTamed(ControllableEntity controllableEntity, Class classToTarget, int chance, boolean checkSenses) {
        super(controllableEntity, classToTarget, chance, checkSenses);
    }

    @Override
    public String getDefaultKey() {
        return "Random Target Non Tamed";
    }
}