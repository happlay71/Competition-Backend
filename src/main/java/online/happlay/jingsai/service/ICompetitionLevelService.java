package online.happlay.jingsai.service;

import online.happlay.jingsai.model.entity.CompetitionLevel;
import com.baomidou.mybatisplus.extension.service.IService;
import online.happlay.jingsai.model.query.LevelQuery;
import online.happlay.jingsai.model.vo.LevelVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 竞赛级别表 服务类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
public interface ICompetitionLevelService extends IService<CompetitionLevel> {

    PaginationResultVO<LevelVO> selectLevel(LevelQuery levelQuery, HttpServletRequest request);

    void saveOrUpdateLevel(LevelVO levelVO, HttpServletRequest request);

    void deleteLevel(Integer id, HttpServletRequest request);

    List<String> selectLevelName(HttpServletRequest request);

    List<String> selectRankingName(HttpServletRequest request);
}
