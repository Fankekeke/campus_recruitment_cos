package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.AiInterview;
import cc.mrbird.febs.cos.dao.AiInterviewMapper;
import cc.mrbird.febs.cos.entity.ExpertInfo;
import cc.mrbird.febs.cos.entity.PostInfo;
import cc.mrbird.febs.cos.service.IAiInterviewService;
import cc.mrbird.febs.cos.service.IEnterpriseInfoService;
import cc.mrbird.febs.cos.service.IExpertInfoService;
import cc.mrbird.febs.cos.service.IPostInfoService;
import cn.hutool.core.date.DateUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * AI面试记录 实现层
 *
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AiInterviewServiceImpl extends ServiceImpl<AiInterviewMapper, AiInterview> implements IAiInterviewService {

    private final IEnterpriseInfoService enterpriseInfoService;

    private final IExpertInfoService expertInfoService;

    private final IPostInfoService postInfoService;

    @Resource
    private Generation generation;

    /**
     * 分页获取AI面试记录信息
     *
     * @param page        分页对象
     * @param aiInterview AI面试记录信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<AiInterview> page, AiInterview aiInterview) {
        return baseMapper.queryPage(page, aiInterview);
    }

    /**
     * 新增AI面试记录信息
     *
     * @param aiInterview AI面试记录信息
     * @return 结果
     */
    @Override
    public Boolean aiInterviewAdd(AiInterview aiInterview) {
        try {
            // 获取学生信息
            ExpertInfo studentInfo = expertInfoService.getOne(Wrappers.<ExpertInfo>lambdaQuery().eq(ExpertInfo::getUserId, aiInterview.getUserId()));
            // 获取岗位信息要求
            PostInfo postInfo = postInfoService.getById(aiInterview.getPostId());

            if (studentInfo == null || postInfo == null) {
                throw new RuntimeException("学生信息或岗位信息不存在");
            }
            aiInterview.setUserId(studentInfo.getId());

            // 构造AI分析提示词
            String analysisPrompt = buildAnalysisPrompt(studentInfo, postInfo, aiInterview.getAnswers());
            aiInterview.setCompletionTime(DateUtil.formatDateTime(new Date()));
            aiInterview.setStatus("分析中");
            this.save(aiInterview);
            // 设置AI分析结果
            queryContent(analysisPrompt, aiInterview.getId());
            return true;
        } catch (Exception e) {
            log.error("保存AI面试记录失败", e);
            throw new RuntimeException("保存AI面试记录失败: " + e.getMessage());
        }
    }

    /**
     * 构建AI分析提示词
     */
    private String buildAnalysisPrompt(ExpertInfo studentInfo, PostInfo postInfo, String answer) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请分析以下学生的面试表现和岗位匹配度：\n\n");

        // 学生信息
        prompt.append("学生信息：\n");
        prompt.append("- 姓名: ").append(studentInfo.getName()).append("\n");
        prompt.append("- 学历: ").append(studentInfo.getEducatio()).append("\n");
        prompt.append("- 专业: ").append(studentInfo.getLevelOne() + " " + studentInfo.getLevelTwo()).append("\n");
        prompt.append("- 期望职位: ").append(studentInfo.getPosition()).append("\n");
        prompt.append("- 一级领域: ").append(studentInfo.getLevelOne()).append("\n");
        prompt.append("- 二级领域: ").append(studentInfo.getLevelTwo()).append("\n");
        prompt.append("- 工作地点: ").append(studentInfo.getAddress()).append("\n");


        // 岗位要求
        prompt.append("\n岗位要求：\n");
        prompt.append("- 岗位名称: ").append(postInfo.getPostName()).append("\n");
        prompt.append("- 职责描述: ").append(postInfo.getResponsibility()).append("\n");
        // 学历要求（1.小学 2.初中 3.高中 4.大专 5.本科 6.研究生）
        prompt.append("- 学历: ").append(convertEducationCodeToText(studentInfo.getEducatio())).append("\n");
        prompt.append("- 专业要求: ").append(postInfo.getWorkRequire()).append("\n");
        prompt.append("- 经验要求: ").append(postInfo.getExperience()).append("\n");
        prompt.append("- 工作地点: ").append(postInfo.getAddress()).append("\n");
        prompt.append("- 薪资范围: ").append(postInfo.getSalary()).append("\n");

        // 面试回答
        prompt.append("\n学生针对问题的回答：\n").append(answer).append("\n");

        // 分析要求
        prompt.append("\n请从以下几个方面进行详细分析：\n");
        prompt.append("1. **学历匹配度分析** - 对比学生学历与岗位学历要求，评估匹配程度并给出具体分数（0-100分）\n");
        prompt.append("2. **专业匹配度分析** - 对比学生专业背景与岗位专业要求，评估匹配程度并给出具体分数（0-100分）\n");
        prompt.append("3. **技能匹配度分析** - 对比学生技能与岗位技能要求，评估匹配程度并给出具体分数（0-100分）\n");
        prompt.append("4. **经验匹配度分析** - 对比学生工作经验与岗位经验要求，评估匹配程度并给出具体分数（0-100分）\n");
        prompt.append("5. **回答质量分析** - 评估学生回答内容的相关性、逻辑性、深度和表达能力\n");
        prompt.append("6. **综合匹配度评估** - 基于以上各项指标，给出总体匹配度评价和最终分数（0-100分）\n");
        prompt.append("7. **优劣势分析** - 明确指出学生的优势和不足之处\n");
        prompt.append("8. **改进建议** - 针对学生不足之处提出具体的改进建议\n");
        prompt.append("9. **面试表现总结** - 给出整体面试表现评价\n");
        prompt.append("\n请使用结构化的方式输出分析结果，包含具体的分数、数据和事实支撑，并确保分析客观公正。并且最后要给出综合匹配度评分 ");

        return prompt.toString();
    }

    @Async("aiAnalysisExecutor")
    public void queryContent(String key, Integer id) {
        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content(key)
                .build();
        GenerationParam param = GenerationParam.builder()
                //指定用于对话的通义千问模型名
                .model("qwen-plus")
                .messages(Arrays.asList(userMessage))
                //
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                //生成过程中核采样方法概率阈值，例如，取值为0.8时，仅保留概率加起来大于等于0.8的最可能token的最小集合作为候选集。
                // 取值范围为（0,1.0)，取值越大，生成的随机性越高；取值越低，生成的确定性越高。
                .topP(0.8)
                //阿里云控制台DASHSCOPE获取的api-key
                .apiKey("sk-fkebb4821588054a66aa1951d7f239f77c")
                //启用互联网搜索，模型会将搜索结果作为文本生成过程中的参考信息，但模型会基于其内部逻辑“自行判断”是否使用互联网搜索结果。
                .enableSearch(true)
                .build();
        GenerationResult generationResult = null;
        try {
            generationResult = generation.call(param);
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }
        List<String> allContents = generationResult.getOutput().getChoices().stream()
                .map(choice -> choice.getMessage().getContent())
                .collect(Collectors.toList());
        String content = String.join("\n---\n", allContents);

        // 定义正则表达式模式
        Pattern pattern = Pattern.compile("综合匹配度评分.*?(\\d+)");
        Matcher matcher = pattern.matcher(content);
        Integer score = 0;
        // 查找匹配项
        if (matcher.find()) {
            try {
                // 提取评分数字
                score = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                score = 0;
            }
        }

        this.update(Wrappers.<AiInterview>lambdaUpdate().set(AiInterview::getAiRemark, content).set(AiInterview::getStatus, "已完成").set(AiInterview::getScore, score).eq(AiInterview::getId, id));
    }

    /**
     * 将学历编码转换为文字描述
     * @param educationCode 学历编码 (1.小学 2.初中 3.高中 4.大专 5.本科 6.研究生)
     * @return 学历文字描述
     */
    private String convertEducationCodeToText(String educationCode) {
        if (educationCode == null) {
            return "不限";
        }
        switch (educationCode) {
            case "1":
                return "小学";
            case "2":
                return "初中";
            case "3":
                return "高中";
            case "4":
                return "大专";
            case "5":
                return "本科";
            case "6":
                return "研究生";
            default:
                return educationCode; // 如果不是预期值则返回原值
        }
    }
}
