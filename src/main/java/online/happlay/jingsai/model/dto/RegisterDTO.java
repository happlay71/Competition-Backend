package online.happlay.jingsai.model.dto;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegisterDTO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户昵称")
    private String username;

    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "密码")
    private String password;
}
