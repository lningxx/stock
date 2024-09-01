package per.stock.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class KLineBean {

    private String code;
    private String transDate;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private String volume;
    private String amount;
    private Date createTime;
    private Date modifyTime;
}
