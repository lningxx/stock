package per.stock.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import per.stock.bean.KLineBean;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * day/week/month 三合一Mapper
 */
@Mapper
public interface KLineMapper {


    /**
     * 查询该股票最新的日k日期
     *
     * @param stockCode 股票代码
     * @return 最新日期
     */
    String queryLastDate(@Param("stockCode")String stockCode);

    /**
     * 批量新增日k线数据
     * @param list 待插入列表
     * @return 影响条数
     */
    int batchAddDayK(List<KLineBean> list);

    /**
     * 新增周k线数据
     * @param kLineBean
     * @return 影响条数
     */
    int addWeekK(KLineBean kLineBean);

    /**
     * 新增月k线数据
     * @param kLineBean
     * @return 影响条数
     */
    int addMonthK(KLineBean kLineBean);

    /**
     * 查询日k表中所有交易日期 --用于做存在判断
     *
     * @param code 股票代码
     * @return 日期集合
     */
    Set<String> queryDayKAllTransDate(@Param("code") String code);
}
