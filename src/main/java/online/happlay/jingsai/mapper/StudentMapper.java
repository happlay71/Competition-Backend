package online.happlay.jingsai.mapper;

import online.happlay.jingsai.model.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

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

}
