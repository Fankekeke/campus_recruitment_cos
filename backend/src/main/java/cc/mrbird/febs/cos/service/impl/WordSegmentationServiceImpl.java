package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.service.WordSegmentationService;
import cn.hutool.core.util.StrUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordSegmentationServiceImpl implements WordSegmentationService {

    @Override
    public List<String> segment(String text) {
        // 这里可以集成HanLP、IK Analyzer或其他分词工具
        // 示例使用简单的空格分词，实际应使用专业分词工具
        if (StrUtil.isBlank(text)) {
            return Collections.emptyList();
        }

        // 使用HanLP示例（需添加相应依赖）
        List<Term> termList = HanLP.segment(text);
        return termList.stream()
                .filter(term -> !isStopWord(term.word)) // 过滤停用词
                .map(term -> term.word)
                .distinct() // 去重
                .collect(Collectors.toList());
    }

    /**
     * 判断是否为停用词
     */
    private boolean isStopWord(String word) {
        Set<String> stopWords = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList("的", "了", "在", "是", "我", "有", "和", "就",
                        "不", "人", "都", "一", "一个", "上", "也", "很")));
        return stopWords.contains(word);
    }
}
