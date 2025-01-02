package online.happlay.jingsai.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AwardVO {
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "申请人ID")
    private Long applicant;

    @ApiModelProperty(value = "外键，关联竞赛信息表")
    private String competitionName;

    @ApiModelProperty(value = "竞赛等级")
    private String competitionLevel;

    @ApiModelProperty(value = "获奖等级")
    private String competitionRanking;

    @ApiModelProperty(value = "指导老师")
    private String advisor;

    @ApiModelProperty(value = "获奖年份")
    private Integer awardYear;

    @ApiModelProperty(value = "获奖日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime awardDate;

    @ApiModelProperty(value = "外键，关联学生信息表，第一成员")
    private String studentName;

    @ApiModelProperty(value = "填表日期，默认为当前日期")
    private String entryDate;

    @ApiModelProperty(value = "0-审核中，1-审核通过，2-审核驳回")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
