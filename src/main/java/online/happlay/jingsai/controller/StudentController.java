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
import online.happlay.jingsai.model.dto.StudentDTO;
import online.happlay.jingsai.model.dto.StudentSaveDTO;
import online.happlay.jingsai.model.query.AwardQuery;
import online.happlay.jingsai.model.query.StudentQuery;
import online.happlay.jingsai.model.vo.AwardVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.model.vo.StudentVO;
import online.happlay.jingsai.service.IStudentService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 学生表 前端控制器
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@Api(tags = "学生")
public class StudentController {

    private final IStudentService studentService;

    @PostMapping("/verify")
    @AuthCheck()
    @ApiOperation(value = "验证学生身份信息")
    public BaseResponse<String> verify(@RequestBody StudentDTO studentDTO,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(studentDTO == null, ErrorCode.NOT_FOUND_ERROR);
        studentService.verify(studentDTO, request);
        return ResultUtils.success("学生身份验证成功");
    }

    @PostMapping("/status")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "改变学生对应的用户的状态")
    public BaseResponse<String> status(@RequestParam("id") Integer id,
                                          @RequestParam("status") Integer status,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(id == null, ErrorCode.NOT_FOUND_ERROR);
        studentService.status(Long.valueOf(id), status, request);
        return ResultUtils.success("学生身份验证成功");
    }

    @GetMapping("/info")
    @AuthCheck()
    @ApiOperation(value = "获取自身学生身份信息")
    public BaseResponse<StudentVO> info(HttpServletRequest request) {
        StudentVO studentVO  = studentService.info(request);
        return ResultUtils.success(studentVO);
    }

    @PostMapping("/selectStudent")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "查询学生信息")
    public BaseResponse<PaginationResultVO<StudentVO>> selectStudent(@RequestBody StudentQuery studentQuery,
                                                                 HttpServletRequest request) {
        PaginationResultVO<StudentVO> studentVOList = studentService.selectStudent(studentQuery, request);
        return ResultUtils.success(studentVOList);
    }

    @PostMapping("/saveStudent")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "新增或修改学生信息")
    public BaseResponse<String> saveStudent(@RequestBody StudentSaveDTO studentSaveDTO,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(studentSaveDTO == null, ErrorCode.NOT_FOUND_ERROR);
        studentService.saveOrUpdateStudent(studentSaveDTO, request);
        return ResultUtils.success("学生信息保存成功");
    }

    @GetMapping("/deleteStudent")
    @AuthCheck(mustRole = "admin")
    @ApiOperation(value = "删除没有获奖信息的学生")
    public BaseResponse<String> deleteStudent(@RequestParam("id") Integer id,
                                           HttpServletRequest request) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "学生ID不存在");
        studentService.deleteStudent(id, request);
        return ResultUtils.success("学生信息删除成功");
    }
}
