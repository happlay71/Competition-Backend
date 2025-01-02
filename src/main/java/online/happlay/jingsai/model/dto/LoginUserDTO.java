package online.happlay.jingsai.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginUserDTO {
    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "密码")
    private String password;
}
