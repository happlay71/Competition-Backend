package online.happlay.jingsai.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserStatusDTO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户状态")
    private Integer status;
}
