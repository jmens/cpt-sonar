package de.jmens;

import java.util.HashMap;
import java.util.Map;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

public final class CorsFilter implements Filter {

	private static final Map<String, String> HEADERS = new HashMap<>();

	static {
		HEADERS.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
		HEADERS.put("Access-Control-Allow-Origin", "*");
		HEADERS.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-APP-SECURITY,Content-Length,Accept,Origin,");
		HEADERS.put("Access-Control-Allow-Credentials", "true");
	}

	@Override
	public void handle(Request request, Response response) throws Exception {
		HEADERS.forEach((key, value) -> {
			response.header(key, value);
		});

		if (request.requestMethod().equalsIgnoreCase("OPTIONS")) {
			Spark.halt(200, "ok");
		}
	}
}
