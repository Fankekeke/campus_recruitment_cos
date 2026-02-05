package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 就业去向表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class EmploymentDestinations implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 1:签协议就业, 2:升学, 3:出国, 4:创业, 5:其他
     */
    private Integer type;

    /**
     * 单位名称
     */
    private String companyName;

    /**
     * 统一社会信用代码
     */
    private String organizationCode;

    /**
     * 就业城市
     */
    private String city;

    private String createTime;


}
