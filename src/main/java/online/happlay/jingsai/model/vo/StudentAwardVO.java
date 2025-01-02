package online.happlay.jingsai.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentAwardVO {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "外键，关联学生信息表")
    private String studentName;

    @ApiModelProperty(value = "外键，关联学生获奖表")
    private Integer awardId;

    @ApiModelProperty(value = "团队中的排名")
    private Integer rankingInTeam;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
