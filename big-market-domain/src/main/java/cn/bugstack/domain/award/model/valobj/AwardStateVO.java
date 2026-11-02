package cn.bugstack.domain.award.model.valobj;

import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 奖品状态枚举值对象 【值对象，用于描述对象属性的值，一个对象中，一个属性，有多个状态值。】
 * @create 2024-04-06 09:13
 */
@Getter
@AllArgsConstructor

public enum AwardStateVO {
    create("create", "创建"),
    complete("complete", "发奖完成"),
    fail("fail", "发奖失败"),
    ;

    private final String code;
    private final String desc;

    public static AwardStateVO of(String code) {
        for (AwardStateVO awardState : values()) {
            if (awardState.getCode().equals(code)) {
                return awardState;
            }
        }
        // 兼容存量数据：旧版本使用 completed 表示 complete
        if ("completed".equals(code)) {
            return complete;
        }
        throw new AppException(ResponseCode.UN_ERROR.getCode(), "未知奖品状态值:" + code);
    }

}
