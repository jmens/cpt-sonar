package de.jmens.persistence;

import static de.jmens.persistence.Decision.State.ACCEPTED;
import static de.jmens.persistence.Decision.State.REJECTED;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.rmi.UnexpectedException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class DB {

	private static final Logger LOGGER = getLogger(DB.class);

	private static QueryRunner runner;

	static {
		try {
//			Class.forName("org.h2.Driver");
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to load jdbc driver", e);
		}
	}

	private static QueryRunner getRunner() {
		if (runner == null) {
			final BasicDataSource dataSource = new BasicDataSource();
//			final String jdbcUrl = System.getenv().getOrDefault("CPTSONAR_JDBCURL", "jdbc:h2:/tmp/cpt-sonar");
			final String jdbcUrl = System.getenv().getOrDefault("CPTSONAR_JDBCURL", "jdbc:mysql://localhost:3306/cptsonar?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&verifyServerCertificate=false&allowMultiQueries=true");
			final String username = System.getenv().getOrDefault("CPTSONAR_DBUSER", "cpt-sonar");

			LOGGER.info("Initializing database connection as {} with {}", username, jdbcUrl);

			dataSource.setUrl(jdbcUrl);
			dataSource.setUsername(username);
			dataSource.setPassword(System.getenv().getOrDefault("CPTSONAR_DBPASS", "cpt-sonar"));
			runner = new QueryRunner(dataSource);
		}
		return runner;
	}

	public static boolean updateDatabase() {
		final String ddl;
		try {
			ddl = IOUtils.resourceToString("/sql/schema.sql", Charset.defaultCharset());
		} catch (IOException e) {
			LOGGER.error("Failed to read DDL from resource", e);
			throw new RuntimeException(e);
		}

		try {
			final Statement statement = getRunner().getDataSource().getConnection().createStatement();
			statement.executeUpdate(ddl);
			statement.close();
		} catch (SQLException e) {
			LOGGER.error("Failed to apply DDL to database", e);
			throw new RuntimeException(e);
		}

		return true;
	}

	public static Optional<Captain> getCaptainByAccessKey(String accessKey) {
		final String query = "SELECT * FROM captain WHERE access_key=?";

		try {
			return getRunner().query(query, crsh, accessKey);
		} catch (SQLException e) {
			DbUtils.printStackTrace(e);
			throw new RuntimeException("failed to access database", e);
		}
	}


	public static Optional<Captain> getCaptainByToken(String token) {
		final String query = "SELECT c.* FROM captain c LEFT JOIN token t ON t.captain_id = c.id WHERE t.token=?;";

		try {
			return getRunner().query(query, crsh, token);
		} catch (SQLException e) {
			DbUtils.printStackTrace(e);
			throw new RuntimeException("failed to access database", e);
		}
	}

	private static ResultSetHandler<Optional<Captain>> crsh = rs -> {
		if (rs.next()) {
			return Optional.of(ImmutableCaptain.builder()
					.id(rs.getInt("id"))
					.email(rs.getString("email"))
					.name(rs.getString("name"))
					.surname(rs.getString("surname"))
					.build());
		}
		return Optional.empty();
	};

	public static List<Schedule> getScheduleByCaptain(int captainId) {
		try {
			final String query = IOUtils.resourceToString("/sql/selectScheduleByCaptainId.sql", Charset.defaultCharset());
			final List<Map<String, Object>> dbresult = getRunner().query(query, new MapListHandler(), captainId);

			return dbresult
					.stream()
					.map(resultSet -> ImmutableSchedule.builder()
							.id(((Number) resultSet.get("id")).intValue())
							.date((Date) resultSet.get("day"))
							.pendingCount(((Number) resultSet.get("pendingCount")).intValue())
							.acceptedCount(((Number) resultSet.get("acceptedCount")).intValue())
							.rejectedCount(((Number) resultSet.get("rejectedCount")).intValue())
							.rejected(resultSet.get("state").equals("REJECTED"))
							.accepted(resultSet.get("state").equals("ACCEPTED"))
							.build()
					)
					.collect(Collectors.toList());
		} catch (SQLException e) {
			DbUtils.printStackTrace(e);
			throw new RuntimeException("failed to access database", e);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load sql query",e);
		}
	}

	public static int setAccepted(Integer scheduleId, Integer captainId) {
		return updateDecision(scheduleId, captainId, ACCEPTED);
	}

	public static int setRejected(Integer scheduleId, Integer captainId) {
		return updateDecision(scheduleId, captainId, REJECTED);
	}

	private static int updateDecision(Integer scheduleId, Integer captainId, Decision.State state) {
		final String selectState = "SELECT stc.id FROM schedule_to_captain stc WHERE stc.schedule_id=? AND stc.captain_id=?";

		LOGGER.info("Setting decision - captain {}, schedule {}, state {}", captainId, scheduleId, state);

		final Integer decisionId;

		try {
			decisionId = getRunner().query(selectState, new ScalarHandler<Integer>(), scheduleId, captainId);
		} catch (SQLException e) {
			DbUtils.printStackTrace(e);
			throw new RuntimeException("Failed to access database", e);
		}

		if (decisionId == null) {
			LOGGER.info("Captain decides schedule for the first time.");
			try {
				final String insertDecisionQuery = "INSERT INTO schedule_to_captain  (`id`, `captain_id`, `schedule_id`, `state`) values (DEFAULT, ?, ?, ?)";
				return getRunner().update(insertDecisionQuery, captainId, scheduleId, state.toString());
			} catch (SQLException e) {
				DbUtils.printStackTrace(e);
				throw new RuntimeException("Failed to access database", e);
			}
		} else {
			LOGGER.info("Captain updates decision {}", decisionId);
			try {
				final String updateDecisionQuery = "UPDATE schedule_to_captain stc SET stc.state=? WHERE id=?";
				return getRunner().update(updateDecisionQuery, state.toString(),  decisionId);
			} catch (SQLException e) {
				DbUtils.printStackTrace(e);
				throw new RuntimeException("Failed to access database", e);
			}
		}
	}

	public static Token generateToken(Captain captain) {

		final ResultSetHandler<Optional<Token>> handler = rs -> {
			if (rs.next()) {
				return Optional.of(ImmutableToken.builder()
						.token(UUID.fromString(rs.getString("token")))
						.captainId(rs.getInt("captain_id"))
						.build());
			}
			return Optional.empty();
		};

		final UUID token = UUID.randomUUID();

		try {
			final String fetchExistingToken = "SELECT t.token, t.captain_id FROM token t LEFT JOIN captain c ON t.captain_id = c.id WHERE c.id=?";
			final Optional<Token> maybeToken = getRunner().query(fetchExistingToken, handler, captain.id());
			if (maybeToken.isPresent()) {
				final int updates = getRunner().update("UPDATE token SET token=? WHERE captain_id=?", token.toString(), captain.id());
				if (updates != 1) {
					LOGGER.error("Invalid update count: {}", updates);
				}
			} else {
				final int inserts = getRunner().update("INSERT INTO token VALUES (DEFAULT, ?, ?)", captain.id(), token.toString());
					LOGGER.error("Invalid insert count: {}", inserts);
			}

			return ImmutableToken.builder()
					.token(token)
					.captainId(captain.id())
					.build();

		} catch (SQLException e) {
			DbUtils.printStackTrace(e);
			throw new RuntimeException("failed to access database", e);
		}
	}
}
