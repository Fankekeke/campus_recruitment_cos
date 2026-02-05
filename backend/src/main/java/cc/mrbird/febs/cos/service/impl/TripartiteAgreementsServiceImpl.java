package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.TripartiteAgreements;
import cc.mrbird.febs.cos.dao.TripartiteAgreementsMapper;
import cc.mrbird.febs.cos.service.ITripartiteAgreementsService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class TripartiteAgreementsServiceImpl extends ServiceImpl<TripartiteAgreementsMapper, TripartiteAgreements> implements ITripartiteAgreementsService {

    /**
     * 分页获取三方协议信息
     *
     * @param page                 分页对象
     * @param tripartiteAgreements 三方协议信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<TripartiteAgreements> page, TripartiteAgreements tripartiteAgreements) {
        return baseMapper.queryPage(page, tripartiteAgreements);
    }
}
