package online.happlay.jingsai.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "学生学号")
    @TableField("student_id")
    private String studentId;

    @ApiModelProperty(value = "学生名字")
    private String name;

    @ApiModelProperty(value = "年级")
    private String grade;

    @ApiModelProperty(value = "专业")
    private String profession;

    @ApiModelProperty(value = "性别")
    private Integer gender;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "认证状态")
    private Integer certification;

    @ApiModelProperty(value = "用户状态")
    private Integer status;
}
