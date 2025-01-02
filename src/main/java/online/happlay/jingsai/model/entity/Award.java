package online.happlay.jingsai.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学生竞赛获奖表
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("award")
@ApiModel(value="Award对象", description="学生竞赛获奖表")
public class Award implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "外键，关联竞赛信息表")
    @TableField("competition_info_id")
    private Integer competitionInfoId;

    @ApiModelProperty(value = "获奖等级")
    @TableField("competition_level")
    private Integer competitionLevel;

    @ApiModelProperty(value = "指导老师")
    private String advisor;

    @ApiModelProperty(value = "获奖年份")
    @TableField("award_year")
    private Integer awardYear;

    @ApiModelProperty(value = "获奖日期")
    @TableField("award_date")
    private LocalDateTime awardDate;

    @ApiModelProperty(value = "外键，关联学生信息表，第一成员")
    @TableField("first_place_student_id")
    private Integer firstPlaceStudentId;

    @ApiModelProperty(value = "申请人ID")
    private Long applicant;

    @ApiModelProperty(value = "填表日期，默认为当前日期")
    @TableField("entry_date")
    private String entryDate;

    @ApiModelProperty(value = "0-审核中，1-审核通过，2-审核驳回")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;


}
