package fri.rg.zamtrax;

public class Util {

	public static String formatTime(float seconds) {
		int s = (int) (seconds % 60);
		int ms = (int) (seconds % 1 * 100);
		int m = (int) (seconds / 60);

		if (m > 0) {
			return String.format("%d:%d.%d", m, s, ms);
		}

		return String.format("%d.%d", s, ms);
	}

}
