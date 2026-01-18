package cc.mrbird.febs.cos.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 面试评价
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EvaluateInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 面试ID
     */
    private Integer interviewId;
    private Integer postId;
    private Integer userId;

    /**
     * 面试评价
     */
    private String content;
    private String satisfy;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 创建时间
     */
    private String createDate;

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
