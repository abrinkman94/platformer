package com.brinkman.platformer.component;


/**
 * @author Austin Brinkman.
 */
@FunctionalInterface
public interface InputComponent extends RootComponent
{
	void setKeyFlags(boolean left, boolean right, boolean run);
}
