package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.TripartiteAgreements;
import cc.mrbird.febs.cos.entity.ExpertInfo;
import cc.mrbird.febs.cos.service.ITripartiteAgreementsService;
import cc.mrbird.febs.cos.service.IExpertInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/tripartite-agreements")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TripartiteAgreementsController {

    private final ITripartiteAgreementsService tripartiteAgreementsService;

    private final IExpertInfoService expertInfoService;

    /**
     * 分页获取三方协议信息
     *
     * @param page                 分页对象
     * @param tripartiteAgreements 三方协议信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<TripartiteAgreements> page, TripartiteAgreements tripartiteAgreements) {
        return R.ok(tripartiteAgreementsService.queryPage(page, tripartiteAgreements));
    }

    /**
     * 查询三方协议信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/queryByUser")
    public R queryByUser(Integer userId) {
        ExpertInfo expertInfo = expertInfoService.getOne(Wrappers.<ExpertInfo>lambdaQuery().eq(ExpertInfo::getUserId, userId));
        return R.ok(tripartiteAgreementsService.getOne(Wrappers.<TripartiteAgreements>lambdaQuery().eq(TripartiteAgreements::getStudentId, expertInfo.getId())));
    }

    /**
     * 查询三方协议信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(tripartiteAgreementsService.getById(id));
    }

    /**
     * 查询三方协议信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(tripartiteAgreementsService.list());
    }

    /**
     * 新增三方协议信息
     *
     * @param tripartiteAgreements 三方协议信息
     * @return 结果
     */
    @PostMapping("/add")
    public R save(TripartiteAgreements tripartiteAgreements) {
        ExpertInfo expertInfo = expertInfoService.getOne(Wrappers.<ExpertInfo>lambdaQuery().eq(ExpertInfo::getUserId, tripartiteAgreements.getStudentId()));
        tripartiteAgreements.setStudentId(Long.valueOf(expertInfo.getId()));
        tripartiteAgreements.setUploadTime(DateUtil.formatDateTime(new Date()));
        return R.ok(tripartiteAgreementsService.save(tripartiteAgreements));
    }

    /**
     * 修改三方协议信息
     *
     * @param tripartiteAgreements 三方协议信息
     * @return 结果
     */
    @PostMapping("/update")
    public R edit(TripartiteAgreements tripartiteAgreements) {
        return R.ok(tripartiteAgreementsService.updateById(tripartiteAgreements));
    }

    /**
     * 删除三方协议信息
     *
     * @param ids ids
     * @return 三方协议信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(tripartiteAgreementsService.removeByIds(ids));
    }
}
