package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.AiInterview;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * AI面试记录 service层
 *
 * @author FanK
 */
public interface IAiInterviewService extends IService<AiInterview> {

    /**
     * 分页获取AI面试记录信息
     *
     * @param page        分页对象
     * @param aiInterview AI面试记录信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<AiInterview> page, AiInterview aiInterview);

    /**
     * 新增AI面试记录信息
     *
     * @param aiInterview AI面试记录信息
     * @return 结果
     */
    Boolean aiInterviewAdd(AiInterview aiInterview);
}
