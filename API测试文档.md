# 智慧公寓管理系统 - API测试文档

## 测试环境配置

- **后端地址**: http://localhost:8080
- **接口文档**: http://localhost:8080/doc.html
- **默认管理员**: admin / admin123

## 一、认证接口测试

### 1.1 用户登录
```
POST /api/auth/login
Content-Type: application/json

请求体:
{
  "username": "admin",
  "password": "admin123"
}

预期响应:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "userType": 1
    }
  },
  "timestamp": 1700000000000
}
```

### 1.2 用户注册
```
POST /api/auth/register
Content-Type: application/json

请求体:
{
  "username": "student001",
  "password": "123456",
  "realName": "张三",
  "phone": "13800138001",
  "email": "student001@example.com",
  "gender": 1,
  "studentNumber": "2024001",
  "department": "计算机学院",
  "major": "软件工程",
  "className": "软工2401"
}

预期响应:
{
  "code": 200,
  "message": "注册成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 1.3 用户登出
```
POST /api/auth/logout?userId=1

预期响应:
{
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": 1700000000000
}
```

## 二、楼宇管理接口测试

### 2.1 新增楼宇
```
POST /api/building
Content-Type: application/json

请求体:
{
  "buildingName": "1号楼",
  "buildingCode": "B001",
  "buildingType": 1,
  "floors": 6,
  "totalRooms": 120,
  "address": "校园北区",
  "managerName": "李宿管",
  "managerPhone": "13900139001",
  "description": "男生宿舍楼",
  "status": 1
}

预期响应:
{
  "code": 200,
  "message": "新增成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 2.2 查询楼宇列表
```
GET /api/building/page?current=1&size=10&buildingType=1

预期响应:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 10,
    "records": [
      {
        "id": 1,
        "buildingName": "1号楼",
        "buildingCode": "B001",
        "buildingType": 1,
        "floors": 6,
        "totalRooms": 120,
        "status": 1,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "current": 1,
    "size": 10
  },
  "timestamp": 1700000000000
}
```

### 2.3 更新楼宇
```
PUT /api/building
Content-Type: application/json

请求体:
{
  "id": 1,
  "buildingName": "1号楼（翻新）",
  "managerPhone": "13900139002",
  "status": 1
}

预期响应:
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 2.4 删除楼宇
```
DELETE /api/building/1

预期响应:
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1700000000000
}
```

## 三、房间管理接口测试

### 3.1 新增房间
```
POST /api/room
Content-Type: application/json

请求体:
{
  "buildingId": 1,
  "roomNumber": "101",
  "floor": 1,
  "roomType": 1,
  "totalBeds": 4,
  "area": 20.5,
  "price": 1200.00,
  "facilities": "{\"空调\":1,\"热水器\":1,\"独立卫生间\":1}",
  "roomStatus": 1,
  "description": "标准四人间"
}

预期响应:
{
  "code": 200,
  "message": "新增成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 3.2 查询房间列表
```
GET /api/room/page?current=1&size=10&buildingId=1&roomStatus=1

预期响应:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 20,
    "records": [
      {
        "id": 1,
        "buildingId": 1,
        "roomNumber": "101",
        "floor": 1,
        "totalBeds": 4,
        "availableBeds": 4,
        "roomStatus": 1,
        "price": 1200.00
      }
    ],
    "current": 1,
    "size": 10
  },
  "timestamp": 1700000000000
}
```

## 四、入住申请接口测试

### 4.1 提交入住申请
```
POST /api/checkin/submit
Content-Type: application/json

请求体:
{
  "studentId": 2,
  "studentName": "张三",
  "studentNumber": "2024001",
  "phone": "13800138001",
  "gender": 1,
  "department": "计算机学院",
  "major": "软件工程",
  "className": "软工2401",
  "preferredBuildingId": 1,
  "applicationReason": "新生入学申请"
}

预期响应:
{
  "code": 200,
  "message": "提交成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 4.2 审批入住申请
```
POST /api/checkin/approve
Content-Type: application/json

请求体:
{
  "id": 1,
  "approverId": 1,
  "approverName": "系统管理员",
  "status": 2,
  "remark": "审批通过，分配宿舍",
  "roomId": 1,
  "bedId": 1
}

预期响应:
{
  "code": 200,
  "message": "审批成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 4.3 确认入住
```
POST /api/checkin/confirm/1

预期响应:
{
  "code": 200,
  "message": "确认入住成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 4.4 查询入住申请列表
```
GET /api/checkin/page?current=1&size=10&status=1

预期响应:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 5,
    "records": [
      {
        "id": 1,
        "studentName": "张三",
        "studentNumber": "2024001",
        "applicationStatus": 1,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "current": 1,
    "size": 10
  },
  "timestamp": 1700000000000
}
```

## 五、报修管理接口测试

### 5.1 提交报修申请
```
POST /api/repair/submit
Content-Type: application/json

请求体:
{
  "studentId": 2,
  "studentName": "张三",
  "phone": "13800138001",
  "roomId": 1,
  "buildingName": "1号楼",
  "roomNumber": "101",
  "repairType": 1,
  "repairTitle": "水龙头漏水",
  "repairContent": "洗手间水龙头一直滴水，需要维修",
  "repairImages": "[\"http://example.com/image1.jpg\"]",
  "urgencyLevel": 2
}

预期响应:
{
  "code": 200,
  "message": "提交成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 5.2 维修人员接单
```
POST /api/repair/accept
Content-Type: application/json

请求体:
{
  "id": 1,
  "handlerId": 3,
  "handlerName": "王师傅"
}

预期响应:
{
  "code": 200,
  "message": "接单成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 5.3 更新维修进度
```
POST /api/repair/status
Content-Type: application/json

请求体:
{
  "id": 1,
  "status": 4,
  "remark": "已更换新水龙头，维修完成"
}

预期响应:
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 5.4 满意度评价
```
POST /api/repair/rate
Content-Type: application/json

请求体:
{
  "id": 1,
  "rating": 5,
  "comment": "维修及时，师傅态度好，非常满意！"
}

预期响应:
{
  "code": 200,
  "message": "评价成功",
  "data": null,
  "timestamp": 1700000000000
}
```

### 5.5 查询报修列表
```
GET /api/repair/page?current=1&size=10&status=1

预期响应:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 15,
    "records": [
      {
        "id": 1,
        "studentName": "张三",
        "repairTitle": "水龙头漏水",
        "repairStatus": 1,
        "urgencyLevel": 2,
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "current": 1,
    "size": 10
  },
  "timestamp": 1700000000000
}
```

## 六、数据统计接口测试

### 6.1 首页统计数据
```
GET /api/statistics/dashboard

预期响应:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "buildingCount": 10,
    "roomCount": 200,
    "totalBeds": 800,
    "availableBeds": 150,
    "occupiedBeds": 650,
    "occupancyRate": "81.25",
    "pendingCheckIn": 5,
    "pendingRepair": 12,
    "unpaidFee": 30
  },
  "timestamp": 1700000000000
}
```

### 6.2 房间状态统计
```
GET /api/statistics/room-status

预期响应:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "available": 30,
    "partiallyOccupied": 100,
    "full": 65,
    "maintenance": 5
  },
  "timestamp": 1700000000000
}
```

### 6.3 报修类型统计
```
GET /api/statistics/repair-type

预期响应:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "water_electric": 45,
    "furniture": 23,
    "network": 18,
    "other": 10
  },
  "timestamp": 1700000000000
}
```

### 6.4 费用统计
```
GET /api/statistics/fee

预期响应:
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "paidTotal": 580000.00,
    "unpaidTotal": 120000.00,
    "total": 700000.00
  },
  "timestamp": 1700000000000
}
```

## 七、异常情况测试

### 7.1 登录失败 - 用户不存在
```
POST /api/auth/login
{
  "username": "nonexist",
  "password": "123456"
}

预期响应:
{
  "code": 500,
  "message": "用户不存在",
  "data": null,
  "timestamp": 1700000000000
}
```

### 7.2 登录失败 - 密码错误
```
POST /api/auth/login
{
  "username": "admin",
  "password": "wrongpass"
}

预期响应:
{
  "code": 500,
  "message": "密码错误",
  "data": null,
  "timestamp": 1700000000000
}
```

### 7.3 新增失败 - 楼宇编号重复
```
POST /api/building
{
  "buildingCode": "B001",
  ...
}

预期响应:
{
  "code": 500,
  "message": "楼宇编号已存在",
  "data": null,
  "timestamp": 1700000000000
}
```

### 7.4 参数校验失败
```
POST /api/auth/login
{
  "username": "",
  "password": ""
}

预期响应:
{
  "code": 500,
  "message": "参数校验失败: username: 用户名不能为空, password: 密码不能为空",
  "data": null,
  "timestamp": 1700000000000
}
```

## 八、性能测试建议

### 8.1 并发测试
使用JMeter或Apache Bench进行并发测试：
```bash
# 使用Apache Bench测试登录接口
ab -n 1000 -c 100 -p login.json -T application/json http://localhost:8080/api/auth/login
```

### 8.2 压力测试指标
- **响应时间**: < 500ms（正常情况）
- **并发用户**: 支持100+并发
- **成功率**: > 99%
- **CPU使用率**: < 80%
- **内存使用率**: < 70%

## 九、测试用例清单

| 模块 | 测试用例 | 优先级 | 状态 |
|------|----------|--------|------|
| 认证 | 管理员登录 | P0 | ✅ |
| 认证 | 学生注册 | P0 | ✅ |
| 楼宇 | 新增楼宇 | P1 | ✅ |
| 楼宇 | 查询列表 | P1 | ✅ |
| 房间 | 新增房间 | P1 | ✅ |
| 房间 | 更新状态 | P1 | ✅ |
| 入住 | 提交申请 | P0 | ✅ |
| 入住 | 审批通过 | P0 | ✅ |
| 报修 | 提交报修 | P0 | ✅ |
| 报修 | 接单维修 | P0 | ✅ |
| 统计 | 首页数据 | P1 | ✅ |
| 异常 | 参数校验 | P1 | ✅ |

## 十、自动化测试脚本

使用Postman Collection可以批量运行所有测试用例。建议：
1. 导出Postman Collection
2. 配置环境变量
3. 使用Newman运行测试
4. 集成到CI/CD流程

```bash
# 安装Newman
npm install -g newman

# 运行测试集合
newman run SmartApartment.postman_collection.json -e prod.postman_environment.json
```

---

**测试完成标准**：
- ✅ 所有P0用例通过
- ✅ 所有P1用例通过
- ✅ 异常情况正确处理
- ✅ 接口文档与实际一致
- ✅ 性能指标达标
