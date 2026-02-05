package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.TripartiteAgreements;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface ITripartiteAgreementsService extends IService<TripartiteAgreements> {

    /**
     * 分页获取三方协议信息
     *
     * @param page                 分页对象
     * @param tripartiteAgreements 三方协议信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<TripartiteAgreements> page, TripartiteAgreements tripartiteAgreements);
}
