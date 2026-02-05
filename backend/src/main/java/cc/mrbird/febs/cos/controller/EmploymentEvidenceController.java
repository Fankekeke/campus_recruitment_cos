package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.EmploymentEvidence;
import cc.mrbird.febs.cos.entity.ExpertInfo;
import cc.mrbird.febs.cos.entity.TripartiteAgreements;
import cc.mrbird.febs.cos.service.IEmploymentEvidenceService;
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
@RequestMapping("/cos/employment-evidence")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmploymentEvidenceController {

    private final IEmploymentEvidenceService employmentEvidenceService;

    private final IExpertInfoService expertInfoService;

    /**
     * 分页获取就业证明材料信息
     *
     * @param page               分页对象
     * @param employmentEvidence 就业证明材料信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<EmploymentEvidence> page, EmploymentEvidence employmentEvidence) {
        return R.ok(employmentEvidenceService.queryPage(page, employmentEvidence));
    }

    /**
     * 查询就业证明材料信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(employmentEvidenceService.getById(id));
    }

    /**
     * 查询就业证明材料信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(employmentEvidenceService.list());
    }

    /**
     * 查询就业证明材料
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/queryByUser")
    public R queryByUser(Integer userId) {
        ExpertInfo expertInfo = expertInfoService.getOne(Wrappers.<ExpertInfo>lambdaQuery().eq(ExpertInfo::getUserId, userId));
        return R.ok(employmentEvidenceService.getOne(Wrappers.<EmploymentEvidence>lambdaQuery().eq(EmploymentEvidence::getStudentId, expertInfo.getId())));
    }

    /**
     * 新增就业证明材料信息
     *
     * @param employmentEvidence 就业证明材料信息
     * @return 结果
     */
    @PostMapping("/add")
    public R save(EmploymentEvidence employmentEvidence) {
        ExpertInfo expertInfo = expertInfoService.getOne(Wrappers.<ExpertInfo>lambdaQuery().eq(ExpertInfo::getUserId, employmentEvidence.getStudentId()));
        employmentEvidence.setStudentId(Long.valueOf(expertInfo.getId()));
        employmentEvidence.setCreateTime(DateUtil.formatDateTime(new Date()));
        return R.ok(employmentEvidenceService.save(employmentEvidence));
    }

    /**
     * 修改就业证明材料信息
     *
     * @param employmentEvidence 就业证明材料信息
     * @return 结果
     */
    @PostMapping("/update")
    public R edit(EmploymentEvidence employmentEvidence) {
        return R.ok(employmentEvidenceService.updateById(employmentEvidence));
    }

    /**
     * 删除就业证明材料信息
     *
     * @param ids ids
     * @return 就业证明材料信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(employmentEvidenceService.removeByIds(ids));
    }
}
