package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.EmploymentEvidence;
import cc.mrbird.febs.cos.dao.EmploymentEvidenceMapper;
import cc.mrbird.febs.cos.service.IEmploymentEvidenceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class EmploymentEvidenceServiceImpl extends ServiceImpl<EmploymentEvidenceMapper, EmploymentEvidence> implements IEmploymentEvidenceService {

    /**
     * 分页获取就业证明材料信息
     *
     * @param page               分页对象
     * @param employmentEvidence 就业证明材料信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<EmploymentEvidence> page, EmploymentEvidence employmentEvidence) {
        return baseMapper.queryPage(page, employmentEvidence);
    }
}
