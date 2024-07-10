package per.stock.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import per.stock.mapper.BasicInfoMapper;

import javax.annotation.Resource;

@Component
public class KLineBatchTask {

    @Resource
    BasicInfoMapper basicInfoMapper;

    @Scheduled(cron = "0/1 * * * * ?")
    public void day(){
        try{
            String x = basicInfoMapper.select();
            System.out.println(x);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
