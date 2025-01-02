package online.happlay.jingsai.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import online.happlay.jingsai.annotation.AuthCheck;
import online.happlay.jingsai.common.BaseResponse;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.common.ResultUtils;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.dto.CompetitionDTO;
import online.happlay.jingsai.model.entity.Competition;
import online.happlay.jingsai.model.query.CompetitionQuery;
import online.happlay.jingsai.model.query.LevelQuery;
import online.happlay.jingsai.model.vo.LevelVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.service.ICompetitionService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 竞赛信息表 前端控制器
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@RestController
@RequestMapping("/competition")
@RequiredArgsConstructor
@Api(tags = "竞赛")
public class CompetitionController {

    private final ICompetitionService competitionService;

    @PostMapping("/selectCompetition")
    @AuthCheck()
    @ApiOperation(value = "查询竞赛信息")
    public BaseResponse<PaginationResultVO<Competition>> selectCompetition(@RequestBody CompetitionQuery competitionQuery,
                                                                           HttpServletRequest request) {
        PaginationResultVO<Competition> competitionList = competitionService.selectCompetition(competitionQuery, request);
        return ResultUtils.success(competitionList);
    }

    @PostMapping("/saveCompetition")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "新增或修改竞赛信息")
    public BaseResponse<String> saveCompetition(@RequestBody CompetitionDTO competitionDTO,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(competitionDTO == null, ErrorCode.NOT_FOUND_ERROR);
        competitionService.saveOrUpdateCompetition(competitionDTO, request);
        return ResultUtils.success("竞赛信息保存成功");
    }

    @GetMapping("/deleteCompetition")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "删除没有获奖信息的竞赛信息")
    public BaseResponse<String> deleteCompetition(@RequestParam("id") Integer id,
                                            HttpServletRequest request) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "专业ID不存在");
        competitionService.deleteCompetition(id, request);
        return ResultUtils.success("竞赛信息删除成功");
    }

    @GetMapping("/selectCompetitionName")
    @AuthCheck()
    @ApiOperation(value = "查询竞赛名称")
    public BaseResponse<List<String>> selectCompetitionName(HttpServletRequest request) {
        List<String> competitionList = competitionService.selectCompetitionName(request);
        return ResultUtils.success(competitionList);
    }
}
