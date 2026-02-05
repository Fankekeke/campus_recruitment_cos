package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.EmploymentEvidence;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IEmploymentEvidenceService extends IService<EmploymentEvidence> {

    /**
     * 分页获取就业证明材料信息
     *
     * @param page               分页对象
     * @param employmentEvidence 就业证明材料信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<EmploymentEvidence> page, EmploymentEvidence employmentEvidence);
}
