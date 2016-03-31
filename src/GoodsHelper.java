import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GoodsHelper {
	Map<String, Integer>	goods	= new HashMap<>();
	Map<String, Float>		scale	= new HashMap<>();
	Map<String, String>		ids		= new HashMap<>();

	public GoodsHelper(final NameHelper nameHelper) throws FileNotFoundException {
		final File root = new File(Starter.freelancerfolder, "/DATA/EQUIPMENT/goods.ini");

		try (Scanner in = new Scanner(root)) {

			String nickName = "";
			while (in.hasNextLine()) {
				final String line = in.nextLine().toLowerCase();
				if (line.contains("nickname")) {
					nickName = line.replace("nickname =", "").trim();
				}
				if (line.contains("price =") && !line.contains("_price")) {
					final int price = Integer.parseInt(line.replace("price =", "").trim());
					this.goods.put(nickName, price);
					this.scale(nickName, new File(Starter.freelancerfolder, "/DATA/EQUIPMENT/select_equip.ini"), nameHelper);
				}

			}
		}

		System.out.println(this.goods);
	}

	public float getScale(final String nickname) {
		return this.scale.getOrDefault(nickname, 1f);
	}

	public String getName(final String nickname) {
		return this.ids.getOrDefault(nickname, "bla");
	}

	private void scale(final String nickName, final File file, final NameHelper nameHelper) throws FileNotFoundException {
		try (Scanner in = new Scanner(file)) {
			boolean read = false;
			while (in.hasNextLine()) {
				String line = in.nextLine();
				if (line.contains("nickname =")) {
					read = line.replace("nickname =", "").trim().equals(nickName);
				}
				if (read) {
					if (line.contains("volume =")) {
						line = line.replace("volume =", "").trim();
						float vol = Float.parseFloat(line);
						if (vol <= 0) {
							vol = 1;
						}
						this.scale.put(nickName, vol);
					}
					if (line.contains("ids_name =")) {
						line = line.replace("ids_name =", "").trim();
						this.ids.put(nickName, nameHelper.getName(nickName));
					}
				}
			}
		}
	}

	public Map<String, Integer> getGoods() {
		return this.goods;
	}
}
