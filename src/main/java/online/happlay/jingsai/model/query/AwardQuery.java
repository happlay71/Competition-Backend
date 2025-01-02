package online.happlay.jingsai.model.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AwardQuery {

    @ApiModelProperty(value = "外键，关联竞赛信息表")
    private String competitionName;

    @ApiModelProperty(value = "获奖等级")
    private String competitionLevel;

    private int pageNo = 1;

    private int pageSize = 5;
}
