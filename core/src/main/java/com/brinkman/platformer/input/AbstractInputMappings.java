package com.brinkman.platformer.input;

/**
 * @author Austin Brinkman.
 */
public abstract class AbstractInputMappings
{
	protected boolean left;
	protected boolean right;
	protected boolean run;
	protected boolean melee;

	public boolean left() { return left; }
	public boolean right() { return right; }
	public boolean run() { return run; }
	public boolean melee() { return melee; }
}
