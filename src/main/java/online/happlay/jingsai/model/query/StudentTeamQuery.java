package online.happlay.jingsai.model.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentTeamQuery {
    private int pageNo = 1;

    private int pageSize = 5;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "外键，关联学生信息表")
    private String studentName;

    @ApiModelProperty(value = "外键，关联学生获奖表")
    private Integer awardId;
}
