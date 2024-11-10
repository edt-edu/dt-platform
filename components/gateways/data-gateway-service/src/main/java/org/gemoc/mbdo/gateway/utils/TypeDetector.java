package org.gemoc.mbdo.gateway.utils;

public class TypeDetector {
	public static Object detectType(String input) {
		if (isBoolean(input)) {
			return Boolean.parseBoolean(input);
		} else if (isInteger(input)) {
			return Integer.parseInt(input);
		} else if (isFloat(input)) {
			return Float.parseFloat(input);
		} else {
			return input;
		}
	}

	public static boolean isBoolean(String input) {
		return "true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input);
	}

	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isFloat(String input) {
		try {
			Float.parseFloat(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
