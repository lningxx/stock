package per.stock.schedule;

import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.stock.cache.Cache;
import per.stock.component.KLineTask;
import per.stock.enums.KLineEnum;

import javax.annotation.Resource;

@Component
public class KLineBatchTask {

    @Resource
    KLineTask kLineTask;
    @Resource
    Cache cache;

    /**
     * <h2>日k线拉取任务</h2>
     * <p>拉取沪市所有股票日k线所需数据</p>
     * <p>定时：每个工作日下午五点：0 0 17 W * ?</p>
     *
     * @author lningxx
     * @since 0.1
     */
   // @Scheduled(fixedDelay = 5000)
//    @Scheduled(cron = "0 0 17 W * ?")
    @DependsOn({"started"})
    public void day(){
        kLineTask.execute(KLineEnum.DAY_K);
    }

    /**
     * <h2>周k线拉取任务</h2>
     * <p>拉取沪市所有股票周k线所需数据</p>
     * <p>每周一凌晨三点 0 0 3 ? * MON</p>
     *
     * @author lningxx
     * @since 0.1
     */
   // @Scheduled(fixedDelay = 500000)
    // @Scheduled(cron = "0 0 3 ? * MON")
    public void week(){
        kLineTask.execute(KLineEnum.WEEK_K);
    }

    /**
     * <h2>月k线拉取任务</h2>
     * <p>拉取沪市所有股票月k线所需数据 </p>
     * <p>每月1日凌晨五点 0 0 5 1 * ?</p>
     *
     * @author lningxx
     * @since 0.1
     */
    @Scheduled(fixedDelay = 500000)
    //    @Scheduled(cron = "0 0 5 1 * ?")
    public void month(){
        kLineTask.execute(KLineEnum.MONTH_K);
    }

    /**
     * <h2>股票代码缓存信息刷新</h2>
     * <p>刷新缓存信息</p>
     * <p>每天凌晨零点 0 0 0 ? * MON</p>
     *
     * @author lningxx
     * @since 0.1
     */
    // @Scheduled(fixedDelay = 500000)
    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshStockListCache(){
        cache.loadStockList();
    }

}
