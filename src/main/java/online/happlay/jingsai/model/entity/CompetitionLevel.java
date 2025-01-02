package online.happlay.jingsai.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 竞赛级别表
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("competition_level")
@ApiModel(value="CompetitionLevel对象", description="竞赛级别表")
public class CompetitionLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "赛事编号")
    private Integer competitionId;

    @ApiModelProperty(value = "竞赛等级")
    private String level;

    @ApiModelProperty(value = "获奖名次")
    private String ranking;

    @ApiModelProperty(value = "认定学分")
    private String credit;

    @ApiModelProperty(value = "折算成绩")
    private Integer achievement;


}
