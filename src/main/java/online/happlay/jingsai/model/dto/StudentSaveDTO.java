package online.happlay.jingsai.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentSaveDTO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "学生学号")
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
}
