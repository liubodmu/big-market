package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 活动列表应答对象
 * @create 2024-08-06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityListResponseDTO implements Serializable {

    // 活动ID
    private Long activityId;
    // 活动名称
    private String activityName;
    // 活动描述
    private String activityDesc;
    // 开始时间
    private Date beginDateTime;
    // 结束时间
    private Date endDateTime;
    // 抽奖策略ID
    private Long strategyId;
    // 活动状态
    private String state;

}
