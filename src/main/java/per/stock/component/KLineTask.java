package per.stock.component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import per.stock.config.Constants;
import per.stock.util.HttpUtils;
import per.stock.bean.KLineBean;
import per.stock.mapper.BasicInfoMapper;
import per.stock.mapper.KLineMapper;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * k线数据拉取任务
 *
 * @author lningxx
 * @since 0.1
 */
@Component
public class KLineTask {

    Logger logger = LoggerFactory.getLogger(KLineTask.class);

    @Resource
    BasicInfoMapper basicInfoMapper;
    @Resource
    KLineMapper kLineMapper;

    // 上海证券交易所接口地址
    @Value("${stock.sh.url}")
    String stockShUrl;

    /**
     * 拉取任务执行方法
     *
     * @param type 0-day 1-week 2-month
     * @param sign 日志开头
     */
    public void execute(String type, String sign){
        // 日志开头
        sign = '[' + sign + ']';
        try{
            logger.info(sign + "开始......");
            // 1. 查出所有股票代码
            List<String> stockList = basicInfoMapper.selectAll();//todo
            logger.info(sign + "待处理股票：" + stockList.toString());

            // 2. 逐一处理
            for (String stockCode : stockList){
                logger.info(sign + "股票" + stockCode + "处理开始......");
                switch (type){
                    case "1":
                        day(stockCode, sign);
                        break;
                    case "2":
                        week(stockCode, sign);
                        break;
                    case "3":
                        month(stockCode, sign);
                        break;
                    default:
                        break;
                }
                logger.info(sign + "股票" + stockCode + "处理结束......");
            }

        } catch (Exception e){
            logger.error(sign + "异常：", e);
        }
        logger.info(sign + "结束......");
    }


    /**
     * 日k数据具体处理
     *
     * @param stockCode 股票代码
     * @param sign 日志开头
     */
    private void day(String stockCode, String sign) {
        try{
            // 1. 组装请求url
            // 日k线表中最新日期
            Date lastDate;
            String lastDateStr = kLineMapper.queryDayKLastDate(stockCode);
            logger.info(sign + "日k线表中最新日期" + lastDateStr);
            if (lastDateStr == null)
                lastDate = DateUtils.parseDate(Constants.CHN_STOCK_START_DATE, "yyyyMMdd");
            else
                lastDate = DateUtils.parseDate(lastDateStr, "yyyyMMdd");

            // 系统时间 - 日k线表中最新日期
            long interval = System.currentTimeMillis() - lastDate.getTime();
            // 开始日坐标
            int start = -1 - (int)(interval/DateUtils.MILLIS_PER_DAY) - 1;
            // 查询url
            String url = stockShUrl + "/v1/sh1/dayk/" + stockCode + "?begin=" + start + "&end=-1&period=day";

            // 2. 请求数据
            String respStr = HttpUtils.get(url);

            // 3. 处理返回数据
            if (StringUtils.isEmpty(respStr))
                return;
            // 查询结果json转换
            JSONObject jsonObject = JSONObject.parseObject(respStr);
            JSONArray jsonArray = jsonObject.getJSONArray("kline");

            // 批量插入List
            List<KLineBean> addList = new LinkedList<>();
            // 日期集合
            Set<String> set = kLineMapper.queryDayKAllTransDate(stockCode);
            for (int i = 0; i < jsonArray.size(); i++) {

                JSONArray singleDayArray = (JSONArray)jsonArray.get(i);
                // 验证当前交易日期是否已有数据
                if (set.contains(singleDayArray.getString(0)))
                    continue;

                // 组装日k线bean
                KLineBean kLineBean = new KLineBean();
                // 股票代码
                kLineBean.setCode(stockCode);
                // 交易日期
                kLineBean.setTransDate(singleDayArray.getString(0));
                // 开盘价
                kLineBean.setOpenPrice(new BigDecimal(singleDayArray.getString(1)));
                // 最高价
                kLineBean.setHighPrice(new BigDecimal(singleDayArray.getString(2)));
                // 最低价
                kLineBean.setLowPrice(new BigDecimal(singleDayArray.getString(3)));
                // 收盘价
                kLineBean.setClosePrice(new BigDecimal(singleDayArray.getString(4)));
                // 成交量（手）
                kLineBean.setVolume(singleDayArray.getString(5));
                // 成交额（元）
                kLineBean.setAmount(singleDayArray.getString(6));
                // 加入列表
                addList.add(kLineBean);

                // 每500条或当前是最后一条，进行批量插入
                if (addList.size() == 500 || i == jsonArray.size() - 1){
                    // 插入数据库条数
                    int num = kLineMapper.batchAddDayK(addList);
                    // 数量匹配判断
                    if (num == addList.size())
                        logger.info(sign + "股票代码[" + stockCode + "] 日期区间[" + addList.get(0).getTransDate() + "->" + addList.get(addList.size()-1).getTransDate() + "]条数：" + num + " k_line_day数据已插入...");
                    else
                        throw new RuntimeException("股票代码[" + stockCode + "] 日期区间[" + addList.get(0).getTransDate() + "->" + addList.get(addList.size()-1).getTransDate() +
                                "]k_line_day数据插入异常，已插条数:" + num + "实际条数：" + addList.size());
                    // 清空list
                    addList.clear();
                }
            }
        }catch (Exception e){
            logger.error(sign + "股票代码[" + stockCode + "]处理异常:", e);
        }
    }

    /**
     * 周k数据具体处理
     *
     * @param stockCode 股票代码
     */
    private void week(String stockCode, String sign) {
        try{
            // 1. 组装请求url
            // 周k线表中最新日期
            Date lastDate;
            String lastDateStr = kLineMapper.queryWeekKLastDate(stockCode);
            logger.info(sign + "周k线表中最新日期" + lastDateStr);
            if (lastDateStr == null)
                lastDate = DateUtils.parseDate(Constants.CHN_STOCK_START_DATE, "yyyyMMdd");
            else
                lastDate = DateUtils.parseDate(lastDateStr, "yyyyMMdd");

            // 系统时间 - 周k线表中最新日期
            long interval = System.currentTimeMillis() - lastDate.getTime();
            // 开始日坐标
            int start = -1 - (int)(interval/DateUtils.MILLIS_PER_DAY/7) - 1;
            // 查询url
            String url = stockShUrl + "/v1/sh1/dayk/" + stockCode + "?begin=" + start + "&end=-1&period=week";

            // 2. 请求数据
            String respStr = HttpUtils.get(url);

            // 3. 处理返回数据
            if (StringUtils.isEmpty(respStr))
                return;
            // 查询结果json转换
            JSONObject jsonObject = JSONObject.parseObject(respStr);
            JSONArray jsonArray = jsonObject.getJSONArray("kline");

            // 批量插入List
            List<KLineBean> addList = new LinkedList<>();
            // 日期集合
            Set<String> set = kLineMapper.queryWeekKAllTransDate(stockCode);
            for (int i = 0; i < jsonArray.size(); i++) {

                JSONArray singleDayArray = (JSONArray)jsonArray.get(i);
                // 验证当前交易日期是否已有数据
                if (set.contains(singleDayArray.getString(0)))
                    continue;

                // 组装周k线bean
                KLineBean kLineBean = new KLineBean();
                // 股票代码
                kLineBean.setCode(stockCode);
                // 交易日期
                kLineBean.setTransDate(singleDayArray.getString(0));
                // 开盘价
                kLineBean.setOpenPrice(new BigDecimal(singleDayArray.getString(1)));
                // 最高价
                kLineBean.setHighPrice(new BigDecimal(singleDayArray.getString(2)));
                // 最低价
                kLineBean.setLowPrice(new BigDecimal(singleDayArray.getString(3)));
                // 收盘价
                kLineBean.setClosePrice(new BigDecimal(singleDayArray.getString(4)));
                // 成交量（手）
                kLineBean.setVolume(singleDayArray.getString(5));
                // 成交额（元）
                kLineBean.setAmount(singleDayArray.getString(6));
                // 加入列表
                addList.add(kLineBean);

                // 每500条或当前是最后一条，进行批量插入
                if (addList.size() == 500 || i == jsonArray.size() - 1){
                    // 插入数据库条数
                    int num = kLineMapper.batchAddWeekK(addList);
                    // 数量匹配判断
                    if (num == addList.size())
                        logger.info(sign + "股票代码[" + stockCode + "] 日期区间[" + addList.get(0).getTransDate() + "->" + addList.get(addList.size()-1).getTransDate() + "]条数：" + num + " k_line_week数据已插入...");
                    else
                        throw new RuntimeException("股票代码[" + stockCode + "] 日期区间[" + addList.get(0).getTransDate() + "->" + addList.get(addList.size()-1).getTransDate() +
                                "]k_line_week数据插入异常，已插条数:" + num + "实际条数：" + addList.size());
                    // 清空list
                    addList.clear();
                }
            }
        }catch (Exception e){
            logger.error(sign + "股票代码[" + stockCode + "]处理异常:", e);
        }
    }

    /**
     * 月k数据具体处理
     *
     * @param stockCode 股票代码
     */
    private void month(String stockCode, String sign) {
        try{
            // 1. 组装请求url
            // 月k线表中最新日期
            Date lastDate;
            String lastDateStr = kLineMapper.queryMonthKLastDate(stockCode);
            logger.info(sign + "月k线表中最新日期" + lastDateStr);
            if (lastDateStr == null)
                lastDate = DateUtils.parseDate(Constants.CHN_STOCK_START_DATE, "yyyyMMdd");
            else
                lastDate = DateUtils.parseDate(lastDateStr, "yyyyMMdd");

            // 系统时间 - 月k线表中最新日期
            long interval = System.currentTimeMillis() - lastDate.getTime();
            // 开始日坐标
            int start = -1 - (int)(interval/DateUtils.MILLIS_PER_DAY/30) - 1;
            // 查询url
            String url = stockShUrl + "/v1/sh1/dayk/" + stockCode + "?begin=" + start + "&end=-1&period=month";

            // 2. 请求数据
            String respStr = HttpUtils.get(url);

            // 3. 处理返回数据
            if (StringUtils.isEmpty(respStr))
                return;
            // 查询结果json转换
            JSONObject jsonObject = JSONObject.parseObject(respStr);
            JSONArray jsonArray = jsonObject.getJSONArray("kline");

            // 批量插入List
            List<KLineBean> addList = new LinkedList<>();
            // 日期集合
            Set<String> set = kLineMapper.queryMonthKAllTransDate(stockCode);
            for (int i = 0; i < jsonArray.size(); i++) {

                JSONArray singleDayArray = (JSONArray)jsonArray.get(i);
                // 验证当前交易日期是否已有数据
                if (set.contains(singleDayArray.getString(0)))
                    continue;

                // 组装月k线bean
                KLineBean kLineBean = new KLineBean();
                // 股票代码
                kLineBean.setCode(stockCode);
                // 交易日期
                kLineBean.setTransDate(singleDayArray.getString(0));
                // 开盘价
                kLineBean.setOpenPrice(new BigDecimal(singleDayArray.getString(1)));
                // 最高价
                kLineBean.setHighPrice(new BigDecimal(singleDayArray.getString(2)));
                // 最低价
                kLineBean.setLowPrice(new BigDecimal(singleDayArray.getString(3)));
                // 收盘价
                kLineBean.setClosePrice(new BigDecimal(singleDayArray.getString(4)));
                // 成交量（手）
                kLineBean.setVolume(singleDayArray.getString(5));
                // 成交额（元）
                kLineBean.setAmount(singleDayArray.getString(6));
                // 加入列表
                addList.add(kLineBean);

                // 每500条或当前是最后一条，进行批量插入
                if (addList.size() == 500 || i == jsonArray.size() - 1){
                    // 插入数据库条数
                    int num = kLineMapper.batchAddMonthK(addList);
                    // 数量匹配判断
                    if (num == addList.size())
                        logger.info(sign + "股票代码[" + stockCode + "] 日期区间[" + addList.get(0).getTransDate() + "->" + addList.get(addList.size()-1).getTransDate() + "]条数：" + num + " k_line_month数据已插入...");
                    else
                        throw new RuntimeException("股票代码[" + stockCode + "] 日期区间[" + addList.get(0).getTransDate() + "->" + addList.get(addList.size()-1).getTransDate() +
                                "]k_line_month数据插入异常，已插条数:" + num + " 实际条数：" + addList.size());
                    // 清空list
                    addList.clear();
                }
            }
        }catch (Exception e){
            logger.error(sign + "股票代码[" + stockCode + "]处理异常:", e);
        }
    }
}
