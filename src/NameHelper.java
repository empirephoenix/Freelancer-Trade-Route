import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class NameHelper {

	private HashMap<String, String>	idMap	= new HashMap<>();

	public NameHelper() throws IOException {
		try (final Scanner in = new Scanner(new File("./files/Bases.txt"))) {
			while (in.hasNextLine()) {
				this.parseBaseNames(in.nextLine());
			}
		}
		try (final Scanner in = new Scanner(new File("./files/Systems.txt"))) {
			while (in.hasNextLine()) {
				this.parseSystem(in.nextLine());
			}
		}
		try (final Scanner in = new Scanner(new File("./files/Commoditys.txt"))) {
			while (in.hasNextLine()) {
				this.parseGoodName(in.nextLine());
			}
		}
	}

	private void parseSystem(String line) {
		line = line.toLowerCase();
		line = line.replace("insert into system values ", "").trim();
		final String[] systems = line.split("\\)");
		for (String system : systems) {
			final int start = system.indexOf("(");
			if (start < 0) {
				continue;
			}
			system = system.substring(start + 1);
			final String[] parts = system.split(",");
			final String nickname = parts[3].replace("'", "").trim();
			final String name = parts[5].replace("'", "").trim();
			this.idMap.put(nickname, name);
		}

	}

	private void parseBaseNames(String line) {
		line = line.toLowerCase();
		line = line.replace("insert into system values ", "").trim();
		final String[] systems = line.split("\\)");
		for (String system : systems) {
			final int start = system.indexOf("(");
			if (start < 0) {
				continue;
			}
			system = system.substring(start + 1);
			final String[] parts = system.split(",");
			final String nickname = parts[6].replace("'", "").trim();
			final String name = parts[7].replace("'", "").trim();
			this.idMap.put(nickname, name);
		}
	}

	private void parseGoodName(String line) {
		line = line.replace("insert into equipment values (", "");
		line = line.substring(line.indexOf("'"));
		final String[] parts = line.split(",");
		final String nickname = parts[0].replace("'", "").trim();
		final String name = parts[1].replace("');", "").replace("'", "").trim();
		this.idMap.put(nickname, name);
	}

	public String getName(final String nickname) {
		return this.idMap.getOrDefault(nickname, nickname);
	}
}
