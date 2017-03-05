package com.brinkman.platformer.component.action;

import com.brinkman.platformer.GameWorld;
import com.brinkman.platformer.entity.Entity;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

/**
 * @author Austin Brinkman.
 */
public class SimpleActionComponent implements ActionComponent {
	private final Map<ActionType, Float> cooldowns = new EnumMap<>(ActionType.class);
	private final Map<ActionType, Float> initialCooldowns = new EnumMap<>(ActionType.class);
	private final Map<ActionType, BiConsumer<Entity, GameWorld>> actions;

	public SimpleActionComponent(Map<ActionType, BiConsumer<Entity, GameWorld>> actions) {
		this.actions = new EnumMap<>(actions);

		initialCooldowns.put(ActionType.MELEE_ATTACK, 2.0f);
		cooldowns.put(ActionType.MELEE_ATTACK, 2.0f);
	}

	@Override
	public float getRemainingCooldown(ActionType actionType) {
		return cooldowns.get(actionType);
	}

	@Override
	public void decrementCooldown(ActionType actionType, float amount) {
		cooldowns.put(actionType, cooldowns.get(actionType) - amount);
	}

	@Override
	public void setCooldown(ActionType actionType, float cooldown) {
		cooldowns.put(actionType, cooldown);
	}

	@Override
	public void doAction(ActionType actionType, Entity entity, GameWorld world) {
		if (actions.get(actionType) != null) {
			actions.get(actionType).accept(entity, world);
		}

		resetCooldowns(actionType);
	}

	@Override
	public void resetCooldowns(ActionType actionType) {
		for (Entry entry : cooldowns.entrySet()) {
			entry.setValue(initialCooldowns.get(actionType));
		}
	}


}
