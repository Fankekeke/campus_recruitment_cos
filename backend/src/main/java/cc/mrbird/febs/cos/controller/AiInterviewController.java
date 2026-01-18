package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AiInterview;
import cc.mrbird.febs.cos.service.IAiInterviewService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * AI面试记录 控制层
 *
 * @author FanK
 */
@RestController
@RequestMapping("/cos/ai-interview")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AiInterviewController {

    private final IAiInterviewService aiInterviewService;

    /**
     * 分页获取AI面试记录信息
     *
     * @param page        分页对象
     * @param aiInterview AI面试记录信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AiInterview> page, AiInterview aiInterview) {
        return R.ok(aiInterviewService.queryPage(page, aiInterview));
    }

    /**
     * 查询AI面试记录信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(aiInterviewService.getById(id));
    }

    /**
     * 查询AI面试记录信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(aiInterviewService.list());
    }

    /**
     * 新增AI面试记录信息
     *
     * @param aiInterview AI面试记录信息
     * @return 结果
     */
    @PostMapping
    public R save(AiInterview aiInterview) {
        return R.ok(aiInterviewService.aiInterviewAdd(aiInterview));
    }

    /**
     * 修改AI面试记录信息
     *
     * @param aiInterview AI面试记录信息
     * @return 结果
     */
    @PutMapping
    public R edit(AiInterview aiInterview) {
        return R.ok(aiInterviewService.updateById(aiInterview));
    }

    /**
     * 删除AI面试记录信息
     *
     * @param ids ids
     * @return AI面试记录信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(aiInterviewService.removeByIds(ids));
    }
}
