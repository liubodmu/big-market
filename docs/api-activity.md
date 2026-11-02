# Activity REST API

Base URL: `http://localhost:8091/api/v1/activity/`

## POST /partake

Creates a raffle order for a user in an activity. Quota/account checks are
performed by the domain service; an existing unused order is returned as is.

Request:

```json
{
  "userId": "xiaofuge",
  "activityId": 100301
}
```

Response:

```json
{
  "code": "0000",
  "info": "调用成功",
  "data": {
    "userId": "xiaofuge",
    "activityId": 100301,
    "activityName": "测试活动",
    "strategyId": 100006,
    "orderId": "364946382386",
    "orderTime": "2026-11-02T21:16:21.69+08:00"
  }
}
```

Error codes: `0001` generic failure, `ERR_BIZ_003` activity not open,
`ERR_BIZ_004` outside activity date range, `ERR_BIZ_006/007/008` insufficient
total/month/day quota.

Known limitation: current code returns `0001` for an invalid/missing
`activityId` instead of a dedicated "activity not found" error code.

## POST /query_account_quota

Returns the user's total/day/month raffle quota for an activity.

Request:

```json
{
  "userId": "xiaofuge",
  "activityId": 100301
}
```

Response:

```json
{
  "code": "0000",
  "info": "调用成功",
  "data": {
    "userId": "xiaofuge",
    "activityId": 100301,
    "totalCount": 1389,
    "totalCountSurplus": 1319,
    "dayCount": 1389,
    "dayCountSurplus": 1319,
    "monthCount": 1389,
    "monthCountSurplus": 1319
  }
}
```

## POST /query_user_award_record

Returns the user's award record list for an activity.

Request:

```json
{
  "userId": "xiaofuge",
  "activityId": 100301
}
```

Response:

```json
{
  "code": "0000",
  "info": "调用成功",
  "data": [
    {
      "awardId": 107,
      "awardTitle": "小霸王游戏机",
      "awardTime": "2026-08-06T17:24:54+08:00",
      "awardState": "create",
      "orderId": "469938986288"
    }
  ]
}
```
