package per.stock.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import per.stock.init.Started;
import per.stock.mapper.StockBasicInfMapper;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 缓存类
 *
 * @author lningxx
 * @since 0.1
 */
@Component
public class Cache {

    Logger logger = LoggerFactory.getLogger(Cache.class);

    @Resource
    StockBasicInfMapper stockBasicInfMapper;


    // 股票代码+市场
    public static List<Map<String, String>> stockList = new LinkedList<>();


    /**
     * 股票代码信息加载缓存
     * @author lningxx
     * @since 0.0.1
     */
    public void loadStockList(){
        logger.info("[缓存加载]开始加载缓存......");
        try{
            logger.info("[缓存加载]stockList 加载开始......");
            stockList = stockBasicInfMapper.selectAll();
            logger.info("[缓存加载]stockList 加载完成......总条数：{}", Cache.stockList.size());
        } catch (Exception e){
            logger.error("[缓存加载]缓存加载异常", e);
        }
        logger.info("[缓存加载]缓存加载已完成......");
    }
}
