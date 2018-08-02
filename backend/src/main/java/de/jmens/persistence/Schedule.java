package de.jmens.persistence;

import java.sql.Date;
import org.immutables.value.Value;

@Value.Immutable
public abstract class Schedule {
	public abstract int id();
	public abstract Date date();
	public abstract int pendingCount();
	public abstract int acceptedCount();
	public abstract int rejectedCount();
	public abstract boolean accepted();
	public abstract boolean rejected();
}
