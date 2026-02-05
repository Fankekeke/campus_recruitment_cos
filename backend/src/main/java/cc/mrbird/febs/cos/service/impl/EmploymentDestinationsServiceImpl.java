package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.EmploymentDestinations;
import cc.mrbird.febs.cos.dao.EmploymentDestinationsMapper;
import cc.mrbird.febs.cos.service.IEmploymentDestinationsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class EmploymentDestinationsServiceImpl extends ServiceImpl<EmploymentDestinationsMapper, EmploymentDestinations> implements IEmploymentDestinationsService {

    /**
     * 分页获取就业去向信息
     *
     * @param page                   分页对象
     * @param employmentDestinations 就业去向信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<EmploymentDestinations> page, EmploymentDestinations employmentDestinations) {
        return baseMapper.queryPage(page, employmentDestinations);
    }
}
