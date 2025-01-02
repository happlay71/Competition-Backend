package online.happlay.jingsai.service;

import online.happlay.jingsai.model.dto.StudentAwardDTO;
import online.happlay.jingsai.model.entity.StudentAward;
import com.baomidou.mybatisplus.extension.service.IService;
import online.happlay.jingsai.model.query.StudentTeamQuery;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.StudentAwardVO;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 学生获奖团队表 服务类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
public interface IStudentAwardService extends IService<StudentAward> {

    PaginationResultVO<StudentAwardVO> selectTeam(StudentTeamQuery studentTeamQuery, HttpServletRequest request);

    void saveOrUpdateTeam(StudentAwardDTO studentAwardDTO, HttpServletRequest request);

    void deleteTeam(Integer id, HttpServletRequest request);
}
