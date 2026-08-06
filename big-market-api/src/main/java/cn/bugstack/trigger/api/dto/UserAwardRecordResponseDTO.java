package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户中奖记录应答对象
 * @create 2024-08-06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordResponseDTO implements Serializable {

    // 用户ID
    private String userId;
    // 活动ID
    private Long activityId;
    // 抽奖策略ID
    private Long strategyId;
    // 抽奖订单ID
    private String orderId;
    // 奖品ID
    private Integer awardId;
    // 奖品标题（名称）
    private String awardTitle;
    // 中奖时间
    private Date awardTime;
    // 奖品状态；create-创建、completed-发奖完成
    private String awardState;

}
