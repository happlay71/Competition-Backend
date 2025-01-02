package online.happlay.jingsai.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 竞赛信息表
 * </p>
 *
 * @author happlay
 * @since 2024-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("competition")
@ApiModel(value="Competition对象", description="竞赛信息表")
public class Competition implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "竞赛编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "竞赛名称")
    private String name;

    @ApiModelProperty(value = "竞赛描述")
    private String description;

    @ApiModelProperty(value = "官网链接")
    private String url;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;


}
