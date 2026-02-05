package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 三方协议表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TripartiteAgreements implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long studentId;

    /**
     * 协议编号
     */
    private String agreementNo;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 签约时间
     */
    private String signDate;

    /**
     * 协议扫描件URL
     */
    private String fileUrl;

    private String uploadTime;


}
