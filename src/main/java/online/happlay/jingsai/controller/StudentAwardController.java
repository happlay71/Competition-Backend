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
import online.happlay.jingsai.model.dto.StudentAwardDTO;
import online.happlay.jingsai.model.entity.Competition;
import online.happlay.jingsai.model.entity.StudentAward;
import online.happlay.jingsai.model.query.CompetitionQuery;
import online.happlay.jingsai.model.query.StudentTeamQuery;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.StudentAwardVO;
import online.happlay.jingsai.service.IStudentAwardService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 学生获奖团队表 前端控制器
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@Api(tags = "学生获奖团队")
public class StudentAwardController {

    private final IStudentAwardService studentAwardService;

    @PostMapping("/selectTeam")
    @AuthCheck()
    @ApiOperation(value = "查询学生获奖团队信息")
    public BaseResponse<PaginationResultVO<StudentAwardVO>> selectTeam(@RequestBody StudentTeamQuery studentTeamQuery,
                                                                       HttpServletRequest request) {
        PaginationResultVO<StudentAwardVO> studentAwardVOList = studentAwardService.selectTeam(studentTeamQuery, request);
        return ResultUtils.success(studentAwardVOList);
    }

    @PostMapping("/saveTeam")
    @AuthCheck()
    @ApiOperation(value = "新增或修改学生获奖团队信息")
    public BaseResponse<String> saveTeam(@RequestBody StudentAwardDTO studentAwardDTO,
                                                HttpServletRequest request) {
        ThrowUtils.throwIf(studentAwardDTO == null, ErrorCode.NOT_FOUND_ERROR);
        studentAwardService.saveOrUpdateTeam(studentAwardDTO, request);
        return ResultUtils.success("学生获奖团队信息保存成功");
    }

    @GetMapping("/deleteTeam")
    @AuthCheck()
    @ApiOperation(value = "删除学生获奖团队信息")
    public BaseResponse<String> deleteTeam(@RequestParam("id") Integer id,
                                                  HttpServletRequest request) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "专业ID不存在");
        studentAwardService.deleteTeam(id, request);
        return ResultUtils.success("学生获奖团队信息删除成功");
    }

}
