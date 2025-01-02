package online.happlay.jingsai.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserDTO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户昵称")
    private String username;

    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "user-普通用户 admin-管理员")
    private String role;
}
