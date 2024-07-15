package per.stock.util;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HTTP请求类封装
 *
 * @author lningxx
 * @since 0.1
 */
public class HttpUtils {

    public static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * GET请求封装
     *
     * @author lningxx
     * @since 0.1
     */
    public static String get(String url) {
        logger.info("[http#GET]请求url：" + url);

        // http客户端
        CloseableHttpClient httpClient = null;
        // http get请求
        HttpGet httpGet = null;
        // http响应
        CloseableHttpResponse response = null;
        // 响应状态
        int statusCode = -1;
        // 响应内容
        String respContent = "";

        try {
            // 创建Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
            httpClient = HttpClientBuilder.create().build();
            // 创建Get请求
            httpGet = new HttpGet(url);
            // 设置请求参数
            RequestConfig requestConfig = RequestConfig.custom()
                    // 设置连接超时时间(单位毫秒)
                    .setConnectTimeout(5000)
                    // 设置请求超时时间(单位毫秒)
                    .setConnectionRequestTimeout(5000)
                    // socket读写超时时间(单位毫秒)
                    .setSocketTimeout(5000)
                    // 设置是否允许重定向(默认为true)
                    .setRedirectsEnabled(true).build();
            // 设置get请求参数
            httpGet.setConfig(requestConfig);

            // 执行get请求
            response = httpClient.execute(httpGet);

            // 解析http响应
            HttpEntity responseEntity;
            if (response !=null && (responseEntity = response.getEntity()) != null) {
                // 响应状态
                statusCode = response.getStatusLine().getStatusCode();
                // 响应内容
                respContent = EntityUtils.toString(responseEntity);
            }
        } catch (ParseException|IOException e) {
            logger.error("[http#GET]请求发生异常：", e);
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("[http#GET]流关闭发生异常：", e);
            }
        }
        logger.info("[http#GET]响应状态:" + statusCode);
        logger.info("[http#GET]响应内容:" + respContent);
        return respContent;
    }
}
