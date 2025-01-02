package online.happlay.jingsai.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import online.happlay.jingsai.annotation.AuthCheck;
import online.happlay.jingsai.common.BaseResponse;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.common.ResultUtils;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.dto.AwardDTO;
import online.happlay.jingsai.model.query.AwardQuery;
import online.happlay.jingsai.model.vo.AwardVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.service.IAwardService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 学生竞赛获奖表 前端控制器
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@RestController
@RequestMapping("/award")
@RequiredArgsConstructor
@Api(tags = "获奖记录")
public class AwardController {

    private final IAwardService awardService;

    @PostMapping("/saveAward")
    @AuthCheck()
    @ApiOperation(value = "新增或修改获奖信息")
    public BaseResponse<String> saveAward(@RequestBody AwardDTO awardDTO,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(awardDTO == null, ErrorCode.NOT_FOUND_ERROR);
        awardService.saveOrUpdateAward(awardDTO, request);
        return ResultUtils.success("奖项信息保存成功");
    }

    @PostMapping("/auditAward")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "管理员审核获奖信息，应该传入awardId和action(approve-审核通过，reject-审核驳回")
    public BaseResponse<String> auditAward(@RequestParam("awardId") Integer awardId,
                                           @RequestParam("action") String action,
                                           HttpServletRequest request) {
        awardService.auditAward(awardId, action, request);
        return ResultUtils.success("奖项信息审核成功");
    }

    @GetMapping("/deleteAward")
    @AuthCheck()
    @ApiOperation(value = "删除自己创建的未审核的获奖信息")
    public BaseResponse<String> deleteAward(@RequestParam("awardId") Integer awardId,
                                            HttpServletRequest request) {
        awardService.deleteAward(awardId, request);
        return ResultUtils.success("奖项信息删除成功");
    }

    @PostMapping("/selectAward")
    @AuthCheck()
    @ApiOperation(value = "查询获奖信息")
    public BaseResponse<PaginationResultVO<AwardVO>> selectAward(@RequestBody AwardQuery awardQuery,
                                                                 HttpServletRequest request) {
        PaginationResultVO<AwardVO> awardVOList = awardService.selectAward(awardQuery, request);
        return ResultUtils.success(awardVOList);
    }
}
