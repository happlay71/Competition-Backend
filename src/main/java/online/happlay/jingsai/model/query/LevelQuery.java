package online.happlay.jingsai.model.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LevelQuery {

    private int pageNo = 1;

    private int pageSize = 5;

    @ApiModelProperty(value = "竞赛名称")
    private String competition;

    @ApiModelProperty(value = "竞赛等级")
    private String level;

}
