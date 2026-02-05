package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.EmploymentDestinations;
import cc.mrbird.febs.cos.entity.EmploymentEvidence;
import cc.mrbird.febs.cos.entity.ExpertInfo;
import cc.mrbird.febs.cos.service.IEmploymentDestinationsService;
import cc.mrbird.febs.cos.service.IExpertInfoService;
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
 * @author FanK
 */
@RestController
@RequestMapping("/cos/employment-destinations")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmploymentDestinationsController {

    private final IEmploymentDestinationsService employmentDestinationsService;

    private final IExpertInfoService expertInfoService;

    /**
     * 分页获取就业去向信息
     *
     * @param page                   分页对象
     * @param employmentDestinations 就业去向信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<EmploymentDestinations> page, EmploymentDestinations employmentDestinations) {
        return R.ok();
    }

    /**
     * 查询就业去向信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(employmentDestinationsService.getById(id));
    }

    /**
     * 查询就业去向信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(employmentDestinationsService.list());
    }

    /**
     * 查询就业去向
     *
     * @param userId 用户ID
     * @return 结果
     */
    @GetMapping("/queryByUser")
    public R queryByUser(Integer userId) {
        ExpertInfo expertInfo = expertInfoService.getOne(Wrappers.<ExpertInfo>lambdaQuery().eq(ExpertInfo::getUserId, userId));
        return R.ok(employmentDestinationsService.getOne(Wrappers.<EmploymentDestinations>lambdaQuery().eq(EmploymentDestinations::getStudentId, expertInfo.getId())));
    }

    /**
     * 新增就业去向信息
     *
     * @param employmentDestinations 就业去向信息
     * @return 结果
     */
    @PostMapping("/add")
    public R save(EmploymentDestinations employmentDestinations) {
        ExpertInfo expertInfo = expertInfoService.getOne(Wrappers.<ExpertInfo>lambdaQuery().eq(ExpertInfo::getUserId, employmentDestinations.getStudentId()));
        employmentDestinations.setStudentId(Long.valueOf(expertInfo.getId()));
        employmentDestinations.setCreateTime(DateUtil.formatDateTime(new Date()));
        return R.ok(employmentDestinationsService.save(employmentDestinations));
    }

    /**
     * 修改就业去向信息
     *
     * @param employmentDestinations 就业去向信息
     * @return 结果
     */
    @PostMapping("/update")
    public R edit(EmploymentDestinations employmentDestinations) {
        return R.ok(employmentDestinationsService.updateById(employmentDestinations));
    }

    /**
     * 删除就业去向信息
     *
     * @param ids ids
     * @return 就业去向信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(employmentDestinationsService.removeByIds(ids));
    }
}
