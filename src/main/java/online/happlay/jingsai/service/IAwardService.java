package online.happlay.jingsai.service;

import online.happlay.jingsai.model.dto.AwardDTO;
import online.happlay.jingsai.model.entity.Award;
import com.baomidou.mybatisplus.extension.service.IService;
import online.happlay.jingsai.model.query.AwardQuery;
import online.happlay.jingsai.model.vo.AwardVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 学生竞赛获奖表 服务类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
public interface IAwardService extends IService<Award> {

    void saveOrUpdateAward(AwardDTO awardDTO, HttpServletRequest request);

    void deleteAward(Integer awardId, HttpServletRequest request);

    PaginationResultVO<AwardVO> selectAward(AwardQuery awardQuery, HttpServletRequest request);

    void auditAward(Integer awardId, String action, HttpServletRequest request);
}
