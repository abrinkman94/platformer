package com.brinkman.platformer.component.input;


import com.brinkman.platformer.component.RootComponent;

/**
 * @author Austin Brinkman.
 */
public interface InputComponent extends RootComponent
{
	boolean isRunActive();

	void setRunActive(boolean runActive);

	boolean isLeftActive();

	void setLeftActive(boolean leftActive);

	boolean isRightActive();

	void setRightActive(boolean rightActive);

	boolean isMeleeActive();

	void setMeleeActive(boolean meleeActive);
}
