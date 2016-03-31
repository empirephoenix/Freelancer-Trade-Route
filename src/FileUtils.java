import java.io.File;

public class FileUtils {
	public static File getFile(final File parent, final String file) {
		for (final File system : parent.listFiles()) {
			if (system.getName().toLowerCase().equals(file.toLowerCase())) {
				return system;
			}
		}
		return null;
	}
}
