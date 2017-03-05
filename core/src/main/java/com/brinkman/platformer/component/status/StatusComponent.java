package com.brinkman.platformer.component.status;

public interface StatusComponent {
    int getMaxHealth();

    void setMaxHealth(int maxHealth);

    int getCurrentHealth();

    void setCurrentHealth(int currentHealth);

    int getMeleeDamage();
}
