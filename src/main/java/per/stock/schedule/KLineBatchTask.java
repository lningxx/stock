package per.stock.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.stock.component.KLineTask;
import per.stock.mapper.BasicInfoMapper;

import javax.annotation.Resource;

@Component
public class KLineBatchTask {

    @Resource
    KLineTask kLineTask;
    @Resource
    BasicInfoMapper basicInfoMapper;

    Logger logger = LoggerFactory.getLogger(KLineBatchTask.class);

    /**
     * <h2>日k线拉取任务</h2>
     * <p>拉取沪市所有股票日k线所需数据</p>
     * <p>定时：每个工作日下午五点：0 0 17 W * ?</p>
     *
     * @author lningxx
     * @since 0.1
     */
    @Scheduled(fixedDelay = 5000)
//    @Scheduled(cron = "0 0 17 W * ?")
    public void day(){
        kLineTask.execute("1","日k线拉取");
    }

    /**
     * <h2>周k线拉取任务</h2>
     * <p>拉取沪市所有股票周k线所需数据</p>
     * <p>每周一凌晨三点 0 0 3 ? * MON</p>
     *
     * @author lningxx
     * @since 0.1
     */
    // @Scheduled(cron = "0 0 3 ? * MON")
    public void week(){
        try{
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * <h2>月k线拉取任务</h2>
     * <p>拉取沪市所有股票月k线所需数据 </p>
     * <p>每月1日凌晨五点 0 0 5 1 * ?</p>
     *
     * @author lningxx
     * @since 0.1
     */
    //    @Scheduled(cron = "0 0 5 1 * ?")
    public void month(){
        try{
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
