package de.jmens;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.slf4j.LoggerFactory.getLogger;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.jmens.persistence.Captain;
import de.jmens.persistence.DB;
import de.jmens.persistence.Token;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import spark.Filter;
import spark.Route;

public class Server {

	private static final Logger LOGGER = getLogger(Server.class);

	private Gson gson;

	private Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder()
					.registerTypeAdapter(java.sql.Date.class, new DateSerializer())
					.create();
		}
		return gson;
	}

	public void startup(int port) {

		DB.updateDatabase();

		port(port);
		staticFileLocation("/static");
		before(corsFilter);
		before(securityFilter);
		post("authenticate", handleAuthentication);
		get("schedule", handleGetSchedule);
		get("captain", handleGetCaptain);
		post("accept", handlePostAccept);
		post("reject", handlePostReject);
	}

	private Filter securityFilter = (request, response) -> {

		final String path = trimToEmpty(request.pathInfo()).toLowerCase();
		if (StringUtils.startsWithAny(path, "/authenticate", "/static")) {
			return;
		}

		final Optional<Captain> mayBeCaptain = DB.getCaptainByToken(request.headers("X-APP-SECURITY"));

		if (!mayBeCaptain.isPresent()) {
			halt(401, "not authenticated");
		}

		request.attribute("CAPTAIN", mayBeCaptain.get());
	};

	private Filter corsFilter = new CorsFilter();

	private Route handleAuthentication = (request, response) -> {

		LOGGER.info("Authentication requested");

		final AuthRequest authRequest = getGson().fromJson(request.body(), AuthRequest.class);
		final Optional<Token> maybeToken = DB.getCaptainByAccessKey(authRequest.accessKey)
				.map(captain -> DB.generateToken(captain));

		Thread.sleep(1000);

		if (maybeToken.isPresent()) {
			LOGGER.info("Access granted");
			return getGson().toJson(Collections.singletonMap("token", maybeToken.get().token()));
		} else {
			LOGGER.info("Access denied");
			halt(401, "not authenticated");
			return null;
		}
	};

	private Route handleGetCaptain = (request, response) -> getGson().toJson(request.<Captain>attribute("CAPTAIN"));

	private Route handleGetSchedule = (request, response) -> {
		final Captain captain = (Captain) request.attribute("CAPTAIN");

		LOGGER.info("Schedule requested by captain {}", captain.id());

		return getGson().toJson(DB.getScheduleByCaptain(captain.id()));
	};

	private Route handlePostAccept = (request, response) -> {
		final UpdateScheduleRequest schedule = getGson().fromJson(request.body(), UpdateScheduleRequest.class);
		final Captain captain = (Captain) request.attribute("CAPTAIN");

		LOGGER.info("Captain {} decides to accept {}", captain.id(), schedule.id());

		DB.setAccepted(schedule.id(), captain.id());
		return "";
	};

	private Route handlePostReject = (request, response) -> {
		final UpdateScheduleRequest schedule = getGson().fromJson(request.body(), UpdateScheduleRequest.class);
		final Captain captain = (Captain) request.attribute("CAPTAIN");

		LOGGER.info("Captain {} decides to reject {}", captain.id(), schedule.id());

		DB.setRejected(schedule.id(), captain.id());
		return "";
	};

	public static class AuthRequest {

		public String accessKey;
	}

	public static class UpdateScheduleRequest {

		public Integer id;

		public Integer id() {
			return id;
		}
	}
}

class DateSerializer implements JsonSerializer<Date> {
	public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
		return date == null ? null : new JsonPrimitive(new SimpleDateFormat("dd.MM.yyyy").format(date));
	}
}
