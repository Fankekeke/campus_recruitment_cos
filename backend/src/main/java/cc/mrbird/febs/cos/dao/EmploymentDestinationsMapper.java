package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.EmploymentDestinations;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface EmploymentDestinationsMapper extends BaseMapper<EmploymentDestinations> {

    /**
     * 分页获取就业去向信息
     *
     * @param page                   分页对象
     * @param employmentDestinations 就业去向信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<EmploymentDestinations> page, @Param("employmentDestinations") EmploymentDestinations employmentDestinations);
}
