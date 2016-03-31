import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SystemInfo {
	private String				nickname;
	private String				name;
	private NameHelper			nameHelper;
	private File				systemFile;
	private SystemHelper		systemHelper;

	private List<SystemInfo>	jumpTargets	= new ArrayList<>();

	private Map<String, Base>	base		= new HashMap<>();

	public SystemInfo(final String nickname, final String strid_name, final File root, final NameHelper nameHelper) throws FileNotFoundException {
		this.nickname = nickname;
		this.name = strid_name;
		this.nameHelper = nameHelper;
		final File systemFolder = new File(root, "SYSTEMS/");
		this.systemFile = FileUtils.getFile(systemFolder, nickname);
	}

	public void process(final SystemHelper systemHelper, final GoodsHelper goodsHelper) throws FileNotFoundException {
		this.systemHelper = systemHelper;
		if (this.systemFile == null) {
			System.out.println("Skipping " + this.nickname + " " + this.name);
			return;
		}

		final File mp = FileUtils.getFile(this.systemFile, this.nickname + "mp.ini");
		if (mp != null) {
			this.processSystem(mp, goodsHelper);
			return;
		}
		final File sp = FileUtils.getFile(this.systemFile, this.nickname + ".ini");
		this.processSystem(sp, goodsHelper);
	}

	private void processSystem(final File mp, final GoodsHelper goodsHelper) throws FileNotFoundException {
		try (Scanner in = new Scanner(mp)) {
			String nickname = "";
			String archtype = "";

			while (in.hasNextLine()) {
				final String line = in.nextLine().toLowerCase();
				if (line.contains("[object]")) {
				}
				if (line.contains("nickname")) {
					nickname = line.replace("nickname =", "").trim();
				}
				if (line.contains("archetype") && (line.contains("jumpgate") || line.contains("jumphole"))) {
					this.parseJumpGate(nickname, this.nameHelper.getName(nickname));
					nickname = "";
					archtype = "";
				}
				if (line.contains("archetype")) {
					archtype = line.replace("archetype =", "").trim();
				}
				if (line.contains("base =")) {
					this.parseBase(nickname, this.nameHelper.getName(line.replace("base =", "").trim()), archtype, line.replace("base =", ""), goodsHelper);
					nickname = "";
					archtype = "";
				}
			}
		}
	}

	private void parseBase(final String nickname2, final String objectName, final String archtype, final String line, final GoodsHelper goodsHelper) throws FileNotFoundException {
		final String targetBase = line.replace("_base", "").trim();
		if (archtype.contains("docking_fixture")) {
			final Base base = this.getBaseByNickName(targetBase);
			System.out.println("Created from docking fixture " + targetBase);
			base.setTrain();
		} else {
			final Base base = this.getBaseByNickName(nickname2);
			base.setData(nickname2, objectName, archtype, targetBase, goodsHelper);

		}
	}

	private Base getBaseByNickName(final String nickname2) {
		final Base rv = this.base.getOrDefault(nickname2, new Base(nickname2));
		this.base.put(nickname2, rv);
		return rv;
	}

	private void parseJumpGate(String nickname, final String objectName) {
		nickname = nickname.replace("_hole", "");
		if (nickname.split("_").length != 3) {
			System.err.println("Could not parse nickname " + nickname);
			return;
		}
		final String targetSystemNickname = nickname.split("_")[2];
		final SystemInfo jumpTarget = this.systemHelper.getSystemById(targetSystemNickname.toLowerCase());
		if (jumpTarget == null) {
			System.err.println("Target invalid " + nickname);
			return;
		}
		this.jumpTargets.add(jumpTarget);
	}

	public List<SystemInfo> getJumpTargets() {
		return this.jumpTargets;
	}

	public Map<String, Base> getBase() {
		return this.base;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SystemInfo [");
		if (this.nickname != null) {
			builder.append("nickname=");
			builder.append(this.nickname);
			builder.append(", ");
		}
		if (this.name != null) {
			builder.append("name=");
			builder.append(this.name);
		}
		builder.append("]");
		return builder.toString();
	}

	public String getNickname() {
		return this.nickname;
	}

	public String getName() {
		return this.name;
	}

	public NameHelper getNameHelper() {
		return this.nameHelper;
	}

	public File getSystemFile() {
		return this.systemFile;
	}

	public SystemHelper getSystemHelper() {
		return this.systemHelper;
	}

}