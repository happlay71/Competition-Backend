package online.happlay.jingsai.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import online.happlay.jingsai.annotation.AuthCheck;
import online.happlay.jingsai.common.BaseResponse;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.common.ResultUtils;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.dto.StudentSaveDTO;
import online.happlay.jingsai.model.entity.Major;
import online.happlay.jingsai.model.query.LevelQuery;
import online.happlay.jingsai.model.query.MajorQuery;
import online.happlay.jingsai.model.vo.LevelVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.service.ICompetitionLevelService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 竞赛级别表 前端控制器
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@RestController
@RequestMapping("/competition-level")
@RequiredArgsConstructor
@Api(tags = "竞赛获奖级别")
public class CompetitionLevelController {

    private final ICompetitionLevelService competitionLevelService;

    @PostMapping("/selectLevel")
    @AuthCheck()
    @ApiOperation(value = "查询竞赛级别信息")
    public BaseResponse<PaginationResultVO<LevelVO>> selectLevel(@RequestBody LevelQuery levelQuery,
                                                                 HttpServletRequest request) {
        PaginationResultVO<LevelVO> levelVOList = competitionLevelService.selectLevel(levelQuery, request);
        return ResultUtils.success(levelVOList);
    }

    @PostMapping("/saveLevel")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "新增或修改竞赛级别信息")
    public BaseResponse<String> saveLevel(@RequestBody LevelVO levelVO,
                                            HttpServletRequest request) {
        ThrowUtils.throwIf(levelVO == null, ErrorCode.NOT_FOUND_ERROR);
        competitionLevelService.saveOrUpdateLevel(levelVO, request);
        return ResultUtils.success("竞赛级别信息保存成功");
    }

    @GetMapping("/deleteLevel")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "删除没有获奖信息的竞赛级别信息")
    public BaseResponse<String> deleteLevel(@RequestParam("id") Integer id,
                                            HttpServletRequest request) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "专业ID不存在");
        competitionLevelService.deleteLevel(id, request);
        return ResultUtils.success("竞赛级别信息删除成功");
    }

    @GetMapping("/selectLevelName")
    @AuthCheck()
    @ApiOperation(value = "查询竞赛级别名称")
    public BaseResponse<List<String>> selectCompetitionName(HttpServletRequest request) {
        List<String> competitionList = competitionLevelService.selectLevelName(request);
        return ResultUtils.success(competitionList);
    }

    @GetMapping("/selectRankingName")
    @AuthCheck()
    @ApiOperation(value = "查询获奖名次名称")
    public BaseResponse<List<String>> selectRankingName(HttpServletRequest request) {
        List<String> competitionList = competitionLevelService.selectRankingName(request);
        return ResultUtils.success(competitionList);
    }
}
