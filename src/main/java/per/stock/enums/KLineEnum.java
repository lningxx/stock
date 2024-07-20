package per.stock.enums;

/**
 * k线类型枚举
 *
 * @author lningxx
 * @since 0.1
 */
public enum KLineEnum {

    DAY_K("1", "日k线"),
    WEEK_K("2", "周k线"),
    MONTH_K("3", "月k线");

    // k线类型代码
    private final String type;
    // k线名称
    private final String name;

    KLineEnum(String type, String name) {
        this.type = type;
        this.name = name;
    };

    public String getName() {
        return name;
    }

}
