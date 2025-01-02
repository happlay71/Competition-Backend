package online.happlay.jingsai.model.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PasswordDTO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "密码")
    private String password;
}
