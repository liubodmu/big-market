package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.UserAwardRecord;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserAwardRecordDao {
    @DBRouter
    void insert(UserAwardRecord userAwardRecord);

    @DBRouter
    List<UserAwardRecord> queryUserAwardRecordList(UserAwardRecord userAwardRecord);
}
