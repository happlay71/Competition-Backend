package online.happlay.jingsai.model.query;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompetitionQuery {
    private int pageNo = 1;

    private int pageSize = 5;

    @ApiModelProperty(value = "竞赛编号")
    private Integer id;

    @ApiModelProperty(value = "竞赛名称")
    private String name;
}
