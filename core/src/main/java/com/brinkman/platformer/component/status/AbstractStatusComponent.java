package com.brinkman.platformer.component.status;

/**
 * @author Caleb Brinkman
 */
public class AbstractStatusComponent implements StatusComponent {
    private int maxHealth;
    private int currentHealth;

    @Override
    public int getMaxHealth() { return maxHealth; }

    @Override
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    @Override
    public int getCurrentHealth() { return currentHealth; }

    @Override
    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }

    @Override
    public int getMeleeDamage() {
        // TODO Make this actually meaningful; probably requires some sort of EquipmentComponent or similar.
        return 3;
    }
}
