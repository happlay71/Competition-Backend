package online.happlay.jingsai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import online.happlay.jingsai.common.ErrorCode;
import online.happlay.jingsai.exception.ThrowUtils;
import online.happlay.jingsai.model.entity.Award;
import online.happlay.jingsai.model.entity.Competition;
import online.happlay.jingsai.model.entity.CompetitionLevel;
import online.happlay.jingsai.mapper.CompetitionLevelMapper;
import online.happlay.jingsai.model.query.LevelQuery;
import online.happlay.jingsai.model.vo.LevelVO;
import online.happlay.jingsai.model.vo.PaginationResultVO;
import online.happlay.jingsai.service.IAwardService;
import online.happlay.jingsai.service.ICompetitionLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import online.happlay.jingsai.service.ICompetitionService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 竞赛级别表 服务实现类
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Service
@RequiredArgsConstructor
public class CompetitionLevelServiceImpl extends ServiceImpl<CompetitionLevelMapper, CompetitionLevel> implements ICompetitionLevelService {

    @Lazy
    @Resource
    private IAwardService awardService;

    private final ICompetitionService competitionService;

    @Override
    public PaginationResultVO<LevelVO> selectLevel(LevelQuery levelQuery, HttpServletRequest request) {
        // 1. 构建查询条件
        LambdaQueryWrapper<CompetitionLevel> queryWrapper = new LambdaQueryWrapper<>();

        // 如果有竞赛名称，根据竞赛名称查询竞赛id
        if (levelQuery.getCompetition() != null && !levelQuery.getCompetition().isEmpty()) {
            // 查询竞赛 ID
            LambdaQueryWrapper<Competition> competitionQueryWrapper = new LambdaQueryWrapper<>();
            competitionQueryWrapper.like(Competition::getName, levelQuery.getCompetition());
            Competition competition = competitionService.getOne(competitionQueryWrapper);

            // 如果找到了竞赛，则将竞赛id加入查询条件
            if (competition != null) {
                queryWrapper.eq(CompetitionLevel::getCompetitionId, competition.getId());
            } else {
                // 如果没有找到竞赛，可以抛出异常或返回空结果
                return new PaginationResultVO<>(0, levelQuery.getPageSize(), levelQuery.getPageNo(), 0, new ArrayList<>());
            }
        }

        // 如果有竞赛等级，加入查询条件
        if (levelQuery.getLevel() != null && !levelQuery.getLevel().isEmpty()) {
            queryWrapper.like(CompetitionLevel::getLevel, levelQuery.getLevel());
        }

        // 2. 分页查询
        Page<CompetitionLevel> page = new Page<>(levelQuery.getPageNo(), levelQuery.getPageSize());
        Page<CompetitionLevel> levelPage = this.page(page, queryWrapper);

        // 获取总记录数
        long totalCount = levelPage.getTotal();

        // 3. 转换查询结果为 VO 对象
        List<LevelVO> levelVOList = levelPage.getRecords().stream()
                .map(this::convertToLevelVO)
                .collect(Collectors.toList());

        // 4. 返回分页结果
        return new PaginationResultVO<>(
                (int) totalCount,  // 总记录数
                levelQuery.getPageSize(),  // 每页大小
                levelQuery.getPageNo(),  // 当前页码
                (int) Math.ceil((double) totalCount / levelQuery.getPageSize()),  // 总页数
                levelVOList  // 竞赛等级信息列表
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateLevel(LevelVO levelVO, HttpServletRequest request) {
        LambdaQueryWrapper<Competition> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Competition::getName, levelVO.getCompetition());
        Competition competition = competitionService.getOne(queryWrapper);
        ThrowUtils.throwIf(competition == null, ErrorCode.SYSTEM_ERROR, "竞赛信息不存在");

        // 结合竞赛id判断竞赛级别表中是否有相同的级别
        LambdaQueryWrapper<CompetitionLevel> competitionLevelQueryWrapper = new LambdaQueryWrapper<>();
        competitionLevelQueryWrapper
                .eq(CompetitionLevel::getCompetitionId, competition.getId())
                .eq(CompetitionLevel::getLevel, levelVO.getLevel())
                .eq(CompetitionLevel::getRanking, levelVO.getRanking());
        ThrowUtils.throwIf(this.getOne(competitionLevelQueryWrapper) != null, ErrorCode.PARAMS_ERROR, "该竞赛级别已存在");


        // 判断 levelVO 是否包含 id（如果 id 存在，则进行更新；否则进行新增）
        if (levelVO.getId() != null) {
            // 查找数据库中是否有对应的 Level 实体
            CompetitionLevel existingLevel = this.getById(levelVO.getId());
            ThrowUtils.throwIf(existingLevel == null ,ErrorCode.PARAMS_ERROR, "该竞赛等级不存在，无法更新");

            existingLevel.setLevel(levelVO.getLevel());
            existingLevel.setRanking(levelVO.getRanking());
            existingLevel.setCredit(levelVO.getCredit());
            existingLevel.setAchievement(levelVO.getAchievement());
            existingLevel.setCompetitionId(competition.getId());


            // 更新数据库
            this.updateById(existingLevel);
        } else {
            // 创建 Level 实体对象
            CompetitionLevel level = BeanUtil.copyProperties(levelVO, CompetitionLevel.class);

            level.setCompetitionId(competition.getId());

            // 保存到数据库
            this.save(level);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLevel(Integer id, HttpServletRequest request) {

        // 1. 检查获奖信息表是否存在该竞赛等级的关联
        LambdaQueryWrapper<Award> awardQueryWrapper = new LambdaQueryWrapper<>();
        awardQueryWrapper.eq(Award::getCompetitionLevel, id);
        Award award = awardService.getOne(awardQueryWrapper);  // 假设 awardService 是用于操作 Award 表的服务类

        // 如果有关联的获奖信息，抛出异常
        ThrowUtils.throwIf(award != null, ErrorCode.PARAMS_ERROR, "该竞赛等级已被关联到获奖信息，无法删除");

        // 2. 删除竞赛等级记录
        boolean isRemoved = this.removeById(id);  // 假设 `removeById` 是用于删除竞赛等级的通用方法

        // 如果删除失败，抛出异常
        ThrowUtils.throwIf(!isRemoved, ErrorCode.PARAMS_ERROR, "删除竞赛等级失败");
    }

    @Override
    public List<String> selectLevelName(HttpServletRequest request) {
        // 查询所有的竞赛级别名称
        LambdaQueryWrapper<CompetitionLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(CompetitionLevel::getLevel);  // 只查询竞赛级别名称字段

        // 执行查询
        List<CompetitionLevel> competitionLevelList = this.list(queryWrapper);

        // 提取竞赛级别名称列表并去重
        return competitionLevelList.stream()
                .map(CompetitionLevel::getLevel)    // 获取每个竞赛级别名称
                .collect(Collectors.toSet())        // 使用Set来去重
                .stream()                           // 将Set转回Stream
                .collect(Collectors.toList());      // 最终转换成List
    }

    @Override
    public List<String> selectRankingName(HttpServletRequest request) {
        // 查询所有的获奖名次名称
        LambdaQueryWrapper<CompetitionLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(CompetitionLevel::getRanking);  // 只查询获奖名次名称字段

        // 执行查询，获取所有的获奖名次
        List<CompetitionLevel> rankingList = this.list(queryWrapper);

        // 提取获奖名次名称并去重
        return rankingList.stream()
                .map(CompetitionLevel::getRanking)   // 获取每个获奖名次名称
                .distinct()                               // 去重
                .collect(Collectors.toList());            // 转换为 List 并返回
    }



    public LevelVO convertToLevelVO(CompetitionLevel level) {
        if (level == null) {
            return null;
        }

        LevelVO levelVO = BeanUtil.copyProperties(level, LevelVO.class);

        Competition competition = competitionService.getById(level.getCompetitionId());
        ThrowUtils.throwIf(competition == null, ErrorCode.SYSTEM_ERROR, "竞赛信息不存在");
        levelVO.setCompetition(competition.getName());

        return levelVO;
    }


}
