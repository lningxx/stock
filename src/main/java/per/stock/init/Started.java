package per.stock.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import per.stock.cache.Cache;
import per.stock.mapper.StockBasicInfMapper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 项目启动后执行的任务
 *
 * @author lningxx
 * @since 0.1
 */
@Component
public class Started {

    @Autowired
    Cache cache;

    /**
     * 加载缓存
     */
    @PostConstruct
    public void loadCache(){
        cache.loadStockList();
    }

}

