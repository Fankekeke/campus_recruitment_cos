package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.ExpertInfo;
import cc.mrbird.febs.cos.entity.PostInfo;
import cc.mrbird.febs.cos.dao.PostInfoMapper;
import cc.mrbird.febs.cos.service.IEnterpriseInfoService;
import cc.mrbird.febs.cos.service.IExpertInfoService;
import cc.mrbird.febs.cos.service.IPostInfoService;
import cc.mrbird.febs.cos.service.WordSegmentationService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位管理 实现层
 *
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PostInfoServiceImpl extends ServiceImpl<PostInfoMapper, PostInfo> implements IPostInfoService {

    private final IEnterpriseInfoService enterpriseInfoService;

    private final IExpertInfoService expertInfoService;

    private final WordSegmentationService wordSegmentationService;

    /**
     * 分页获取岗位管理信息
     *
     * @param page     分页对象
     * @param postInfo 岗位管理信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectPostPage(Page<PostInfo> page, PostInfo postInfo) {
        return baseMapper.selectPostPage(page, postInfo);
    }

    /**
     * 推荐岗位
     *
     * @param page     分页对象
     * @param postInfo 岗位信息
     * @return 结果
     */
    @Override
    public IPage<PostInfo> selectPostRecommend(Page<PostInfo> page, PostInfo postInfo) {
        // 获取专家/学生信息作为用户画像
        ExpertInfo expertInfo = expertInfoService.getOne(Wrappers.<ExpertInfo>lambdaQuery()
                .eq(ExpertInfo::getUserId, postInfo.getUserId()));

        // 查询所有有效的岗位信息
        IPage<PostInfo> allPostInfoListFix = baseMapper.selectPostRecommend(page, postInfo); // 按创建时间降序排列
        List<PostInfo> allPostInfoList = allPostInfoListFix.getRecords();

        if (CollectionUtil.isEmpty(allPostInfoList)) {
            // 如果没有岗位数据，返回空分页结果
            IPage<PostInfo> emptyPage = new Page<>();
            emptyPage.setCurrent(page.getCurrent());
            emptyPage.setSize(page.getSize());
            emptyPage.setTotal(0);
            emptyPage.setRecords(Collections.emptyList());
            return emptyPage;
        }

        // 如果没有用户画像，按默认顺序返回所有岗位
        if (expertInfo == null) {
            return buildPagedResult(allPostInfoList, page, allPostInfoListFix.getTotal(), allPostInfoListFix.getCurrent());
        }

        // 对用户画像中的关键词进行分词
        List<String> positionKeywords = StrUtil.isNotEmpty(expertInfo.getPosition())
                ? wordSegmentationService.segment(expertInfo.getPosition()) : Collections.emptyList();
        List<String> levelOneKeywords = StrUtil.isNotEmpty(expertInfo.getLevelOne())
                ? wordSegmentationService.segment(expertInfo.getLevelOne()) : Collections.emptyList();
        List<String> levelTwoKeywords = StrUtil.isNotEmpty(expertInfo.getLevelTwo())
                ? wordSegmentationService.segment(expertInfo.getLevelTwo()) : Collections.emptyList();
        List<String> addressKeywords = StrUtil.isNotEmpty(expertInfo.getAddress())
                ? wordSegmentationService.segment(expertInfo.getAddress()) : Collections.emptyList();

        // 使用评分算法对所有岗位进行排序
        List<PostInfo> rankedPosts = allPostInfoList.stream()
                .map(post -> {
                    // 计算每个岗位的匹配分数
                    int score = calculateScoreForAllFields(post, expertInfo, positionKeywords,
                            levelOneKeywords, levelTwoKeywords, addressKeywords);
                    post.setSortField(String.valueOf(score)); // 临时借用一个字段存储分数
                    return post;
                })
                .sorted((p1, p2) -> Integer.parseInt(p2.getSortField()) - Integer.parseInt(p1.getSortField()))
                .collect(Collectors.toList());

        // 构建分页结果
        return buildPagedResult(rankedPosts, page, allPostInfoListFix.getTotal(), allPostInfoListFix.getCurrent());
    }

    private IPage<PostInfo> buildPagedResult(List<PostInfo> allData, Page<PostInfo> page, long total, long current) {
        IPage<PostInfo> resultPage = new Page<>();
        resultPage.setCurrent(current);
        resultPage.setSize(page.getSize());
        resultPage.setTotal(total);

        // 分页截取数据
//        int startIndex = (int) ((page.getCurrent() - 1) * page.getSize());
//        int endIndex = Math.min(startIndex + (int) page.getSize(), allData.size());
//
//        if (startIndex < allData.size()) {
//            List<PostInfo> pagedData = allData.subList(startIndex, endIndex);
//            resultPage.setRecords(pagedData);
//        } else {
//            resultPage.setRecords(Collections.emptyList());
//        }
        resultPage.setRecords(allData);
        return resultPage;
    }

    /**
     * 为所有字段计算匹配分数
     */
    private int calculateScoreForAllFields(PostInfo post, ExpertInfo expertInfo,
                                           List<String> positionKeywords, List<String> levelOneKeywords,
                                           List<String> levelTwoKeywords, List<String> addressKeywords) {
        int totalScore = 0;
        // 职位名称与分词关键词匹配（最高30分）
        for (String keyword : positionKeywords) {
            if (post.getPostName().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 30 / Math.max(positionKeywords.size(), 1); // 防止除零错误
            } else if (post.getResponsibility().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 15 / Math.max(positionKeywords.size(), 1);
            }
        }
        // 一级领域匹配（最高25分）
        for (String keyword : levelOneKeywords) {
            if (post.getPostName().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 25 / Math.max(levelOneKeywords.size(), 1);
            } else if (post.getResponsibility().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 12 / Math.max(levelOneKeywords.size(), 1);
            }
        }
        // 二级领域匹配（最高20分）
        for (String keyword : levelTwoKeywords) {
            if (post.getPostName().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 20 / Math.max(levelTwoKeywords.size(), 1);
            } else if (post.getResponsibility().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 10 / Math.max(levelTwoKeywords.size(), 1);
            }
        }
        // 经验要求匹配（最高15分）
        if (StrUtil.isNotEmpty(expertInfo.getPosition())) {
            List<String> experienceKeywords = wordSegmentationService.segment(expertInfo.getPosition());
            for (String keyword : experienceKeywords) {
                if (post.getExperience().toLowerCase().contains(keyword.toLowerCase())) {
                    totalScore += 15 / Math.max(experienceKeywords.size(), 1);
                }
            }
        }
        // 工作地点偏好匹配（最高10分）
        for (String keyword : addressKeywords) {
            if (post.getAddress().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 10 / Math.max(addressKeywords.size(), 1);
            }
        }
        return totalScore;
    }

    /**
     * 根据id查询岗位信息
     *
     * @param ids id集合
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryPostByIds(List<Integer> ids) {
        return baseMapper.queryPostByIds(ids);
    }

    /**
     * 推荐岗位
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryPostRecommend(Integer userId) {
        // 获取专家/学生信息作为用户画像
        ExpertInfo expertInfo = expertInfoService.getOne(Wrappers.<ExpertInfo>lambdaQuery()
                .eq(ExpertInfo::getUserId, userId));

        if (expertInfo == null) {
            // 如果没有找到用户画像，默认返回最新发布的10个岗位
            List<PostInfo> postInfoList = this.list(Wrappers.<PostInfo>lambdaQuery()
                    .orderByDesc(PostInfo::getCreateDate)
                    .last("limit 10"));
            if (CollectionUtil.isEmpty(postInfoList)) {
                return Collections.emptyList();
            }
            List<Integer> ids = postInfoList.stream()
                    .map(PostInfo::getId)
                    .collect(Collectors.toList());
            return this.queryPostByIds(ids);
        }

        // 对用户画像中的关键词进行分词
        List<String> positionKeywords = StrUtil.isNotEmpty(expertInfo.getPosition())
                ? wordSegmentationService.segment(expertInfo.getPosition()) : Collections.emptyList();
        List<String> levelOneKeywords = StrUtil.isNotEmpty(expertInfo.getLevelOne())
                ? wordSegmentationService.segment(expertInfo.getLevelOne()) : Collections.emptyList();
        List<String> levelTwoKeywords = StrUtil.isNotEmpty(expertInfo.getLevelTwo())
                ? wordSegmentationService.segment(expertInfo.getLevelTwo()) : Collections.emptyList();

        // 构建分词后的动态查询条件
        List<PostInfo> postInfoList = this.list(Wrappers.<PostInfo>lambdaQuery()
                // 职位名称和职责中匹配分词后的关键词
                .and(!positionKeywords.isEmpty(), wrapper -> {
                    for (String keyword : positionKeywords) {
                        wrapper.or().like(PostInfo::getPostName, keyword)
                                .or().like(PostInfo::getResponsibility, keyword);
                    }
                    return wrapper;
                })
                // 一级领域匹配
                .or(!levelOneKeywords.isEmpty(), wrapper -> {
                    for (String keyword : levelOneKeywords) {
                        wrapper.like(PostInfo::getPostName, keyword)
                                .or().like(PostInfo::getResponsibility, keyword);
                    }
                    return wrapper;
                })
                // 二级领域匹配
                .or(!levelTwoKeywords.isEmpty(), wrapper -> {
                    for (String keyword : levelTwoKeywords) {
                        wrapper.like(PostInfo::getPostName, keyword)
                                .or().like(PostInfo::getResponsibility, keyword);
                    }
                    return wrapper;
                })
                .orderByDesc(PostInfo::getCreateDate) // 按创建时间降序排列
                .last("limit 15")); // 扩大候选集以便后续排序

        if (CollectionUtil.isEmpty(postInfoList)) {
            return Collections.emptyList();
        }

        // 使用更复杂的评分算法对结果进行重新排序
        List<PostInfo> rankedPosts = rankPostsByMatchScoreWithSegments(postInfoList, expertInfo,
                positionKeywords, levelOneKeywords, levelTwoKeywords);

        // 取前10个最佳匹配
        List<Integer> ids = rankedPosts.stream()
                .limit(6)
                .map(PostInfo::getId)
                .collect(Collectors.toList());

        return this.queryPostByIds(ids);
    }

    /**
     * 使用分词对岗位进行评分和排序
     * @param postInfoList
     * @param expertInfo
     * @param positionKeywords
     * @param levelOneKeywords
     * @param levelTwoKeywords
     * @return
     */
    private List<PostInfo> rankPostsByMatchScoreWithSegments(List<PostInfo> postInfoList,
                                                             ExpertInfo expertInfo, List<String> positionKeywords,
                                                             List<String> levelOneKeywords, List<String> levelTwoKeywords) {
        return postInfoList.stream()
                .map(post -> {
                    int score = calculateMatchScoreWithSegments(post, expertInfo,
                            positionKeywords, levelOneKeywords, levelTwoKeywords);
                    post.setSortField(String.valueOf(score)); // 临时借用一个字段存储分数
                    return post;
                })
                .sorted((p1, p2) -> Integer.parseInt(p2.getSortField()) - Integer.parseInt(p1.getSortField()))
                .collect(Collectors.toList());
    }

    /**
     * 使用分词计算匹配分数
     */
    private int calculateMatchScoreWithSegments(PostInfo post, ExpertInfo expertInfo,
                                                List<String> positionKeywords, List<String> levelOneKeywords, List<String> levelTwoKeywords) {
        int totalScore = 0;

        // 职位名称与分词关键词匹配（最高30分）
        for (String keyword : positionKeywords) {
            if (post.getPostName().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 30 / positionKeywords.size(); // 平均分配分数
            } else if (post.getResponsibility().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 15 / positionKeywords.size();
            }
        }

        // 一级领域匹配（最高25分）
        for (String keyword : levelOneKeywords) {
            if (post.getPostName().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 25 / levelOneKeywords.size();
            } else if (post.getResponsibility().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 12 / levelOneKeywords.size();
            }
        }

        // 二级领域匹配（最高20分）
        for (String keyword : levelTwoKeywords) {
            if (post.getPostName().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 20 / levelTwoKeywords.size();
            } else if (post.getResponsibility().toLowerCase().contains(keyword.toLowerCase())) {
                totalScore += 10 / levelTwoKeywords.size();
            }
        }

        // 经验要求匹配（最高15分）
        if (StrUtil.isNotEmpty(expertInfo.getPosition())) {
            List<String> experienceKeywords = wordSegmentationService.segment(expertInfo.getPosition());
            for (String keyword : experienceKeywords) {
                if (post.getExperience().toLowerCase().contains(keyword.toLowerCase())) {
                    totalScore += 15 / experienceKeywords.size();
                }
            }
        }

        // 工作地点偏好匹配（最高10分）
        if (StrUtil.isNotEmpty(expertInfo.getAddress())) {
            List<String> addressKeywords = wordSegmentationService.segment(expertInfo.getAddress());
            for (String keyword : addressKeywords) {
                if (post.getAddress().toLowerCase().contains(keyword.toLowerCase())) {
                    totalScore += 10 / addressKeywords.size();
                }
            }
        }

        return totalScore;
    }

    /**
     * 根据匹配度对岗位进行评分和排序
     *
     * @param postInfoList 候选岗位列表
     * @param expertInfo   用户画像信息
     * @return 排序后的岗位列表
     */
    private List<PostInfo> rankPostsByMatchScore(List<PostInfo> postInfoList, ExpertInfo expertInfo) {
        return postInfoList.stream()
                .map(post -> {
                    int score = calculateMatchScore(post, expertInfo);
                    post.setSortField(String.valueOf(score)); // 临时借用一个字段存储分数
                    return post;
                })
                .sorted((p1, p2) -> Integer.parseInt(p2.getSortField()) - Integer.parseInt(p1.getSortField()))
                .collect(Collectors.toList());
    }

    /**
     * 计算岗位与用户画像的匹配分数
     *
     * @param post       岗位信息
     * @param expertInfo 用户画像信息
     * @return 匹配分数
     */
    private int calculateMatchScore(PostInfo post, ExpertInfo expertInfo) {
        int totalScore = 0;
        // 职位名称匹配权重最高 (最高30分)
        if (StrUtil.isNotEmpty(expertInfo.getPosition())) {
            if (post.getPostName().toLowerCase().contains(expertInfo.getPosition().toLowerCase())) {
                totalScore += 30;
            } else if (post.getResponsibility().toLowerCase().contains(expertInfo.getPosition().toLowerCase())) {
                totalScore += 15; // 要求中包含得一半分
            }
        }
        // 一级领域匹配 (最高25分)
        if (StrUtil.isNotEmpty(expertInfo.getLevelOne())) {
            if (post.getPostName().toLowerCase().contains(expertInfo.getLevelOne().toLowerCase())) {
                totalScore += 25;
            } else if (post.getResponsibility().toLowerCase().contains(expertInfo.getLevelOne().toLowerCase())) {
                totalScore += 12;
            }
        }
        // 二级领域匹配 (最高20分)
        if (StrUtil.isNotEmpty(expertInfo.getLevelTwo())) {
            if (post.getPostName().toLowerCase().contains(expertInfo.getLevelTwo().toLowerCase())) {
                totalScore += 20;
            } else if (post.getResponsibility().toLowerCase().contains(expertInfo.getLevelTwo().toLowerCase())) {
                totalScore += 10;
            }
        }
        // 经验要求匹配 (最高15分)
        if (StrUtil.isNotEmpty(expertInfo.getPosition()) &&
                post.getExperience().toLowerCase().contains(expertInfo.getPosition().toLowerCase())) {
            totalScore += 15;
        }
        // 工作地点偏好匹配 (最高10分)
        if (StrUtil.isNotEmpty(expertInfo.getAddress()) &&
                post.getAddress().toLowerCase().contains(expertInfo.getAddress().toLowerCase())) {
            totalScore += 10;
        }
        return totalScore;
    }
}
