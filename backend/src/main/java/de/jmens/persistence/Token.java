package de.jmens.persistence;

import java.util.UUID;
import org.immutables.value.Value.Immutable;

@Immutable
public abstract class Token {
	public abstract UUID token();
	public abstract int captainId();
}
