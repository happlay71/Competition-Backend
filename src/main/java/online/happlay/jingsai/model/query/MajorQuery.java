package online.happlay.jingsai.model.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MajorQuery {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "专业名称")
    private String name;

    private int pageNo = 1;

    private int pageSize = 5;
}
