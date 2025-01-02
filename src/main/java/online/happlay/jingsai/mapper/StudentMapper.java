package online.happlay.jingsai.mapper;

import online.happlay.jingsai.model.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 学生表 Mapper 接口
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    /**
     * 批量插入学生信息
     * @param studentList 学生信息列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<Student> studentList);

    /**
     * 根据学号判断是否存在
     * @param studentId 学号
     * @return 是否存在
     */
    boolean existsByStudentId(@Param("studentId") String studentId);
}
