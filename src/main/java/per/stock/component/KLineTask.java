package per.stock.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import per.stock.cache.Cache;
import per.stock.enums.KLineEnum;
import per.stock.enums.MarketEnum;

import javax.annotation.Resource;
import java.util.Map;

/**
 * k线数据拉取任务
 *
 * @author lningxx
 * @since 0.1
 */
@Component
public class KLineTask {

    @Resource
    SHTransDataComponent shTransDataComponent;

    Logger logger = LoggerFactory.getLogger(KLineTask.class);

    /**
     * 拉取任务执行方法
     *
     * @param kLineEnum k线类型枚举
     */
    public void execute(KLineEnum kLineEnum){
        // 日志开头
        String sign = "[股票"+kLineEnum.getName()+"数据拉取]";
        try{
            logger.info(sign + "开始......");
            // 1. 查出所有股票代码+市场
            logger.info(sign + "待处理股票：" + Cache.stockList);

            // 2. 逐一处理
            for (Map<String, String> stockMap : Cache.stockList){

                // 股票代码
                String stockCode = stockMap.get("code");
                // 股票市场
                String stockMarket = stockMap.get("stockMarket");

                logger.info(sign + "股票" + stockCode + "处理开始......");

                // 上海证券交易所处理
                if (MarketEnum.SHANGHAI.getShortEngName().equals(stockMarket)){
                    switch (kLineEnum){
                        case DAY_K:
                            shTransDataComponent.day(stockCode, sign);
                            break;
                        case WEEK_K:
                            shTransDataComponent.week(stockCode, sign);
                            break;
                        case MONTH_K:
                            shTransDataComponent.month(stockCode, sign);
                            break;
                        default:
                            break;
                    }
                }

                // 深圳证券交易所处理
                if (MarketEnum.SHENZHEN.getShortEngName().equals(stockMarket)){


                }

                // 北京证券交易所处理
                if (MarketEnum.BEIJING.getShortEngName().equals(stockMarket)){


                }

                logger.info(sign + "股票" + stockCode + "处理结束......");
            }

        } catch (Exception e){
            logger.error(sign + "异常：", e);
        }
        logger.info(sign + "结束......");
    }


}
