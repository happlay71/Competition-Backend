package online.happlay.jingsai.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LevelVO {
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "竞赛名称")
    private String competition;

    @ApiModelProperty(value = "竞赛等级")
    private String level;

    @ApiModelProperty(value = "获奖名次")
    private String ranking;

    @ApiModelProperty(value = "认定学分")
    private String credit;

    @ApiModelProperty(value = "折算成绩")
    private Integer achievement;
}
