package online.happlay.jingsai.service;

import online.happlay.jingsai.model.dto.CompetitionDTO;
import online.happlay.jingsai.model.entity.Competition;
import com.baomidou.mybatisplus.extension.service.IService;
import online.happlay.jingsai.model.query.CompetitionQuery;
import online.happlay.jingsai.model.vo.LevelVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 竞赛信息表 服务类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
public interface ICompetitionService extends IService<Competition> {

    PaginationResultVO<Competition> selectCompetition(CompetitionQuery competitionQuery, HttpServletRequest request);

    void saveOrUpdateCompetition(CompetitionDTO competitionDTO, HttpServletRequest request);

    void deleteCompetition(Integer id, HttpServletRequest request);

    List<String> selectCompetitionName(HttpServletRequest request);
}
