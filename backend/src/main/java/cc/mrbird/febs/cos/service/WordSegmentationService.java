package cc.mrbird.febs.cos.service;

import java.util.List;

public interface WordSegmentationService {

    /**
     * 对文本进行分词
     * @param text 待分词文本
     * @return 分词结果列表
     */
    List<String> segment(String text);
}
