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
import online.happlay.jingsai.model.entity.Major;
import online.happlay.jingsai.model.query.MajorQuery;
import online.happlay.jingsai.model.query.StudentQuery;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.StudentVO;
import online.happlay.jingsai.service.IMajorService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 专业表 前端控制器
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@RestController
@RequestMapping("/major")
@RequiredArgsConstructor
@Api(tags = "专业")
public class MajorController {

    private final IMajorService majorService;

    @PostMapping("/selectMajor")
    @AuthCheck()
    @ApiOperation(value = "查询专业信息")
    public BaseResponse<PaginationResultVO<Major>> selectMajor(@RequestBody MajorQuery majorQuery,
                                                                   HttpServletRequest request) {
        PaginationResultVO<Major> majorList = majorService.selectMajor(majorQuery, request);
        return ResultUtils.success(majorList);
    }

    @PostMapping("/saveMajor")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "新增或修改专业信息")
    public BaseResponse<String> saveMajor(@RequestBody Major major,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(major == null, ErrorCode.NOT_FOUND_ERROR);
        majorService.saveOrUpdateMajor(major, request);
        return ResultUtils.success("专业信息保存成功");
    }

    @GetMapping("/deleteMajor")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "删除没有学生的专业信息")
    public BaseResponse<String> deleteMajor(@RequestParam("id") Integer id,
                                            HttpServletRequest request) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "专业ID不存在");
        majorService.deleteMajor(id, request);
        return ResultUtils.success("专业信息删除成功");
    }

    @GetMapping("/selectMajorName")
    @AuthCheck()
    @ApiOperation(value = "查询专业名称")
    public BaseResponse<List<String>> selectMajorName(HttpServletRequest request) {
        List<String> competitionList = majorService.selectMajorName(request);
        return ResultUtils.success(competitionList);
    }

}
