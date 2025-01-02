package online.happlay.jingsai.mapper;

import online.happlay.jingsai.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
