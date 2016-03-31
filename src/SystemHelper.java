import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SystemHelper {
	private Map<String, SystemInfo>	systems	= new HashMap<>();

	public SystemHelper(final NameHelper nameHelper, final GoodsHelper goodsHelper) throws Exception {
		final File root = new File(Starter.freelancerfolder, "/DATA/UNIVERSE/");

		final File systemFile = new File(root, "universe.ini");
		try (Scanner in = new Scanner(systemFile)) {
			String nickname = "";
			String strid_name = "";
			boolean base = false;
			while (in.hasNextLine()) {
				final String line = in.nextLine().toLowerCase();
				if (line.contains("[system]")) {
					nickname = "";
					strid_name = "";
					base = false;
				}
				if (line.contains("[base]")) {
					nickname = "";
					strid_name = "";
					base = true;
				}
				if (line.contains("nickname")) {
					nickname = line.split("=")[1].trim();
				}
				if (line.contains("strid_name")) {
					strid_name = nameHelper.getName(nickname);
					this.parse(nickname, strid_name, base, root, nameHelper);
				}
			}
		}

		for (final SystemInfo sys : this.systems.values()) {
			sys.process(this, goodsHelper);
		}
	}

	private void parse(final String nickname, final String name, final boolean station, final File root, final NameHelper nameHelper) throws FileNotFoundException {
		if (!station) {
			this.systems.put(nickname.toLowerCase(), new SystemInfo(nickname, name, root, nameHelper));
		}
	}

	public Map<String, SystemInfo> getSystems() {
		return this.systems;
	}

	public SystemInfo getSystemById(final String lowerCase) {
		return this.systems.get(lowerCase);
	}

}
