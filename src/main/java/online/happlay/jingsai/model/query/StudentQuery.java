package online.happlay.jingsai.model.query;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentQuery {

    @ApiModelProperty(value = "学生学号")
    private String studentId;

    @ApiModelProperty(value = "学生名字")
    private String studentName;

    @ApiModelProperty(value = "专业名称")
    private String profession;

    @ApiModelProperty(value = "认证状态")
    private String certification;

    private int pageNo = 1;

    private int pageSize = 5;
}
