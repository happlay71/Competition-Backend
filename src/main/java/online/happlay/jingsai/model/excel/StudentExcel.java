package online.happlay.jingsai.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class StudentExcel {
    
    @ExcelProperty("学号")
    private String studentId;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("年级")
    private String grade;

    @ExcelProperty("专业")
    private String profession;

    @ExcelProperty("性别")
    private Integer gender;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("电话")
    private String phone;
}