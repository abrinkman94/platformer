package com.brinkman.platformer.component.status;

import com.brinkman.platformer.component.RootComponent;

public interface StatusComponent extends RootComponent {
    int getMaxHealth();

    void setMaxHealth(int maxHealth);

    int getCurrentHealth();

    void setCurrentHealth(int currentHealth);

    int getMeleeDamage();
}
