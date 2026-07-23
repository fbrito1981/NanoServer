package nano.server.utils;

import nano.server.enums.ServerProperties;

public class IpAddressUtils {
	public static boolean areInSameSubNet(String firstIp, String secondIp) {
		if (firstIp.equals(secondIp) || Boolean.valueOf(ServerProperties.ALLOW_EXTERNAL_ACCESS.getValue())) {
			return true;
		} else {
			if (firstIp.contains(".") && secondIp.contains(".")) {
				String[] firstIpParts = firstIp.split("\\.");
				String[] secondIpParts = secondIp.split("\\.");
				
				if (firstIpParts.length == 4 && secondIpParts.length == 4) {
					return firstIpParts[0].equals(secondIpParts[0]) &&
							firstIpParts[1].equals(secondIpParts[1]) &&
							firstIpParts[2].equals(secondIpParts[2]);
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	public static boolean isKnownIp(String ipToCheck, String knownIp) {
		if (knownIp.contains(",")) {
			return isKnownIp(ipToCheck, knownIp.split(","));
		} else {
			return isKnownIp(ipToCheck, new String[] { knownIp });
		}
	}
	
	public static boolean isKnownIp(String ipToCheck, String[] knownIps) {
		for (String knownIp : knownIps) {
			int parts = 4;
			String knownIpToCheck = knownIp;
			if (knownIp.contains("/")) {
				String[] knownIpParts = knownIp.split("/");
				parts = Integer.parseInt(knownIpParts[1]) / 8;
				knownIpToCheck = knownIpParts[0];
			}
			if (ipToCheck.contains(".") && knownIpToCheck.contains(".")) {
				String[] ipToCheckParts = ipToCheck.split("\\.");
				String[] knownIpToCheckParts = knownIpToCheck.split("\\.");
				boolean valid = true;
				for (int i = 0; i < parts; i++) {
					if (!ipToCheckParts[i].equals(knownIpToCheckParts[i]) && !knownIpToCheckParts[i].equals("0")) {
						valid = false;
					}
				}
				if (valid) {
					return true;
				}
			}
		}
		return false;
	}
}
