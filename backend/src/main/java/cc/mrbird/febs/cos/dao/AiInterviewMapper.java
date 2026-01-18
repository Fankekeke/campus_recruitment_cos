package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.AiInterview;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * AI面试记录 mapper层
 *
 * @author FanK
 */
public interface AiInterviewMapper extends BaseMapper<AiInterview> {

    /**
     * 分页获取AI面试记录信息
     *
     * @param page        分页对象
     * @param aiInterview AI面试记录信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<AiInterview> page, @Param("aiInterview") AiInterview aiInterview);
}
