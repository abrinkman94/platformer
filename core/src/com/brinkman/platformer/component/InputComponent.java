package com.brinkman.platformer.component;


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
}
