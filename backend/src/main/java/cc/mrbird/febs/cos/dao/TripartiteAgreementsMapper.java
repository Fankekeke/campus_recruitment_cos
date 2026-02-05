package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.TripartiteAgreements;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface TripartiteAgreementsMapper extends BaseMapper<TripartiteAgreements> {

    /**
     * 分页获取三方协议信息
     *
     * @param page                 分页对象
     * @param tripartiteAgreements 三方协议信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<TripartiteAgreements> page, @Param("tripartiteAgreements") TripartiteAgreements tripartiteAgreements);
}
