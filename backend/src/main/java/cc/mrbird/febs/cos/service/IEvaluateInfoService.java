package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.EvaluateInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * 面试评价 service层
 *
 * @author FanK
 */
public interface IEvaluateInfoService extends IService<EvaluateInfo> {

    /**
     * 分页获取面试评价信息
     *
     * @param page         分页对象
     * @param evaluateInfo 面试评价信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<EvaluateInfo> page, EvaluateInfo evaluateInfo);
}
