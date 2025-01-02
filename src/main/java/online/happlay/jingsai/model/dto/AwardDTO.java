package online.happlay.jingsai.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AwardDTO {

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

    @ApiModelProperty(value = "外键，关联学生信息表，第一成员的id")
    private Integer firstPlaceStudentId;

    @ApiModelProperty(value = "填表日期，默认为当前日期")
    private String entryDate;



}
