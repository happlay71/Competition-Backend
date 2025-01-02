package online.happlay.jingsai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.exception.BusinessException;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.dto.AwardDTO;
import online.happlay.jingsai.model.entity.*;
import online.happlay.jingsai.mapper.AwardMapper;
import online.happlay.jingsai.model.query.AwardQuery;
import online.happlay.jingsai.model.query.BaseQuery;
import online.happlay.jingsai.model.vo.AwardVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.happlay.jingsai.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 学生竞赛获奖表 服务实现类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AwardServiceImpl extends ServiceImpl<AwardMapper, Award> implements IAwardService {

    @Resource
    @Lazy
    private IAwardService awardService;

    @Lazy
    @Resource
    private IUserService userService;

    private final ICompetitionService competitionService;

    private final ICompetitionLevelService competitionLevelService;

    private final IStudentService studentService;

    @Lazy
    @Resource
    private IStudentAwardService studentAwardService;

    private final IMajorService majorService;

    private final JwtUtils jwtUtils;


    /**
     * 新增或修改获奖信息
     * @param awardDTO
     */
    @Override
    @Transactional
    public void saveOrUpdateAward(AwardDTO awardDTO, HttpServletRequest request) {
        // 从请求中获取当前用户的ID和角色
        String currentUserId = jwtUtils.getUserId(request);
        String currentUserRole = jwtUtils.getUserRole(request);

        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 如果不是管理员，校验申请ID是否是当前用户
        if (!isAdmin && !currentUserId.equals(String.valueOf(awardDTO.getApplicant()))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限操作");
        }

        // 根据竞赛名称获取竞赛ID
        LambdaQueryWrapper<Competition> competitionQuery = new LambdaQueryWrapper<>();
        competitionQuery.eq(Competition::getName, awardDTO.getCompetitionName());
        Competition competition = competitionService.getOne(competitionQuery);
        ThrowUtils.throwIf(competition == null, ErrorCode.NOT_FOUND_ERROR, "没有对应竞赛信息");

        // 根据竞赛ID判断竞赛等级是否存在
        LambdaQueryWrapper<CompetitionLevel> competitionLevelQuery = new LambdaQueryWrapper<>();
        competitionLevelQuery
                .eq(CompetitionLevel::getCompetitionId, competition.getId())
                .eq(CompetitionLevel::getLevel, awardDTO.getCompetitionLevel())
                .eq(CompetitionLevel::getRanking, awardDTO.getCompetitionRanking());
        CompetitionLevel competitionLevel = competitionLevelService.getOne(competitionLevelQuery);
        ThrowUtils.throwIf(competitionLevel == null, ErrorCode.NOT_FOUND_ERROR, "没有对应竞赛级别信息");

        // 根据学生学号判断学生信息是否存在
        LambdaQueryWrapper<Student> studentQuery = new LambdaQueryWrapper<>();
        studentQuery.eq(Student::getStudentId, awardDTO.getFirstPlaceStudentId());
        Student student = studentService.getOne(studentQuery);
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR, "没有对应学生信息");

        // 获取现有奖项信息
        Award existingAward = awardService.getById(awardDTO.getId());

        // 创建或更新奖项信息
        Award award = new Award();
        award.setCompetitionInfoId(competition.getId());
        award.setCompetitionLevel(competitionLevel.getId());
        award.setAdvisor(awardDTO.getAdvisor());
        award.setAwardYear(awardDTO.getAwardYear());
        award.setAwardDate(awardDTO.getAwardDate());
        award.setFirstPlaceStudentId(Math.toIntExact(student.getId()));
        award.setApplicant(awardDTO.getApplicant());
        award.setEntryDate(awardDTO.getEntryDate());

        // 如果已经存在该奖项，执行更新操作
        if (existingAward != null) {
            award.setId(existingAward.getId()); // 设置现有奖项ID，进行更新
            awardService.updateById(award);
        } else {
            // 如果是新奖项，则保存
            awardService.save(award);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAward(Integer awardId, HttpServletRequest request) {
        // 从请求中获取当前用户的ID和角色
        String currentUserId = jwtUtils.getUserId(request);
        String currentUserRole = jwtUtils.getUserRole(request);

        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 根据 awardId 获取 Award 记录
        Award award = awardService.getById(awardId);
        if (award == null) {
            log.error("未找到奖项记录，无法删除，奖项ID: {}", awardId);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到奖项记录，无法删除");
        }

        // 如果不是管理员，校验当前用户是否为该记录的申请者
        if (!isAdmin && !currentUserId.equals(String.valueOf(award.getApplicant()))) {
            log.error("用户无权限删除奖项记录，用户ID: {}, 奖项ID: {}", currentUserId, awardId);
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "无权限操作");
        }

        Student student = studentService.getById(award.getFirstPlaceStudentId());
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR, "未找到学生信息");
        Major major = majorService.getById(student.getProfession());
        ThrowUtils.throwIf(major == null, ErrorCode.NOT_FOUND_ERROR, "未找到专业信息");
        major.setAwardCount(major.getAwardCount() - 1);
        majorService.updateById(major);

        // 删除获奖表记录
        boolean removed = awardService.removeById(awardId);

        // 如果奖项删除成功，日志记录
        if (removed) {
            log.info("成功删除获奖记录，奖项ID: {}", awardId);
        } else {
            log.error("未找到奖项记录，无法删除，奖项ID: {}", awardId);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到奖项记录，无法删除");
        }
    }


//    @Override
//    public PaginationResultVO<AwardVO> selectAward(AwardQuery awardQuery, HttpServletRequest request) {
//        // 从请求中获取当前用户的ID和角色
//        String currentUserId = jwtUtils.getUserId(request);
//        String currentUserRole = jwtUtils.getUserRole(request);
//
//        // 判断是否为管理员
//        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);
//
//        // 创建查询条件构建器
//        LambdaQueryWrapper<Award> queryWrapper = new LambdaQueryWrapper<>();
//
//        // 如果不是管理员，只能查询自己的获奖信息
//        if (!isAdmin) {
//            queryWrapper.eq(Award::getApplicant, Long.valueOf(currentUserId));
//        }
//
//        LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
//        studentQueryWrapper.eq(Student::getUserId, currentUserId);
//        Student student = studentService.getOne(studentQueryWrapper);
//        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR, "未找到学生信息");
//
//        LambdaQueryWrapper<Award> awardQueryWrapper = new LambdaQueryWrapper<>();
//        awardQueryWrapper.eq(Award::getFirstPlaceStudentId, student.getId());
//        if (this.list(awardQueryWrapper).isEmpty()) {
//            return new PaginationResultVO<>(0, awardQuery.getPageSize(), awardQuery.getPageNo(), 0, new ArrayList<>());
//        }
//        List<Integer> awardIds = this.list(awardQueryWrapper).stream()
//                .map(Award::getId)
//                .collect(Collectors.toList());
//
//        queryWrapper.eq(Award::getId, awardIds);
//
//        // 根据竞赛名称模糊查询，获取对应的竞赛ID列表
//        if (StrUtil.isNotBlank(awardQuery.getCompetitionName())) {
//            LambdaQueryWrapper<Competition> competitionQuery = new LambdaQueryWrapper<>();
//            competitionQuery.like(Competition::getName, awardQuery.getCompetitionName());
//            List<Competition> competitions = competitionService.list(competitionQuery);
//
//            // 如果没有匹配的竞赛信息，直接返回空结果
//            if (competitions.isEmpty()) {
//                return PaginationResultVO.emptyResult(new BaseQuery());
//            }
//
//            // 提取竞赛ID列表
//            List<Long> competitionIds = competitions.stream()
//                    .map(competition -> competition.getId().longValue())
//                    .collect(Collectors.toList());
//            queryWrapper.in(Award::getCompetitionInfoId, competitionIds); // 添加竞赛ID in 条件
//        }
//
//        // 根据竞赛级别模糊查询，获取对应的竞赛级别ID列表
//        if (StrUtil.isNotBlank(awardQuery.getCompetitionLevel())) {
//            LambdaQueryWrapper<CompetitionLevel> competitionLevelQuery = new LambdaQueryWrapper<>();
//            competitionLevelQuery.like(CompetitionLevel::getLevel, awardQuery.getCompetitionLevel());
//            List<CompetitionLevel> competitionLevels = competitionLevelService.list(competitionLevelQuery);
//
//            // 如果没有匹配的竞赛级别信息，直接返回空结果
//            if (competitionLevels.isEmpty()) {
//                return PaginationResultVO.emptyResult(new BaseQuery());
//            }
//
//            // 提取竞赛级别ID列表
//            List<Long> competitionLevelIds = competitionLevels.stream()
//                    .map(competitionLevel -> competitionLevel.getId().longValue())
//                    .collect(Collectors.toList());
//            queryWrapper.in(Award::getCompetitionLevel, competitionLevelIds); // 添加竞赛级别ID in 条件
//        }
//
//        // 进行分页查询
//        Page<Award> page = new Page<>(awardQuery.getPageNo(), awardQuery.getPageSize());
//        Page<Award> awardPage = awardService.page(page, queryWrapper);
//
//        // 获取分页结果
//        long totalCount = awardPage.getTotal(); // 总记录数
//        List<Award> awardList = awardPage.getRecords(); // 当前页的数据记录列表
//
//        // 将 Award 转换为 AwardVO
//        List<AwardVO> awardVOList = awardList.stream()
//                .map(award -> convertToAwardVO(award))
//                .collect(Collectors.toList());
//
//        // 构建 PaginationResultVO
//        return new PaginationResultVO<>(
//                (int) totalCount,
//                awardQuery.getPageSize(),
//                awardQuery.getPageNo(),
//                (int) Math.ceil((double) totalCount / awardQuery.getPageSize()),
//                awardVOList
//        );
//    }

    @Override
    public PaginationResultVO<AwardVO> selectAward(AwardQuery awardQuery, HttpServletRequest request) {
        // 从请求中获取当前用户的ID和角色
        String currentUserId = jwtUtils.getUserId(request);
        String currentUserRole = jwtUtils.getUserRole(request);

        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 创建查询条件构建器
        LambdaQueryWrapper<Award> queryWrapper = new LambdaQueryWrapper<>();

        // 如果不是管理员，只能查询自己的获奖信息
        if (!isAdmin) {
            // 查询当前学生信息
            LambdaQueryWrapper<Student> studentQueryWrapper = new LambdaQueryWrapper<>();
            studentQueryWrapper.eq(Student::getUserId, currentUserId);
            Student student = studentService.getOne(studentQueryWrapper);

            if (student != null) {
                // 获取学生的获奖信息（先查询一次所有获奖记录）
                LambdaQueryWrapper<Award> awardQueryWrapper = new LambdaQueryWrapper<>();
                awardQueryWrapper.eq(Award::getFirstPlaceStudentId, student.getId());
                List<Award> awards = this.list(awardQueryWrapper);

                if (awards.isEmpty()) {
                    return new PaginationResultVO<>(0, awardQuery.getPageSize(), awardQuery.getPageNo(), 0, new ArrayList<>());
                }

                // 获取所有符合条件的获奖ID
                List<Integer> awardIds = awards.stream()
                        .map(Award::getId)
                        .collect(Collectors.toList());

                // 查找获奖团队里的获奖信息
                LambdaQueryWrapper<StudentAward> studentAwardQueryWrapper = new LambdaQueryWrapper<>();
                studentAwardQueryWrapper.eq(StudentAward::getStudentId, student.getId());
                List<StudentAward> studentAwards = studentAwardService.list(studentAwardQueryWrapper);
                if (!studentAwards.isEmpty()) {
                    List<Integer> studentAwardIds = studentAwards.stream()
                            .map(StudentAward::getAwardId)
                            .collect(Collectors.toList());
                    awardIds.addAll(studentAwardIds);
                    queryWrapper.in(Award::getId, awardIds);
                }
                queryWrapper.in(Award::getId, awardIds);
            } else {
                return new PaginationResultVO<>(0, awardQuery.getPageSize(), awardQuery.getPageNo(), 0, new ArrayList<>());
            }

        }


        // 如果有竞赛名称，根据竞赛名称查询竞赛ID
        if (StrUtil.isNotBlank(awardQuery.getCompetitionName())) {
            LambdaQueryWrapper<Competition> competitionQuery = new LambdaQueryWrapper<>();
            competitionQuery.like(Competition::getName, awardQuery.getCompetitionName());
            List<Competition> competitions = competitionService.list(competitionQuery);

            // 如果没有匹配的竞赛信息，直接返回空结果
            if (competitions.isEmpty()) {
                return PaginationResultVO.emptyResult(new BaseQuery());
            }

            // 提取竞赛ID列表并加入查询条件
            List<Long> competitionIds = competitions.stream()
                    .map(competition -> competition.getId().longValue())
                    .collect(Collectors.toList());
            queryWrapper.in(Award::getCompetitionInfoId, competitionIds);
        }

        // 如果有竞赛等级，根据竞赛等级查询竞赛级别ID
        if (StrUtil.isNotBlank(awardQuery.getCompetitionLevel())) {
            LambdaQueryWrapper<CompetitionLevel> competitionLevelQuery = new LambdaQueryWrapper<>();
            competitionLevelQuery.like(CompetitionLevel::getLevel, awardQuery.getCompetitionLevel());
            List<CompetitionLevel> competitionLevels = competitionLevelService.list(competitionLevelQuery);

            // 如果没有匹配的竞赛级别信息，直接返回空结果
            if (competitionLevels.isEmpty()) {
                return PaginationResultVO.emptyResult(new BaseQuery());
            }

            // 提取竞赛级别ID列表并加入查询条件
            List<Long> competitionLevelIds = competitionLevels.stream()
                    .map(competitionLevel -> competitionLevel.getId().longValue())
                    .collect(Collectors.toList());
            queryWrapper.in(Award::getCompetitionLevel, competitionLevelIds);
        }

        // 进行分页查询
        Page<Award> page = new Page<>(awardQuery.getPageNo(), awardQuery.getPageSize());
        Page<Award> awardPage = awardService.page(page, queryWrapper);

        // 获取分页结果
        long totalCount = awardPage.getTotal(); // 总记录数
        List<Award> awardList = awardPage.getRecords(); // 当前页的数据记录列表

        // 将 Award 转换为 AwardVO
        List<AwardVO> awardVOList = awardList.stream()
                .map(award -> convertToAwardVO(award))
                .collect(Collectors.toList());

        // 构建 PaginationResultVO
        return new PaginationResultVO<>(
                (int) totalCount,
                awardQuery.getPageSize(),
                awardQuery.getPageNo(),
                (int) awardPage.getPages(), // 使用 `awardPage.getPages()` 获取总页数
                awardVOList
        );
    }


    /**
     * 获奖审核操作
     * @param awardId
     * @param request
     */
    @Override
    @Transactional
    public void auditAward(Integer awardId, String action, HttpServletRequest request) {
        // 从请求中获取当前用户的角色
        String currentUserRole = jwtUtils.getUserRole(request);

        // 判断是否为管理员
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        // 如果不是管理员，校验申请ID是否是当前用户
        ThrowUtils.throwIf(!isAdmin ,ErrorCode.FORBIDDEN_ERROR, "无权限操作");

        // 校验奖项 ID 是否存在
        Award award = awardService.getById(awardId);
        ThrowUtils.throwIf(award == null ,ErrorCode.NOT_FOUND_ERROR, "未找到奖项信息");

        // 获取审核结果（从请求中获取或根据需求设置逻辑）
        ThrowUtils.throwIf(StrUtil.isBlank(action), ErrorCode.PARAMS_ERROR, "缺少必要的审核参数");

        Student student = studentService.getById(award.getFirstPlaceStudentId());
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR, "未找到学生信息");
        Major major = majorService.getById(student.getProfession());
        ThrowUtils.throwIf(major == null, ErrorCode.NOT_FOUND_ERROR, "未找到专业信息");

        // 更新审核状态
        switch (action.toLowerCase()) {
            case "approve": // 审核通过
                award.setStatus(1);

                major.setAwardCount(major.getAwardCount() + 1);
                majorService.updateById(major);
                log.info("奖项审核通过，奖项ID: {}", awardId);
                break;
            case "reject": // 审核驳回
                award.setStatus(2);

                if (award.getStatus() == 1) {
                    major.setAwardCount(major.getAwardCount() - 1);
                }

                majorService.updateById(major);
                log.info("奖项审核驳回，奖项ID: {}", awardId);
                break;
            default:
                log.error("无效的审核操作类型: {}", action);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "无效的审核操作类型");
        }

        // 保存状态更新
        boolean updated = awardService.updateById(award);
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "审核状态更新失败");
    }



    /**
     * 将 Award 转换为 AwardVO
     *
     * @param award Award 对象
     * @return AwardVO 对象
     */
    private AwardVO convertToAwardVO(Award award) {
        AwardVO awardVO = new AwardVO();
        BeanUtils.copyProperties(award, awardVO); // 将 Award 的基础属性复制到 AwardVO

        // 根据竞赛ID获取竞赛名称
        if (award.getCompetitionInfoId() != null) {
            Competition competition = competitionService.getById(award.getCompetitionInfoId());
            awardVO.setCompetitionName(competition != null ? competition.getName() : "未知竞赛");
        } else {
            awardVO.setCompetitionName("未知竞赛");
        }

        // 根据竞赛级别ID获取竞赛级别名称
        if (award.getCompetitionLevel() != null) {
            CompetitionLevel competitionLevel = competitionLevelService.getById(award.getCompetitionLevel());
            awardVO.setCompetitionLevel(competitionLevel != null ? competitionLevel.getLevel() : "未知级别");
            awardVO.setCompetitionRanking(competitionLevel != null ? competitionLevel.getRanking() : "未知级别");
        } else {
            awardVO.setCompetitionLevel("未知级别");
            awardVO.setCompetitionRanking("未知级别");
        }

        // 根据学生ID获取学生姓名
        if (award.getFirstPlaceStudentId() != null) {
            Student student = studentService.getById(award.getFirstPlaceStudentId());
            awardVO.setStudentName(student != null ? student.getName() : "未知学生");
        } else {
            awardVO.setStudentName("未知学生");
        }

        return awardVO;
    }


}
