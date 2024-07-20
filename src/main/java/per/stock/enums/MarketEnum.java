package per.stock.enums;

/**
 * 证券交易市场枚举
 *
 * @author lningxx
 * @since 0.1
 */
public enum MarketEnum {

    SHANGHAI("上海证券交易所", "SH"),
    SHENZHEN("深圳证券交易所", "SZ"),
    BEIJING("北京证券交易所", "BJ");

    // 市场名称
    private String name;
    // 英文简称
    private String shortEngName;

    MarketEnum(String name, String shortEngName) {
        this.name = name;
        this.shortEngName = shortEngName;
    };

    public String getName() {
        return name;
    }

    public String getShortEngName() {
        return shortEngName;
    }
}
