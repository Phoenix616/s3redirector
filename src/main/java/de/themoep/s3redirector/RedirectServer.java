package de.themoep.s3redirector;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static de.themoep.s3redirector.Main.logDebug;

public class RedirectServer {
	private final String host;
	private final int port;
	private final int redirectCode;
	private final LoadingCache<String, String> cache;

	public RedirectServer(String host, int port, long cacheExpiration, Function<String, String> redirector, int redirectCode) {
		this.host = host;
		this.port = port;
		this.redirectCode = redirectCode;
		this.cache = Caffeine.newBuilder()
				.expireAfterWrite(cacheExpiration - 100, TimeUnit.MILLISECONDS)
				.build(redirector::apply);
	}

	public void start() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
			server.createContext("/", new HttpHandler() {
				@Override
				public void handle(HttpExchange exchange) throws IOException {
					String requestMethod = exchange.getRequestMethod();
					String path = exchange.getRequestURI().getPath();
					logDebug("Got request: " + requestMethod + " " + path);
					if ("GET".equals(requestMethod)) {
						String redirectUrl = cache.get(path);
						if (redirectUrl != null) {
							exchange.getResponseHeaders().set("Location", redirectUrl);
							reply(exchange, redirectCode, "Redirect " + redirectUrl);
						} else {
							// Not Found if the object does not exist
							reply(exchange, 404, "Not Found");
						}
					} else {
						// Method Not Allowed if not a GET request
						reply(exchange, 405, "Method Not Allowed");
					}
					exchange.close();
				}
			});
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
			System.out.println("Started server on " + host + ":" + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void reply(HttpExchange exchange, int code, String message) {
		try {
			exchange.sendResponseHeaders(code, -1);
			logDebug("Replied with " + code + ": " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
