package de.themoep.s3redirector;

/*
 * s3redirector
 * Copyright (C) 2023 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

public class Main {

	static Properties config = new Properties();

	private static boolean debug = true;

	public static void main(String[] args) {
		Properties appInfo = new Properties();
		try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("app.properties")) {
			appInfo.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		File configFile = new File("config.properties");
		if (configFile.exists()) {
			try (InputStream inputStream = new FileInputStream(configFile)) {
				config.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		debug = Boolean.parseBoolean(getValue("debug", "false"));

		System.out.print(
				appInfo.getProperty("application.name") + " " + appInfo.getProperty("application.version")
						+ "\n"
						+ "Copyright (C) 2023 Max Lee aka Phoenix616 (max@themoep.de)\n"
						+ "    By using this program you agree to the terms of the AGPLv3\n"
						+ "    The full license text can be found here: https://phoenix616.dev/licenses/agpl-v3.txt\n"
						+ "    This program's source is available here: https://github.com/Phoenix616/s3redirector\n"
		);

		long expiration = Long.parseLong(getValue("expiration", "3600")) * 1000;
		S3Adapter s3Adapter = new S3Adapter(
				getValue("endpoint"),
				getValue("region"),
				getValue("accesskey"),
				getValue("secretkey"),
                expiration
		);

		String host = getValue("host", "127.0.0.1");
		int port = Integer.parseInt(getValue("port", "8053"));

		new RedirectServer(host, port, expiration, path -> {
			String[] parts = path.split("/", 2);
			if (parts.length != 2) {
				return null;
			}
			URL url = s3Adapter.getObjectUrl(parts[0], parts[1]);
			if (url != null) {
				return url.toString();
			}
			return null;
		}, Integer.parseInt(getValue("redirectcode", "302"))).start();
	}

	static void logDebug(String message) {
		if (debug) {
			System.out.println("[DEBUG] " + message);
		}
	}

	private static String getValue(String path) {
		return getValue(path, null);
	}

	private static String getValue(String path, String defaultValue) {
		String value = System.getProperty("s3r." + path.toLowerCase(Locale.ROOT).replace('_', '.'));
		if (value != null) {
			return value;
		}

		value = config.getProperty(path.toLowerCase(Locale.ROOT).replace('_', '.'));
		if (value != null) {
			return value;
		}

		value = System.getenv("S3R_" + path.toUpperCase(Locale.ROOT).replace('.', '_'));
		if (value != null) {
			return value;
		}
		return defaultValue;
	}
}

