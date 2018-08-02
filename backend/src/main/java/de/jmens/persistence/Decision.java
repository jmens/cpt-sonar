package de.jmens.persistence;

import java.util.Arrays;
import org.immutables.value.Value.Immutable;

@Immutable
public abstract class Decision {
	public abstract int id();
	public abstract String captainId();
	public abstract String scheduleId();
	public abstract State state();

	public enum State {
		PENDING, ACCEPTED, REJECTED, UNKNOWN;

		public State parse(String key) {
			return Arrays.stream(State.values())
					.filter(state -> key.equalsIgnoreCase(state.toString()))
					.findFirst()
					.orElse(UNKNOWN);
		}
	}
}
