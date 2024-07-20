package per.stock.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import per.stock.cache.Cache;
import per.stock.mapper.BasicInfoMapper;

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

    @Resource
    BasicInfoMapper basicInfoMapper;


    Logger logger = LoggerFactory.getLogger(Started.class);


    /**
     * 加载缓存
     */
    @PostConstruct
    public void loadCache(){
        logger.info("[缓存加载]开始加载缓存......");
        try{
            Cache.stockList = basicInfoMapper.selectAll();
            logger.info("[缓存加载]stockList总条数：{}", Cache.stockList.size());
        } catch (Exception e){
            logger.error("[缓存加载]缓存加载异常", e);
        }
        logger.info("[缓存加载]缓存加载已完成......");
    }


    @PostConstruct
    public void justWait() throws InterruptedException {
        Thread.sleep(10000);
        logger.info("wait.......");
    }
}

