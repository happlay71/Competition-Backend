package online.happlay.jingsai.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentAwardDTO {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "外键，关联学生信息表")
    private String studentId;

    @ApiModelProperty(value = "外键，关联学生获奖表")
    private Integer awardId;

    @ApiModelProperty(value = "团队中的排名")
    private Integer rankingInTeam;

}
