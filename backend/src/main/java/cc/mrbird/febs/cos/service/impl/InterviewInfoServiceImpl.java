package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.dao.InterviewInfoMapper;
import cc.mrbird.febs.cos.service.*;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 面试管理 实现层
 *
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InterviewInfoServiceImpl extends ServiceImpl<InterviewInfoMapper, InterviewInfo> implements IInterviewInfoService {

    private final IEnterpriseInfoService enterpriseInfoService;

    private final IBulletinInfoService bulletinInfoService;

    private final IPostInfoService postInfoService;

    private final IAiInterviewService aiInterviewService;

    private static final Pattern MATCH_SCORE_PATTERN = Pattern.compile("综合匹配度评分.*?(\\d+)");

    /**
     * 分页获取所属面试管理信息
     *
     * @param page          分页对象
     * @param interviewInfo 所属面试管理信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectInterviewPage(Page<InterviewInfo> page, InterviewInfo interviewInfo) {
        return baseMapper.selectInterviewPage(page, interviewInfo);
    }

    /**
     * 获取主页统计数据
     *
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> homeData(Integer userId) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("orderNum", 0);
                put("orderPrice", 0);
                put("staffNum", 0);
                put("memberNum", 0);
            }
        };

        if (userId != null) {
            // 获取企业信息
            EnterpriseInfo enterpriseInfo = enterpriseInfoService.getOne(Wrappers.<EnterpriseInfo>lambdaQuery().eq(EnterpriseInfo::getUserId, userId));
            if (enterpriseInfo != null) {
                userId = enterpriseInfo.getId();
            }
        }

        // 本月预约数量
        List<ReserveInfo> orderMonthList = baseMapper.selectOrderByMonth(userId);
        result.put("monthOrderNum", CollectionUtil.isEmpty(orderMonthList) ? 0 : orderMonthList.size());
        // 获取本月面试投递
        List<InterviewInfo> interviewMonthList = baseMapper.selectInterviewByMonth(userId);
        result.put("monthOrderTotal", CollectionUtil.isEmpty(interviewMonthList) ? 0 : interviewMonthList.size());

        // 本年预约数量
        List<ReserveInfo> orderYearList = baseMapper.selectOrderByYear(userId);
        result.put("yearOrderNum", CollectionUtil.isEmpty(orderYearList) ? 0 : orderYearList.size());
        // 本年面试投递
        List<InterviewInfo> interviewYearList = baseMapper.selectInterviewByYear(userId);
        result.put("yearOrderTotal", CollectionUtil.isEmpty(interviewYearList) ? 0 : interviewYearList.size());

        // 近十天预约统计
        result.put("orderNumDayList", baseMapper.selectOrderNumWithinDays(userId));
        // 近十天面试统计
        result.put("priceDayList", baseMapper.selectOrderPriceWithinDays(userId));

        // 公告信息
        result.put("bulletinInfoList", bulletinInfoService.list(Wrappers.<BulletinInfo>lambdaQuery().eq(BulletinInfo::getRackUp, 1)));

        return result;
    }

    /**
     * 年统计订单及收益
     *
     * @param date 年份
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectStoreStatisticsByYear(String date) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        int year =  DateUtil.year(new Date());
        if (StrUtil.isNotEmpty(date)) {
            year = Integer.parseInt(date);
        }

        List<InterviewInfo> scenicOrderList = this.list(new LambdaQueryWrapper<InterviewInfo>().apply("DATE_FORMAT(create_date, '%Y') = {0}", year));
        for (InterviewInfo scenicOrder : scenicOrderList) {
            scenicOrder.setMonth(DateUtil.month(DateUtil.parseDate(scenicOrder.getCreateDate())) + 1);
        }
        Map<Integer, List<InterviewInfo>> orderOutMonthMap = scenicOrderList.stream().collect(Collectors.groupingBy(InterviewInfo::getMonth));
        List<AiInterview> hotelOrderList = aiInterviewService.list(new LambdaQueryWrapper<AiInterview>().apply("DATE_FORMAT(completion_time, '%Y') = {0}", year));
        for (AiInterview orderInfo : hotelOrderList) {
            // 提取AI分数
            if (StrUtil.isNotEmpty(orderInfo.getAiRemark())) {
                // 使用预编译的正则表达式常量
                Matcher matcher = MATCH_SCORE_PATTERN.matcher(orderInfo.getAiRemark());
                if (matcher.find()) {
                    try {
                        int score = Integer.parseInt(matcher.group(1));
                        orderInfo.setScore(score);
                    } catch (NumberFormatException e) {
                        // 处理解析分数时可能出现的异常
                        orderInfo.setScore(0);
                    }
                }
            } else {
                orderInfo.setScore(0);
            }
            orderInfo.setMonth(DateUtil.month(DateUtil.parseDate(orderInfo.getCompletionTime())) + 1);
        }
        Map<Integer, List<AiInterview>> orderPutMonthMap = hotelOrderList.stream().collect(Collectors.groupingBy(AiInterview::getMonth));

        result.put("orderNum", scenicOrderList.size());
        BigDecimal totalPrice = BigDecimal.valueOf(scenicOrderList.stream().filter(e -> (4 == e.getStatus() || 5 == e.getStatus())).count());
        result.put("totalPrice", totalPrice);
        result.put("putNum", hotelOrderList.size());
        // 计算平均分
        double averageScore = hotelOrderList.stream()
                .mapToInt(AiInterview::getScore)
                .average()
                .orElse(0.0);
        result.put("outlayPrice", BigDecimal.valueOf(averageScore));

        List<Integer> orderNumList = new ArrayList<>();
        List<BigDecimal> orderPriceList = new ArrayList<>();
        List<Integer> outlayNumList = new ArrayList<>();
        List<BigDecimal> outlayPriceList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {

            List<InterviewInfo> currentMonthOutList = orderOutMonthMap.get(i);
            if (CollectionUtil.isEmpty(currentMonthOutList)) {
                orderNumList.add(0);
                orderPriceList.add(BigDecimal.ZERO);
            } else {
                orderNumList.add(currentMonthOutList.size());
                BigDecimal currentMonthOutPrice = BigDecimal.valueOf(currentMonthOutList.stream().filter(e -> (4 == e.getStatus() || 5 == e.getStatus())).count());
                orderPriceList.add(currentMonthOutPrice);
            }

            List<AiInterview> currentMonthPutList = orderPutMonthMap.get(i);
            if (CollectionUtil.isEmpty(currentMonthPutList)) {
                outlayNumList.add(0);
                outlayPriceList.add(BigDecimal.ZERO);
            } else {
                outlayNumList.add(currentMonthPutList.size());
                double currentMonthPutPrice = currentMonthPutList.stream()
                        .mapToInt(AiInterview::getScore)
                        .average()
                        .orElse(0.0);
                outlayPriceList.add(BigDecimal.valueOf(currentMonthPutPrice));
            }

        }
        result.put("orderPriceList", orderPriceList);
        result.put("orderNumList", orderNumList);
        result.put("outlayPriceList", outlayPriceList);
//        result.put("outlayNumList", orderNumList);
        result.put("outlayNumList", outlayNumList);

        // 岗位销量排行
        List<LinkedHashMap<String, Object>> saleRank = new ArrayList<>();
        List<LinkedHashMap<String, Object>> salePriceRank = new ArrayList<>();
        LinkedHashMap<String, Integer> saleTypeRankMap = new LinkedHashMap<>();

        Map<Integer, List<InterviewInfo>> recordInfoMap = scenicOrderList.stream().collect(Collectors.groupingBy(InterviewInfo::getBaseId));

        // 岗位信息
        Set<Integer> scenicCodeList = recordInfoMap.keySet();
        List<PostInfo> scenicInfoList = postInfoService.list(Wrappers.<PostInfo>lambdaQuery().in(CollectionUtil.isNotEmpty(scenicCodeList), PostInfo::getId, scenicCodeList));
        Map<Integer, PostInfo> scenicMap = scenicInfoList.stream().collect(Collectors.toMap(PostInfo::getId, e -> e));
        List<String> scenicTypeList = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8");

        recordInfoMap.forEach((key, value) -> {
            PostInfo scenic= scenicMap.get(key);
            if (scenic != null) {
                saleRank.add(new LinkedHashMap<String, Object>() {
                    {
                        put("name", scenic.getPostName());
                        put("num", value.size());
                    }
                });
                salePriceRank.add(new LinkedHashMap<String, Object>() {
                    {
                        put("name", scenic.getPostName());
                        put("num", value.stream().filter(e -> (4 == e.getStatus() || 5 == e.getStatus())).count());
                    }
                });

                saleTypeRankMap.merge(StrUtil.toString(scenic.getAcademic()), value.size(), Integer::sum);
            }
        });
        result.put("saleRank", saleRank);
        result.put("salePriceRank", salePriceRank);
        // 销售岗位分类
        LinkedHashMap<String, Integer> saleTypeRankMapCopy = new LinkedHashMap<>();
        Map<String, String> academicMapping = new HashMap<>();
        academicMapping.put("1", "小学");
        academicMapping.put("2", "初中");
        academicMapping.put("3", "高中");
        academicMapping.put("4", "大专");
        academicMapping.put("5", "本科");
        academicMapping.put("6", "硕士");
        academicMapping.put("7", "博士");
        academicMapping.put("8", "不限");

        for (String level : scenicTypeList) {
            String chineseLevel = academicMapping.getOrDefault(level, "未知");
            saleTypeRankMapCopy.put(chineseLevel, saleTypeRankMap.get(level) == null ? 0 : saleTypeRankMap.get(level));
        }
        result.put("saleTypeRankMapCopy", saleTypeRankMapCopy);
        return result;
    }

    /**
     * 月统计订单及收益
     *
     * @param dateStr 日期
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> selectStoreStatisticsByMonth(String dateStr) {
        String date = dateStr + "-01";
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        int year =  DateUtil.year(new Date());
        int month =  DateUtil.month(new Date()) + 1;
        if (StrUtil.isNotEmpty(date)) {
            year = DateUtil.year(DateUtil.parseDate(date));
            month = DateUtil.month(DateUtil.parseDate(date)) + 1;
        }

        List<InterviewInfo> scenicOrderList = this.list(new LambdaQueryWrapper<InterviewInfo>().apply("DATE_FORMAT(create_date, '%Y%m') = {0}", (year + "" + ((month < 10) ? "0" + month : month))));
        for (InterviewInfo scenicOrder : scenicOrderList) {
            scenicOrder.setMonth(DateUtil.month(DateUtil.parseDate(scenicOrder.getCreateDate())) + 1);
            scenicOrder.setDay(DateUtil.dayOfMonth(DateUtil.parseDate(scenicOrder.getCreateDate())));
        }
        Map<Integer, List<InterviewInfo>> orderOutDayMap = scenicOrderList.stream().collect(Collectors.groupingBy(InterviewInfo::getDay));
        List<AiInterview> hotelOrderList = aiInterviewService.list(new LambdaQueryWrapper<AiInterview>().apply("DATE_FORMAT(completion_time, '%Y%m') = {0}", (year + "" + ((month < 10) ? "0" + month : month))));
        for (AiInterview orderInfo : hotelOrderList) {
            orderInfo.setMonth(DateUtil.month(DateUtil.parseDate(orderInfo.getCompletionTime())) + 1);
            orderInfo.setDay(DateUtil.dayOfMonth(DateUtil.parseDate(orderInfo.getCompletionTime())));
        }
        Map<Integer, List<AiInterview>> orderPutDayMap = hotelOrderList.stream().collect(Collectors.groupingBy(AiInterview::getDay));

        // 本月订单量
        result.put("orderNum", scenicOrderList.size());
        // 本月总收益
        BigDecimal totalPrice = BigDecimal.valueOf(scenicOrderList.stream().filter(e -> 4 == e.getStatus() || 5 == e.getStatus()).count());
        result.put("totalPrice", totalPrice);
        result.put("putNum", hotelOrderList.size());
        // 计算平均分
        double averageScore = hotelOrderList.stream()
                .mapToInt(AiInterview::getScore)
                .average()
                .orElse(0.0);
        result.put("outlayPrice", averageScore);

        List<Integer> orderNumList = new ArrayList<>();
        List<BigDecimal> orderPriceList = new ArrayList<>();
        List<Integer> outlayNumList = new ArrayList<>();
        List<BigDecimal> outlayPriceList = new ArrayList<>();
        int days = DateUtil.getLastDayOfMonth(DateUtil.parseDate(date));

        // 本月日期
        List<String> dateTimeList = new ArrayList<>();

        for (int i = 1; i <= days; i++) {
            dateTimeList.add(month + "月" + i + "日");
            List<InterviewInfo> currentDayOutList = orderOutDayMap.get(i);
            if (CollectionUtil.isEmpty(currentDayOutList)) {
                orderNumList.add(0);
                orderPriceList.add(BigDecimal.ZERO);
            } else {
                orderNumList.add(currentDayOutList.size());
                BigDecimal currentDayOutPrice = BigDecimal.valueOf(currentDayOutList.stream().filter(e -> 4 == e.getStatus() || 5 == e.getStatus()).count());
                orderPriceList.add(currentDayOutPrice);
            }

            // 本天入库
            List<AiInterview> currentDayPutList = orderPutDayMap.get(i);
            if (CollectionUtil.isEmpty(currentDayPutList)) {
                outlayNumList.add(0);
                outlayPriceList.add(BigDecimal.ZERO);
            } else {
                outlayNumList.add(currentDayPutList.size());
                BigDecimal currentDayPutPrice = BigDecimal.valueOf(currentDayPutList.stream().filter(e -> e.getScore() > 80).count());
                outlayPriceList.add(currentDayPutPrice);
            }

        }
        result.put("orderPriceList", orderPriceList);
        result.put("orderNumList", orderNumList);
        result.put("outlayPriceList", outlayPriceList);
//        result.put("outlayNumList", orderNumList);
        result.put("outlayNumList", outlayNumList);

        result.put("dateList", dateTimeList);
        // 岗位销量排行
        List<LinkedHashMap<String, Object>> saleRank = new ArrayList<>();
        List<LinkedHashMap<String, Object>> salePriceRank = new ArrayList<>();
        LinkedHashMap<String, Integer> saleTypeRankMap = new LinkedHashMap<>();

        Map<Integer, List<InterviewInfo>> recordInfoMap = scenicOrderList.stream().collect(Collectors.groupingBy(InterviewInfo::getBaseId));

        // 岗位信息
        Set<Integer> scenicIdList = recordInfoMap.keySet();
        List<PostInfo> scenicInfoList = postInfoService.list(Wrappers.<PostInfo>lambdaQuery().in(CollectionUtil.isNotEmpty(scenicIdList), PostInfo::getId, scenicIdList));
        Map<Integer, PostInfo> scenicMap = scenicInfoList.stream().collect(Collectors.toMap(PostInfo::getId, e -> e));

        recordInfoMap.forEach((key, value) -> {
            PostInfo scenicInfo = scenicMap.get(key);
            if (scenicInfo != null) {
                saleRank.add(new LinkedHashMap<String, Object>() {
                    {
                        put("name", scenicInfo.getPostName());
                        put("num", value.size());
                    }
                });
                salePriceRank.add(new LinkedHashMap<String, Object>() {
                    {
                        put("name", scenicInfo.getPostName());
                        put("num", value.stream().filter(e -> 4 == e.getStatus() || 5 == e.getStatus()).count());
                    }
                });

                saleTypeRankMap.merge(StrUtil.toString(scenicInfo.getAcademic()), value.size(), Integer::sum);
            }
        });
        result.put("saleRank", saleRank);
        result.put("salePriceRank", salePriceRank);
        // 销售岗位分类
        LinkedHashMap<String, Integer> saleTypeRankMapCopy = new LinkedHashMap<>();
        List<String> scenicTypeList = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8");
        Map<String, String> academicMapping = new HashMap<>();
        academicMapping.put("1", "小学");
        academicMapping.put("2", "初中");
        academicMapping.put("3", "高中");
        academicMapping.put("4", "大专");
        academicMapping.put("5", "本科");
        academicMapping.put("6", "硕士");
        academicMapping.put("7", "博士");
        academicMapping.put("8", "不限");

        for (String level : scenicTypeList) {
            String chineseLevel = academicMapping.getOrDefault(level, "未知");
            saleTypeRankMapCopy.put(chineseLevel, saleTypeRankMap.get(level) == null ? 0 : saleTypeRankMap.get(level));
        }
        result.put("saleTypeRankMapCopy", saleTypeRankMapCopy);
        return result;
    }

    /**
     * 岗位评价占比
     *
     * @return 评价占比
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryEvaluateRate() {
        // 获取所有岗位信息
        List<PostInfo> postInfoList = postInfoService.list();
        int totalCount = postInfoList.size();

        // 定义学历映射
        Map<Integer, String> academicMap = new HashMap<>();
        academicMap.put(1, "小学");
        academicMap.put(2, "初中");
        academicMap.put(3, "高中");
        academicMap.put(4, "大专");
        academicMap.put(5, "本科");
        academicMap.put(6, "硕士");
        academicMap.put(7, "博士");
        academicMap.put(8, "不限");
        // 统计各学历要求的数量
        Map<String, Integer> academicCountMap = new HashMap<>();
        for (PostInfo postInfo : postInfoList) {
            Integer academic = postInfo.getAcademic();
            if (academic != null) {
                String academicDesc = academicMap.get(academic);
                if (academicDesc != null) {
                    academicCountMap.merge(academicDesc, 1, Integer::sum);
                } else {
                    // 如果学历值不在预定义范围内，默认为"未知"
                    academicCountMap.merge("未知", 1, Integer::sum);
                }
            } else {
                // 如果学历为空，默认为"不限"
                academicCountMap.merge("不限", 1, Integer::sum);
            }
        }
        // 转换为结果格式，包含占比
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : academicCountMap.entrySet()) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("academic", entry.getKey());  // 学历要求
            map.put("count", entry.getValue());   // 数量
            map.put("percentage", (double) entry.getValue() / totalCount * 100);  // 占比
            result.add(map);
        }

        // 按数量降序排列
        result.sort((a, b) -> (int)b.get("count") - (int)a.get("count"));
        return result;
    }

    /**
     * 岗位区域占比
     *
     * @return 区域占比
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryAreaScenicNumRate() {
        // 获取所有岗位信息
        List<PostInfo> postInfoList = postInfoService.list();
        int totalCount = postInfoList.size();

        // 提取城市并统计数量
        Map<String, Integer> cityCountMap = new HashMap<>();
        for (PostInfo postInfo : postInfoList) {
            String address = postInfo.getAddress();
            if (StrUtil.isNotEmpty(address)) {
                String city = extractCity(address);
                if (StrUtil.isNotEmpty(city)) {
                    cityCountMap.merge(city, 1, Integer::sum);
                }
            }
        }
        // 转换为结果格式，包含占比
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : cityCountMap.entrySet()) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("city", entry.getKey());
            map.put("count", entry.getValue());
            map.put("percentage", (double) entry.getValue() / totalCount * 100);
            result.add(map);
        }
        // 按数量降序排列
        result.sort((a, b) -> (int)b.get("count") - (int)a.get("count"));
        return result;
    }

    /**
     * 从地址中提取城市
     *
     * @param address 完整地址
     * @return 城市名称
     */
    private String extractCity(String address) {
        if (StrUtil.isEmpty(address)) {
            return null;
        }

        if (address.contains(" - ") || address.contains("-")) {
            String[] parts = address.split("[- ]+-[ -]+|[- ]+");
            if (parts.length > 0) {
                String cityPart = parts[0];
                return normalizeCityName(cityPart);
            }
        }
        if (address.contains(",")) {
            String[] parts = address.split(",");
            if (parts.length > 0) {
                String cityPart = parts[0];
                return normalizeCityName(cityPart);
            }
        }
        if (address.length() >= 2) {
            // 检查是否包含直辖市
            if (address.startsWith("北京") || address.startsWith("上海") ||
                    address.startsWith("天津") || address.startsWith("重庆")) {
                if (address.startsWith("北京")) {
                    return "北京";
                } else if (address.startsWith("上海")) {
                    return "上海";
                } else if (address.startsWith("天津")) {
                    return "天津";
                } else if (address.startsWith("重庆")) {
                    return "重庆";
                }
            }
            // 检查省份缩写
            String[] provinces = {"北京", "上海", "天津", "重庆", "河北", "山西", "辽宁", "吉林",
                    "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东", "河南",
                    "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西",
                    "甘肃", "青海", "内蒙古", "广西", "西藏", "宁夏", "新疆", "香港", "澳门", "台湾"};
            for (String province : provinces) {
                if (address.startsWith(province)) {
                    // 如果省份名是两个字，返回省份名；如果是省名带地区（如"甘肃兰州"），返回"甘肃"
                    if (address.length() >= province.length()) {
                        return province;
                    }
                }
            }
            // 如果前面没有匹配到，尝试提取前两个字符作为城市名（适用于直辖市等）
            if (address.length() >= 2) {
                String potentialCity = address.substring(0, 2);
                if ("北京".equals(potentialCity) || "上海".equals(potentialCity) ||
                        "天津".equals(potentialCity) || "重庆".equals(potentialCity)) {
                    return potentialCity;
                }
            }
        }
        return normalizeCityName(address);
    }

    /**
     * 标准化城市名称，去除可能的后缀
     *
     * @param cityName 原始城市名
     * @return 标准化后的城市名
     */
    private String normalizeCityName(String cityName) {
        if (StrUtil.isEmpty(cityName)) {
            return cityName;
        }
        // 去除可能的省份或城市后缀
        if (cityName.endsWith("省")) {
            return cityName.substring(0, cityName.length() - 1);
        }
        if (cityName.endsWith("市")) {
            return cityName.substring(0, cityName.length() - 1);
        }
        if (cityName.endsWith("自治区")) {
            return cityName.substring(0, cityName.length() - 3);
        }
        if (cityName.endsWith("区") && cityName.length() > 2) {
            // 如果是"北京朝阳区"这样的格式，只取前面的城市名
            if (cityName.length() >= 4) {
                return cityName.substring(0, 2);
            }
        }
        return cityName;
    }

    /**
     * 岗位等级占比
     *
     * @return 等级占比
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryScenicLevelRate() {
        // 获取所有岗位信息
        List<PostInfo> postInfoList = postInfoService.list();
        int totalCount = postInfoList.size();

        // 提取所有岗位名称中的关键词
        Map<String, Integer> keywordCountMap = new HashMap<>();

        for (PostInfo postInfo : postInfoList) {
            String postName = postInfo.getPostName();
            if (StrUtil.isNotEmpty(postName)) {
                // 提取关键词并统计
                Set<String> keywords = extractKeywords(postName);
                for (String keyword : keywords) {
                    keywordCountMap.merge(keyword, 1, Integer::sum);
                }
            }
        }
        // 转换为结果格式，包含占比
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : keywordCountMap.entrySet()) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            // 关键词
            map.put("level", entry.getKey());
            // 出现次数
            map.put("count", entry.getValue());
            map.put("percentage", (double) entry.getValue() / totalCount * 100);
            result.add(map);
        }
        // 按出现次数降序排列
        result.sort((a, b) -> (int)b.get("count") - (int)a.get("count"));
        return result;
    }

    /**
     * 从岗位名称中提取关键词（增强版）
     *
     * @param postName 原始岗位名称
     * @return 关键词集合
     */
    private Set<String> extractKeywords(String postName) {
        Set<String> keywords = new HashSet<>();

        if (StrUtil.isEmpty(postName)) {
            return keywords;
        }

        // 移除编号、特殊字符等
        String cleanName = postName.replaceAll("\\([^)]*\\)|\\[[^]]*\\]|【[^】]*】|MJ\\d+", "").trim();

        // 转换为小写便于匹配
        String lowerCaseName = cleanName.toLowerCase();

        // 按优先级定义关键词模式
        // 技术栈关键词
        Map<String, String[]> techStacks = new HashMap<>();
        techStacks.put("Java", new String[]{"java", "spring", "springboot", "mybatis", "hibernate"});
        techStacks.put("Python", new String[]{"python", "django", "flask", "pandas", "numpy"});
        techStacks.put("JavaScript", new String[]{"javascript", "js", "react", "vue", "angular", "node", "express"});
        techStacks.put("C++", new String[]{"c++", "cpp"});
        techStacks.put("C#", new String[]{"c#", "dotnet", "asp.net"});
        techStacks.put("PHP", new String[]{"php", "laravel", "symfony"});
        techStacks.put("Go", new String[]{"go", "golang"});
        techStacks.put("前端", new String[]{"frontend", "web", "html", "css", "ui"});
        techStacks.put("后端", new String[]{"backend", "server"});
        techStacks.put("全栈", new String[]{"fullstack", "full-stack"});

        // 职位级别关键词
        String[] levels = {"初级", "中级", "高级", "资深", "专家", "主管", "经理", "总监"};
        String[] levelEng = {"junior", "senior", "lead", "principal", "expert"};

        // 职位类型关键词
        String[] jobTypes = {"开发", "工程师", "程序员", "设计师", "产品经理", "测试", "运维", "算法", "数据", "安全"};

        // 匹配技术栈关键词
        for (Map.Entry<String, String[]> entry : techStacks.entrySet()) {
            for (String pattern : entry.getValue()) {
                if (lowerCaseName.contains(pattern)) {
                    keywords.add(entry.getKey());
                    break; // 每个技术栈只添加一次
                }
            }
        }

        // 匹配职位级别关键词
        for (String level : levels) {
            if (lowerCaseName.contains(level.toLowerCase())) {
                keywords.add(level);
            }
        }

        for (String level : levelEng) {
            if (lowerCaseName.contains(level)) {
                keywords.add(capitalizeFirst(level));
            }
        }

        // 匹配职位类型关键词
        for (String type : jobTypes) {
            if (lowerCaseName.contains(type.toLowerCase())) {
                keywords.add(type);
            }
        }

        // 如果没有匹配到关键词，添加通用分类
        if (keywords.isEmpty()) {
            if (lowerCaseName.contains("java")) {
                keywords.add("Java");
            } else if (lowerCaseName.contains("开发")) {
                keywords.add("开发");
            } else {
                // 可以考虑按字符长度提取有意义的词语
                keywords.add("其他");
            }
        }

        return keywords;
    }

    /**
     * 首字母大写
     */
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * 岗位价格分布占比
     *
     * @return 价格分布占比
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryPriceStepRate() {
        // 获取所有岗位信息
        List<PostInfo> postInfoList = postInfoService.list();

        // 定义薪资区间（单位：元）
        int[] salaryRanges = {0, 2000, 3000, 5000, 8000, 13000, 18000, 25000, 35000};
        String[] rangeLabels = {"2000以下", "2000-3000", "3000-5000", "5000-8000", "8000-13000", "13000-18000", "18000-25000", "25000-35000", "35000以上"};

        // 初始化各区间计数器
        Map<String, Integer> rangeCountMap = new HashMap<>();
        for (String label : rangeLabels) {
            rangeCountMap.put(label, 0);
        }

        // 统计每个岗位的薪资所属区间
        for (PostInfo postInfo : postInfoList) {
            String salaryStr = postInfo.getSalary(); // 假设薪资字符串存储在salaryStr字段
            if (salaryStr != null) {
                Double avgSalary = parseSalaryToNumber(salaryStr);
                if (avgSalary != null) {
                    String rangeLabel = getSalaryRangeLabel(avgSalary.intValue(), salaryRanges, rangeLabels);
                    if (rangeLabel != null) {
                        rangeCountMap.merge(rangeLabel, 1, Integer::sum);
                    }
                }
            }
        }

        // 转换为结果格式
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        for (String rangeLabel : rangeLabels) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("range", rangeLabel);
            map.put("count", rangeCountMap.get(rangeLabel));
            result.add(map);
        }
        return result;
    }

    /**
     * 解析薪资字符串为数值
     *
     * @param salaryStr 薪资字符串
     * @return 平均薪资数值
     */
    private Double parseSalaryToNumber(String salaryStr) {
        if (salaryStr == null || salaryStr.trim().isEmpty()) {
            return null;
        }

        // 移除空格
        salaryStr = salaryStr.trim();

        // 查找薪资范围的分隔符（"-" 或 "~"）
        String[] parts;
        if (salaryStr.contains("-")) {
            parts = salaryStr.split("-");
        } else if (salaryStr.contains("~")) {
            parts = salaryStr.split("~");
        } else {
            // 如果只有一个数值，直接解析
            return parseSingleSalary(salaryStr);
        }

        if (parts.length >= 2) {
            Double minSalary = parseSingleSalary(parts[0].trim());
            Double maxSalary = parseSingleSalary(parts[1].trim());

            if (minSalary != null && maxSalary != null) {
                // 返回平均值
                return (minSalary + maxSalary) / 2;
            } else if (minSalary != null) {
                return minSalary;
            } else if (maxSalary != null) {
                return maxSalary;
            }
        }

        return null;
    }

    /**
     * 解析单个薪资值
     * 处理如"13K"、"15k"等格式
     *
     * @param salaryPart 单个薪资部分
     * @return 薪资数值
     */
    private Double parseSingleSalary(String salaryPart) {
        if (salaryPart == null || salaryPart.trim().isEmpty()) {
            return null;
        }
        // 移除空格
        salaryPart = salaryPart.toUpperCase().trim();
        // 移除K或万等标识符，并转换为具体数值
        double multiplier = 1;
        if (salaryPart.contains("K")) {
            multiplier = 1000;
            salaryPart = salaryPart.replace("K", "");
        } else if (salaryPart.contains("W")) {
            multiplier = 10000;
            salaryPart = salaryPart.replace("W", "");
        }
        try {
            double value = Double.parseDouble(salaryPart);
            return value * multiplier;
        } catch (NumberFormatException e) {
            // 如果无法解析为数字，则返回null
            return null;
        }
    }

    /**
     * 根据薪资获取所属区间标签
     *
     * @param salary 薪资
     * @param salaryRanges 薪资区间数组
     * @param rangeLabels 区间标签数组
     * @return 区间标签
     */
    private String getSalaryRangeLabel(int salary, int[] salaryRanges, String[] rangeLabels) {
        for (int i = 0; i < salaryRanges.length - 1; i++) {
            if (salary >= salaryRanges[i] && salary < salaryRanges[i + 1]) {
                return rangeLabels[i];
            }
        }
        // 如果薪资超过最大区间，归入最高区间（35000以上）
        if (salary >= salaryRanges[salaryRanges.length - 1]) {
            return rangeLabels[rangeLabels.length - 1];
        }
        // 如果薪资小于最小值，归入第一个区间（2000以下）
        return rangeLabels[0];
    }

    /**
     * 订单词云
     *
     * @return 订单词云
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryOrderWordCloud() {
        List<PostInfo> scenicInfoList = postInfoService.list();
        List<String> wordList = new ArrayList<>();

        // 收集岗位名称和描述中的关键词
        for (PostInfo scenicInfo : scenicInfoList) {
            if (StrUtil.isNotEmpty(scenicInfo.getResponsibility())) {
                wordList.addAll(segmentWords(scenicInfo.getResponsibility()));
            }
        }

        // 统计词频
        Map<String, Integer> wordCountMap = new HashMap<>();
        for (String word : wordList) {
            if (StrUtil.isNotEmpty(word) && !isStopWord(word)) {
                wordCountMap.merge(word, 1, Integer::sum);
            }
        }

        // 按词频排序并取前50个
        List<LinkedHashMap<String, Object>> result = wordCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(50)
                .map(entry -> {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                    map.put("word", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 判断字符是否为中文
     *
     * @param c 待判断字符
     * @return 是否为中文
     */
    private boolean isChineseChar(char c) {
        return c >= 0x4e00 && c <= 0x9fa5;
    }

    /**
     * 简单的中文分词处理
     *
     * @param text 待分词的文本
     * @return 分词结果列表
     */
    private List<String> segmentWords(String text) {
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        for (char c : text.toCharArray()) {
            // 判断是否为中文字符
            if (isChineseChar(c)) {
                currentWord.append(c);
            } else {
                // 遇到非中文字符，将当前词加入列表
                if (currentWord.length() > 0) {
                    String word = currentWord.toString();
                    if (word.length() >= 2) { // 过滤单个字符
                        words.add(word);
                    }
                    currentWord = new StringBuilder();
                }
            }
        }
        // 处理最后一个词
        if (currentWord.length() > 0) {
            String word = currentWord.toString();
            if (word.length() >= 2) {
                words.add(word);
            }
        }
        return words;
    }


    /**
     * 判断是否为停用词
     *
     * @param word 待判断词汇
     * @return 是否为停用词
     */
    private boolean isStopWord(String word) {
        // 定义常见停用词
        Set<String> stopWords = new HashSet<>();
        stopWords.add("的");
        stopWords.add("了");
        stopWords.add("在");
        stopWords.add("是");
        stopWords.add("我");
        stopWords.add("有");
        stopWords.add("和");
        stopWords.add("就");
        stopWords.add("不");
        stopWords.add("人");
        stopWords.add("都");
        stopWords.add("一");
        stopWords.add("一个");
        stopWords.add("上");
        stopWords.add("也");
        stopWords.add("很");
        stopWords.add("到");
        stopWords.add("说");
        stopWords.add("要");
        stopWords.add("去");
        stopWords.add("你");
        stopWords.add("会");
        stopWords.add("着");
        stopWords.add("没有");
        stopWords.add("看");
        stopWords.add("好");
        stopWords.add("自己");
        stopWords.add("这");

        return stopWords.contains(word);
    }
}
