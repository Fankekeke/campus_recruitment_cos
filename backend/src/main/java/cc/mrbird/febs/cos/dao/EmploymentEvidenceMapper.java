package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.EmploymentEvidence;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface EmploymentEvidenceMapper extends BaseMapper<EmploymentEvidence> {

    /**
     * 分页获取就业证明材料信息
     *
     * @param page               分页对象
     * @param employmentEvidence 就业证明材料信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<EmploymentEvidence> page, @Param("employmentEvidence") EmploymentEvidence employmentEvidence);
}
