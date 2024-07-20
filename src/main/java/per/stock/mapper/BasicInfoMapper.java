package per.stock.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BasicInfoMapper {

    /**
     * 查询所有股票代码
     *
     * @return List<String>
     */
    List<Map<String, String>> selectAll();


}
