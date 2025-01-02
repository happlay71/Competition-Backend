package online.happlay.jingsai.service;

import online.happlay.jingsai.model.dto.StudentDTO;
import online.happlay.jingsai.model.dto.StudentSaveDTO;
import online.happlay.jingsai.model.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import online.happlay.jingsai.model.query.StudentQuery;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.StudentVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 学生表 服务类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
public interface IStudentService extends IService<Student> {

    void verify(StudentDTO studentDTO, HttpServletRequest request);

    StudentVO info(HttpServletRequest request);

    PaginationResultVO<StudentVO> selectStudent(StudentQuery studentQuery, HttpServletRequest request);

    void saveOrUpdateStudent(StudentSaveDTO studentSaveDTO, HttpServletRequest request);

    void status(Long id, Integer status, HttpServletRequest request);

    void deleteStudent(Integer id, HttpServletRequest request);

    void importStudentByExcel(MultipartFile file, HttpServletRequest request);
}
