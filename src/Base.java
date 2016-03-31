import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Base {
	private static Set<String>	trainStations	= new HashSet<>();

	static {
		try (Scanner in = new Scanner(new File("./files/MooringTypes.txt"))) {
			for (final String s : in.nextLine().split(";")) {
				Base.trainStations.add(s);
			}
			;
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private String				nickname;
	private String				objectName;
	private String				archtype;
	private String				base;
	private boolean				train;
	private List<ItemInfo>		data			= new ArrayList<>();

	public Base(final String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return this.nickname;
	}

	public String getObjectName() {
		return this.objectName;
	}

	public boolean isTrain() {
		return this.train;
	}

	public void setTrain() {
		this.train = true;
	}

	public void setData(final String nickname2, final String objectName2, final String archtype2, final String line, final GoodsHelper goodsHelper) throws FileNotFoundException {
		this.nickname = nickname2;
		this.objectName = objectName2;
		this.archtype = archtype2;
		if (Base.trainStations.contains(this.archtype)) {
			this.setTrain();
		}
		this.base = line;

		this.readData(goodsHelper);

	}

	private void readData(final GoodsHelper goodsHelper) throws FileNotFoundException {
		if (this.nickname.equals("li01_08")) {
			System.out.println("kakce");
		}

		final File root = new File(Starter.freelancerfolder, "/DATA/EQUIPMENT/market_commodities.ini");
		try (Scanner in = new Scanner(root)) {
			boolean skip = true;
			while (in.hasNextLine()) {
				final String line = in.nextLine().toLowerCase();
				if (line.contains("base = ")) {
					final String base = line.replace("base = ", "").replace("_base", "").trim().toLowerCase();
					skip = !base.equals(this.base);
					continue;
				}

				if (line.isEmpty()) {
					skip = true;
				}

				if (!skip) {
					final String item = line.split("=")[1];
					final String[] parts = item.split(",");
					final Integer goodBasePrice = goodsHelper.goods.get(parts[0].trim());
					final boolean sells = (Float.parseFloat(parts[3]) > 0);
					final int currentPrice = (int) (goodBasePrice * Float.parseFloat(parts[6]));

					this.data.add(new ItemInfo(parts[0].trim(), sells, currentPrice, goodBasePrice));

				}

			}
		}
	}

	public List<ItemInfo> getData() {
		return this.data;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Base [");
		if (this.nickname != null) {
			builder.append("nickname=");
			builder.append(this.nickname);
			builder.append(", ");
		}
		if (this.objectName != null) {
			builder.append("objectName=");
			builder.append(this.objectName);
			builder.append(", ");
		}
		if (this.archtype != null) {
			builder.append("archtype=");
			builder.append(this.archtype);
			builder.append(", ");
		}
		if (this.base != null) {
			builder.append("base=");
			builder.append(this.base);
			builder.append(", ");
		}
		builder.append("train=");
		builder.append(this.train);
		builder.append("]");
		return builder.toString();
	}

	public Integer getGood(final String id, final GoodsHelper goodsHelper) {
		for (final ItemInfo ite : this.data) {
			if (ite.id.equals(id)) {
				return ite.currentPrice;
			}
		}
		return goodsHelper.goods.get(id);
	}

}
