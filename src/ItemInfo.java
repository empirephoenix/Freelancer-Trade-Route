public class ItemInfo {
	String			id;
	boolean			sells;
	int				currentPrice;
	private Integer	goodBasePrice;

	public ItemInfo(final String id, final boolean sells, final int currentPrice, final Integer goodBasePrice) {
		this.id = id;
		this.sells = sells;
		this.currentPrice = currentPrice;
		this.goodBasePrice = goodBasePrice;
	}

	public String getId() {
		return this.id;
	}

	public boolean isSells() {
		return this.sells;
	}

	public int getCurrentPrice() {
		return this.currentPrice;
	}

	public Integer getGoodBasePrice() {
		return this.goodBasePrice;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ItemInfo [");
		if (this.id != null) {
			builder.append("id=");
			builder.append(this.id);
			builder.append(", ");
		}
		builder.append("sells=");
		builder.append(this.sells);
		builder.append(", currentPrice=");
		builder.append(this.currentPrice);
		builder.append("]");
		return builder.toString();
	}

}