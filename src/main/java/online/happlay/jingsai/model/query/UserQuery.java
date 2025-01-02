package online.happlay.jingsai.model.query;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserQuery {
    private int pageNo = 1;

    private int pageSize = 5;

    @ApiModelProperty(value = "用户昵称")
    private String username;

    @ApiModelProperty(value = "账号")
    @TableField("userAccount") // 指定数据库字段名
    private String userAccount;

    @ApiModelProperty(value = "用户状态")
    private Integer status;

}
