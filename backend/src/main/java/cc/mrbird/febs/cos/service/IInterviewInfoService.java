package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.InterviewInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 面试管理 service层
 *
 * @author FanK
 */
public interface IInterviewInfoService extends IService<InterviewInfo> {

    /**
     * 分页获取所属面试管理信息
     *
     * @param page          分页对象
     * @param interviewInfo 所属面试管理信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectInterviewPage(Page<InterviewInfo> page, InterviewInfo interviewInfo);

    /**
     * 获取主页统计数据
     *
     * @return 结果
     */
    LinkedHashMap<String, Object> homeData(Integer userId);

    /**
     * 年统计
     *
     * @param date 年份
     * @return 结果
     */
    LinkedHashMap<String, Object> selectStoreStatisticsByYear(String date);

    /**
     * 月统计
     *
     * @param date 日期
     * @return 结果
     */
    LinkedHashMap<String, Object> selectStoreStatisticsByMonth(String date);

    /**
     * 评价占比
     *
     * @return 评价占比
     */
    List<LinkedHashMap<String, Object>> queryEvaluateRate();

    /**
     * 区域占比
     *
     * @return 区域占比
     */
    List<LinkedHashMap<String, Object>> queryAreaScenicNumRate();

    /**
     * 等级占比
     *
     * @return 等级占比
     */
    List<LinkedHashMap<String, Object>> queryScenicLevelRate();

    /**
     * 薪资分布占比
     *
     * @return 价格分布占比
     */
    List<LinkedHashMap<String, Object>> queryPriceStepRate();

    /**
     * 订单词云
     *
     * @return 订单词云
     */
    List<LinkedHashMap<String, Object>> queryOrderWordCloud();
}
