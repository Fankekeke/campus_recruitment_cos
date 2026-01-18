package cc.mrbird.febs.cos.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * AI面试记录
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AiInterview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 学生ID
     */
    private Integer userId;

    /**
     * 岗位ID
     */
    private Integer postId;

    /**
     * 回答问题
     */
    private String answers;
    private String status;

    /**
     * 回答时间
     */
    private String completionTime;

    /**
     * 回答分析
     */
    private String aiRemark;

    @TableField(exist = false)
    private String enterpriseName;
    @TableField(exist = false)
    private String abbreviation;
    @TableField(exist = false)
    private String expertName;
    @TableField(exist = false)
    private String expertId;
    @TableField(exist = false)
    private String enterpriseId;
}
