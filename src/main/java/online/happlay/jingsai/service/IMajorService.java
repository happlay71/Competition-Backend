package online.happlay.jingsai.service;

import online.happlay.jingsai.model.entity.Major;
import com.baomidou.mybatisplus.extension.service.IService;
import online.happlay.jingsai.model.query.MajorQuery;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.StudentVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 专业表 服务类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
public interface IMajorService extends IService<Major> {

    PaginationResultVO<Major> selectMajor(MajorQuery majorQuery, HttpServletRequest request);

    void saveOrUpdateMajor(Major major, HttpServletRequest request);

    void deleteMajor(Integer id, HttpServletRequest request);

    List<String> selectMajorName(HttpServletRequest request);
}
