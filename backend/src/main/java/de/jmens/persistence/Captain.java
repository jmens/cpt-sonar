package de.jmens.persistence;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Captain {
	public abstract int id();
	public abstract String name();
	public abstract String surname();
	public abstract String email();
}
