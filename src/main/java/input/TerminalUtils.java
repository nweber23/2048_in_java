package input;

import java.io.*;

public class TerminalUtils {
	private static String originalConfig = null;
	private static int rawModeCount = 0;

	public static boolean isUnixLike() {
		String os = System.getProperty("os.name").toLowerCase();
		return !os.contains("win");
	}

	public static synchronized void enableRawMode() {
		if (!isUnixLike()) return;
		try {
			if (rawModeCount == 0) {
				originalConfig = exec(new String[] { "sh", "-c", "stty -g < /dev/tty" });
			}
			exec(new String[] { "sh", "-c", "stty raw -echo < /dev/tty" });
			rawModeCount++;
		} catch (Exception e) {
		}
	}

	public static synchronized void disableRawMode() {
		if (!isUnixLike() || originalConfig == null) return;
		try {
			rawModeCount--;
			if (rawModeCount <= 0) {
				exec(new String[] { "sh", "-c", "stty " + originalConfig + " < /dev/tty" });
				originalConfig = null;
				rawModeCount = 0;
			}
		} catch (Exception e) {
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