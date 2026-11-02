package cn.bugstack.trigger.http;

import cn.bugstack.domain.activity.model.entity.ActivityAccountDayEntity;
import cn.bugstack.domain.activity.model.entity.ActivityAccountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityAccountMonthEntity;
import cn.bugstack.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.IRaffleActivityPartakeService;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.repository.IAwardRepository;
import cn.bugstack.trigger.api.IActivityService;
import cn.bugstack.trigger.api.dto.ActivityAccountRequestDTO;
import cn.bugstack.trigger.api.dto.ActivityAccountResponseDTO;
import cn.bugstack.trigger.api.dto.ActivityPartakeRequestDTO;
import cn.bugstack.trigger.api.dto.ActivityPartakeResponseDTO;
import cn.bugstack.trigger.api.dto.UserAwardRecordRequestDTO;
import cn.bugstack.trigger.api.dto.UserAwardRecordResponseDTO;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import cn.bugstack.types.model.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/activity/")
public class IActivityController implements IActivityService {

    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;
    @Resource
    private IActivityRepository activityRepository;
    @Resource
    private IAwardRepository awardRepository;

    private final SimpleDateFormat dateFormatMonth = new SimpleDateFormat("yyyy-MM");
    private final SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    @RequestMapping(value = "partake", method = RequestMethod.POST)
    public Response<ActivityPartakeResponseDTO> partakeActivity(@RequestBody ActivityPartakeRequestDTO requestDTO) {
        try {
            log.info("活动参与开始 userId:{} activityId:{}", requestDTO.getUserId(), requestDTO.getActivityId());
            UserRaffleOrderEntity userRaffleOrderEntity = raffleActivityPartakeService.createOrder(PartakeRaffleActivityEntity.builder()
                    .userId(requestDTO.getUserId())
                    .activityId(requestDTO.getActivityId())
                    .build());
            ActivityPartakeResponseDTO responseDTO = ActivityPartakeResponseDTO.builder()
                    .userId(userRaffleOrderEntity.getUserId())
                    .activityId(userRaffleOrderEntity.getActivityId())
                    .activityName(userRaffleOrderEntity.getActivityName())
                    .strategyId(userRaffleOrderEntity.getStrategyId())
                    .orderId(userRaffleOrderEntity.getOrderId())
                    .orderTime(userRaffleOrderEntity.getOrderTime())
                    .build();
            Response<ActivityPartakeResponseDTO> response = Response.<ActivityPartakeResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
            log.info("活动参与完成 userId:{} response: {}", requestDTO.getUserId(), JSON.toJSONString(response));
            return response;
        } catch (AppException e) {
            log.error("活动参与失败 userId:{} activityId:{} info: {}", requestDTO.getUserId(), requestDTO.getActivityId(), e.getInfo());
            return Response.<ActivityPartakeResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("活动参与失败 userId:{} activityId:{}", requestDTO.getUserId(), requestDTO.getActivityId(), e);
            return Response.<ActivityPartakeResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "query_account_quota", method = RequestMethod.POST)
    public Response<ActivityAccountResponseDTO> queryActivityAccount(@RequestBody ActivityAccountRequestDTO requestDTO) {
        try {
            log.info("查询账户额度开始 userId:{} activityId:{}", requestDTO.getUserId(), requestDTO.getActivityId());
            ActivityAccountEntity activityAccountEntity = activityRepository.queryActivityAccountByUserId(requestDTO.getUserId(), requestDTO.getActivityId());
            if (null == activityAccountEntity) {
                return Response.<ActivityAccountResponseDTO>builder()
                        .code(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode())
                        .info(ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo())
                        .build();
            }
            String month = dateFormatMonth.format(new Date());
            String day = dateFormatDay.format(new Date());
            ActivityAccountMonthEntity activityAccountMonthEntity = activityRepository.queryActivityAccountMonthByUserId(requestDTO.getUserId(), requestDTO.getActivityId(), month);
            ActivityAccountDayEntity activityAccountDayEntity = activityRepository.queryActivityAccountDayByUserId(requestDTO.getUserId(), requestDTO.getActivityId(), day);
            ActivityAccountResponseDTO responseDTO = ActivityAccountResponseDTO.builder()
                    .userId(requestDTO.getUserId())
                    .activityId(requestDTO.getActivityId())
                    .totalCount(activityAccountEntity.getTotalCount())
                    .totalCountSurplus(activityAccountEntity.getTotalCountSurplus())
                    .dayCount(activityAccountEntity.getDayCount())
                    .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                    .monthCount(activityAccountEntity.getMonthCount())
                    .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                    .build();
            // 月/日账户是按需创建的，未创建则用总额度填充，避免前端把 null 当作 0 之外的情况处理
            if (null != activityAccountMonthEntity) {
                responseDTO.setMonthCount(activityAccountMonthEntity.getMonthCount());
                responseDTO.setMonthCountSurplus(activityAccountMonthEntity.getMonthCountSurplus());
            }
            if (null != activityAccountDayEntity) {
                responseDTO.setDayCount(activityAccountDayEntity.getDayCount());
                responseDTO.setDayCountSurplus(activityAccountDayEntity.getDayCountSurplus());
            }
            Response<ActivityAccountResponseDTO> response = Response.<ActivityAccountResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
            log.info("查询账户额度完成 userId:{} response: {}", requestDTO.getUserId(), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("查询账户额度失败 userId:{} activityId:{}", requestDTO.getUserId(), requestDTO.getActivityId(), e);
            return Response.<ActivityAccountResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "query_user_award_record", method = RequestMethod.POST)
    public Response<List<UserAwardRecordResponseDTO>> queryUserAwardRecordList(@RequestBody UserAwardRecordRequestDTO requestDTO) {
        try {
            log.info("查询中奖记录开始 userId:{} activityId:{}", requestDTO.getUserId(), requestDTO.getActivityId());
            List<UserAwardRecordEntity> userAwardRecordEntityList = awardRepository.queryUserAwardRecordList(requestDTO.getUserId(), requestDTO.getActivityId());
            List<UserAwardRecordResponseDTO> userAwardRecordResponseDTOList = new ArrayList<>(userAwardRecordEntityList.size());
            for (UserAwardRecordEntity userAwardRecordEntity : userAwardRecordEntityList) {
                userAwardRecordResponseDTOList.add(UserAwardRecordResponseDTO.builder()
                        .awardId(userAwardRecordEntity.getAwardId())
                        .awardTitle(userAwardRecordEntity.getAwardTitle())
                        .awardTime(userAwardRecordEntity.getAwardTime())
                        .awardState(userAwardRecordEntity.getAwardState().getCode())
                        .orderId(userAwardRecordEntity.getOrderId())
                        .build());
            }
            Response<List<UserAwardRecordResponseDTO>> response = Response.<List<UserAwardRecordResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(userAwardRecordResponseDTOList)
                    .build();
            log.info("查询中奖记录完成 userId:{} response: {}", requestDTO.getUserId(), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("查询中奖记录失败 userId:{} activityId:{}", requestDTO.getUserId(), requestDTO.getActivityId(), e);
            return Response.<List<UserAwardRecordResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

}
