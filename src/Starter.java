import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Starter {

	public static File	freelancerfolder;

	public static void main(final String[] args) throws Exception {
		Starter.freelancerfolder = new File(args[1]);

		final NameHelper nameHelper = new NameHelper();
		final GoodsHelper goodsHelper = new GoodsHelper(nameHelper);
		final SystemHelper systemHelper = new SystemHelper(nameHelper, goodsHelper);

		final PrintStream excelout = new PrintStream(new File("./out.csv"));
		excelout.println("ItemProfit;CargoProfit;Amount;OriginSystem;OriginBase;TargetSystem;TargetBase;Trainable;Jumps;Commodity;Route");

		final int cargoSpace = Integer.parseInt(args[0]);

		final HashMap<SystemInfo, AINode> systemToAi = new HashMap<SystemInfo, AINode>();
		final HashMap<AINode, SystemInfo> AiToSystem = new HashMap<AINode, SystemInfo>();
		for (final SystemInfo system : systemHelper.getSystems().values()) {
			final AINode ai = new AINode();
			systemToAi.put(system, ai);
			AiToSystem.put(ai, system);
		}
		for (final SystemInfo system : systemHelper.getSystems().values()) {
			final AINode oNode = systemToAi.get(system);
			for (final SystemInfo tSystem : system.getJumpTargets()) {
				final AINode tNode = systemToAi.get(tSystem);
				oNode.connect(tNode);
			}
		}

		final AIPathfinder pathFinder = new AIPathfinder(new ArrayList<AINode>(systemToAi.values()));

		for (final SystemInfo originSystem : systemHelper.getSystems().values()) {
			System.out.println("Calculating " + originSystem.getName());
			for (final SystemInfo targetSystem : systemHelper.getSystems().values()) {
				// todo
				final AINode startAi = systemToAi.get(originSystem);
				final AINode targetAi = systemToAi.get(targetSystem);

				final ArrayList<AINode> route = pathFinder.dijkstra(startAi, targetAi);
				final int jumps = route.size();
				String routeString = "";
				for (final AINode ai : route) {
					final SystemInfo system = AiToSystem.get(ai);
					if (!routeString.isEmpty()) {
						routeString = routeString + "<-";
					}
					routeString += system.getName();
				}

				final boolean routestrange = routeString.contains(originSystem.getName()) && routeString.contains(targetSystem.getName());
				if (!routestrange) {
					continue;
				}

				for (final Base originBase : originSystem.getBase().values()) {
					for (final ItemInfo originData : originBase.getData()) {
						if (originData.sells) {
							Starter.calculateRoutes(originSystem, originBase, originData, targetSystem, goodsHelper, excelout, cargoSpace, jumps, routeString, nameHelper);
						}
					}
				}
			}
		}

		excelout.close();
	}

	private static void calculateRoutes(final SystemInfo originSystem, final Base originBase, final ItemInfo originData, final SystemInfo targetSystem, final GoodsHelper goodsHelper, final PrintStream excelout, final int cargoSpace, final int jumps,
			final String route, final NameHelper nameHelper) {
		for (final Base targetBase : targetSystem.getBase().values()) {
			final Integer sellprice = targetBase.getGood(originData.id, goodsHelper);

			final int profit = sellprice - originData.currentPrice;
			if (profit <= 0) {
				continue;
			}
			final boolean trainable = originBase.isTrain() && targetBase.isTrain();

			final float scale = goodsHelper.getScale(originData.getId());
			excelout.println(profit + ";" + (int) (cargoSpace * profit / scale) + ";" + (cargoSpace / scale) + ";" + originSystem.getName() + ";" + originBase.getObjectName() + ";" + targetSystem.getName() + ";" + targetBase.getObjectName() + ";"
					+ trainable + ";" + (jumps - 1) + ";" + goodsHelper.getName(originData.id) + ";" + route);
		}
	}
}
