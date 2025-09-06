package input;

import java.io.*;

public class TerminalUtils {
	// Only works on Unix-like systems (uses stty)
	private static String originalConfig = null;

	public static boolean isUnixLike() {
		String os = System.getProperty("os.name").toLowerCase();
		return !os.contains("win");
	}

	public static void enableRawMode() {
		if (!isUnixLike()) return;
		try {
			// save current settings
			originalConfig = exec(new String[] { "sh", "-c", "stty -g < /dev/tty" });
			// set raw mode, disable echo
			exec(new String[] { "sh", "-c", "stty raw -echo < /dev/tty" });
		} catch (Exception e) {
			// ignore; fall back to normal input behavior
		}
	}

	public static void disableRawMode() {
		if (!isUnixLike() || originalConfig == null) return;
		try {
			// restore saved settings
			exec(new String[] { "sh", "-c", "stty " + originalConfig + " < /dev/tty" });
			originalConfig = null;
		} catch (Exception e) {
			// ignore
		}
	}

	private static String exec(String[] cmd) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec(cmd);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (InputStream is = p.getInputStream()) {
			int b;
			while ((b = is.read()) != -1) out.write(b);
		}
		p.waitFor();
		return out.toString().trim();
	}
}