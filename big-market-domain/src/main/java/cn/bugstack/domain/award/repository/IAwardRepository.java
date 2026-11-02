package cn.bugstack.domain.award.repository;

import cn.bugstack.domain.award.model.aggregate.UserAwardRecordAggregate;

import java.util.List;

import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;

public interface IAwardRepository {
    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    List<UserAwardRecordEntity> queryUserAwardRecordList(String userId, Long activityId);
}
