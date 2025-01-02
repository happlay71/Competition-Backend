package online.happlay.jingsai.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CompetitionDTO {
    @ApiModelProperty(value = "竞赛编号")
    private Integer id;

    @ApiModelProperty(value = "竞赛名称")
    private String name;

    @ApiModelProperty(value = "竞赛描述")
    private String description;

    @ApiModelProperty(value = "官网链接")
    private String url;
}
