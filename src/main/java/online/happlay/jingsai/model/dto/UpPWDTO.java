package online.happlay.jingsai.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpPWDTO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "旧密码")
    private String oldPassword;

    @ApiModelProperty(value = "新密码")
    private String newPassword;
}
