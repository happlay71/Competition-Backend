package online.happlay.jingsai.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户昵称")
    private String username;

    @ApiModelProperty(value = "账号")
    private String userAccount;

    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "用户状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField("createTime")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("createTime")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "是否删除")
    @TableField("isDelete")
    private Integer isDelete;

    @ApiModelProperty(value = "user-普通用户 admin-管理员")
    private String role;
}
