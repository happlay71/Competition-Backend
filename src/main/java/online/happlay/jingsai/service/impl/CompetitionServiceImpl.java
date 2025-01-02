package online.happlay.jingsai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.dto.CompetitionDTO;
import online.happlay.jingsai.model.entity.Award;
import online.happlay.jingsai.model.entity.Competition;
import online.happlay.jingsai.mapper.CompetitionMapper;
import online.happlay.jingsai.model.entity.CompetitionLevel;
import online.happlay.jingsai.model.query.CompetitionQuery;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.service.IAwardService;
import online.happlay.jingsai.service.ICompetitionLevelService;
import online.happlay.jingsai.service.ICompetitionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 竞赛信息表 服务实现类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Service
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements ICompetitionService {

    @Lazy
    @Resource
    private IAwardService awardService;

    @Lazy
    @Resource
    private ICompetitionLevelService competitionLevelService;

    @Override
    public PaginationResultVO<Competition> selectCompetition(CompetitionQuery competitionQuery, HttpServletRequest request) {
        // 1. 构建查询条件
        LambdaQueryWrapper<Competition> queryWrapper = new LambdaQueryWrapper<>();

        // 如果有竞赛名称，则进行模糊查询
        if (competitionQuery.getName() != null && !competitionQuery.getName().isEmpty()) {
            queryWrapper.like(Competition::getName, competitionQuery.getName());
        }

        // 2. 分页查询
        Page<Competition> page = new Page<>(competitionQuery.getPageNo(), competitionQuery.getPageSize());
        Page<Competition> competitionPage = this.page(page, queryWrapper);

        // 3. 获取查询结果并转换成 VO
        List<Competition> competitionList = competitionPage.getRecords();  // 获取当前页的竞赛数据
        long totalCount = competitionPage.getTotal();  // 获取总记录数

        // 4. 返回分页结果
        return new PaginationResultVO<>(
                (int) totalCount,  // 总记录数
                competitionQuery.getPageSize(),  // 每页大小
                competitionQuery.getPageNo(),  // 当前页码
                (int) Math.ceil((double) totalCount / competitionQuery.getPageSize()),  // 总页数
                competitionList  // 当前页的竞赛列表
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateCompetition(CompetitionDTO competitionDTO, HttpServletRequest request) {

        LambdaQueryWrapper<Competition> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Competition::getName, competitionDTO.getName());
        Competition competition = this.getOne(queryWrapper);
        ThrowUtils.throwIf(competition != null, ErrorCode.PARAMS_ERROR, "该竞赛已存在");

        // 判断是否传入了id，判断是更新还是新增
        if (competitionDTO.getId() != null) {
            // 更新操作
            Competition existingCompetition = this.getById(competitionDTO.getId());

            // 如果没有找到对应的竞赛记录，则抛出异常
            ThrowUtils.throwIf(existingCompetition == null, ErrorCode.NOT_FOUND_ERROR, "竞赛记录不存在");

            // 将DTO转换为实体对象，更新必要的字段
            existingCompetition.setName(competitionDTO.getName());
            existingCompetition.setDescription(competitionDTO.getDescription());
            existingCompetition.setUrl(competitionDTO.getUrl());

            // 执行更新操作
            boolean updated = this.updateById(existingCompetition);
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新竞赛信息失败");

        } else {
            // 新增操作
            Competition newCompetition = new Competition();

            // 将DTO数据转换为实体对象
            newCompetition.setName(competitionDTO.getName());
            newCompetition.setDescription(competitionDTO.getDescription());
            newCompetition.setUrl(competitionDTO.getUrl());

            // 执行新增操作
            boolean saved = this.save(newCompetition);
            ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR, "保存竞赛信息失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCompetition(Integer id, HttpServletRequest request) {
        // 判断获奖表中是否有要删除的竞赛id
        LambdaQueryWrapper<Award> awardQueryWrapper = new LambdaQueryWrapper<>();
        awardQueryWrapper.eq(Award::getCompetitionInfoId, id);  // 假设 Award 表中有一个字段 competitionId
        Award award = awardService.getOne(awardQueryWrapper);

        ThrowUtils.throwIf(award != null, ErrorCode.PARAMS_ERROR, "该竞赛已经有获奖记录，无法删除");

        // 判断竞赛级别表中是否有要删除的竞赛id
        LambdaQueryWrapper<CompetitionLevel> levelQueryWrapper = new LambdaQueryWrapper<>();
        levelQueryWrapper.eq(CompetitionLevel::getCompetitionId, id);  // 假设 CompetitionLevel 表中有一个字段 competitionId
        CompetitionLevel competitionLevel = competitionLevelService.getOne(levelQueryWrapper);

        ThrowUtils.throwIf(competitionLevel != null, ErrorCode.PARAMS_ERROR, "该竞赛已被使用在竞赛级别表中，无法删除");


        // 如果没有相关联的记录，执行删除操作
        boolean deleted = this.removeById(id);
        ThrowUtils.throwIf(!deleted, ErrorCode.OPERATION_ERROR, "删除竞赛信息失败");
    }

    @Override
    public List<String> selectCompetitionName(HttpServletRequest request) {
        // 在这里可以加入对请求的权限校验等逻辑
        // 如果需要的话，可以根据请求的当前用户权限来过滤竞赛信息

        // 查询所有的竞赛名称
        LambdaQueryWrapper<Competition> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Competition::getName);  // 只查询竞赛名称字段

        // 执行查询
        List<Competition> competitionList = this.list(queryWrapper);

        // 提取竞赛名称列表
        return competitionList.stream()
                .map(Competition::getName)
                .collect(Collectors.toList());
    }


}
