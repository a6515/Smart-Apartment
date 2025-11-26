# 智能宿舍管理系统 - 业务流程文档

## 一、入住流程 (Check-In Process)

### 1.1 流程图
```
学生提交申请 → 管理员审批 → 分配房间床位 → 学生确认入住 → 完成
   (状态1)      (状态2/3)         (状态2)        (状态4)
```

### 1.2 状态码说明
- **1**: 待审核 (Pending)
- **2**: 审批通过 (Approved) 
- **3**: 审批驳回 (Rejected)
- **4**: 已确认入住 (Checked In)
- **5**: 已退宿 (Checked Out)

### 1.3 涉及的API接口

| 接口路径 | HTTP方法 | 权限 | 说明 |
|---------|---------|------|------|
| `/api/checkin/submit` | POST | 学生(3) | 学生提交入住申请 |
| `/api/checkin/page` | GET | 全部(1,2,3) | 查询入住申请列表 |
| `/api/checkin/approve` | POST | 管理员/宿管(1,2) | 审批入住申请并分配床位 |
| `/api/checkin/confirm/{id}` | POST | 管理员/宿管(1,2) | 确认学生已入住 |
| `/api/checkin/cancel/{id}` | DELETE | 学生(3) | 学生撤销待审批申请 |
| `/api/checkin/{id}` | DELETE | 管理员/宿管(1,2) | 管理员删除申请 |
| `/api/checkin/roommates/{roomId}` | GET | 全部(1,2,3) | 查询室友列表 |

### 1.4 涉及的数据库表

| 表名 | 作用 | 关键字段 |
|------|------|----------|
| `check_in_application` | 入住申请主表 | `id`, `student_id`, `application_status`, `assigned_room_id`, `assigned_bed_id` |
| `bed` | 床位表 | `id`, `bed_status`, `student_id`, `student_name`, `check_in_time` |
| `room` | 房间表 | `id`, `available_beds`, `total_beds`, `room_status` |
| `sys_user` | 用户表 | `id`, `real_name`, `student_number` |

### 1.5 详细步骤

#### 步骤1: 学生提交申请
- **触发**: 学生在前端填写入住申请表单
- **后端处理**:
  1. 自动填充学生信息(从token获取)
  2. 设置状态为1(待审核)
  3. 插入`check_in_application`表
- **SQL操作**: `INSERT INTO check_in_application`

#### 步骤2: 管理员审批
- **触发**: 管理员在审批页面点击"通过"或"驳回"
- **后端处理**:
  1. 验证申请状态必须为1
  2. 更新审批人信息、审批时间、审批意见
  3. 如果通过(status=2):
     - 检查床位可用性
     - 更新床位状态为2(已占用)
     - 设置床位的student_id和student_name
     - 房间available_beds减1
     - 更新房间状态(空闲/部分入住/已满)
- **SQL操作**: 
  - `UPDATE check_in_application`
  - `UPDATE bed SET bed_status=2, student_id=?, student_name=?`
  - `UPDATE room SET available_beds = available_beds - 1`

#### 步骤3: 确认入住
- **触发**: 管理员确认学生已实际入住
- **后端处理**:
  1. 验证状态必须为2(审批通过)
  2. 更新状态为4(已入住)
  3. 记录入住时间
- **SQL操作**: `UPDATE check_in_application SET application_status=4`

---

## 二、换宿流程 (Transfer Process)

### 2.1 流程图
```
学生提交换宿申请 → 管理员审批 → 分配新床位 → 释放旧床位 → 更新入住记录 → 完成
    (状态1)        (状态2/3)      同时处理旧房间和新房间    (状态5)
```

### 2.2 状态码说明
- **1**: 待审核 (Pending)
- **2**: 审批通过 (Approved)
- **3**: 审批驳回 (Rejected)
- **4**: 已撤销 (Cancelled)
- **5**: 已完成 (Completed) - 换宿完成后自动设置

### 2.3 涉及的API接口

| 接口路径 | HTTP方法 | 权限 | 说明 |
|---------|---------|------|------|
| `/api/transfer/apply` | POST | 学生(3) | 提交换宿申请 |
| `/api/transfer/page` | GET | 全部(1,2,3) | 查询换宿申请列表 |
| `/api/transfer/approve` | POST | 管理员/宿管(1,2) | 审批换宿申请并分配新床位 |

### 2.4 涉及的数据库表

| 表名 | 作用 | 关键字段 |
|------|------|----------|
| `transfer_application` | 换宿申请表 | `id`, `student_id`, `current_room_id`, `current_bed_id`, `target_room_id`, `assigned_bed_id`, `status` |
| `check_in_application` | 入住申请表 | `assigned_room_id`, `assigned_bed_id` - **关键:换宿会更新这里** |
| `bed` | 床位表 | 旧床位释放，新床位占用 |
| `room` | 房间表 | 旧房间available_beds+1，新房间available_beds-1 |

### 2.5 详细步骤

#### 步骤1: 学生提交换宿申请
- **前置条件**: 学生必须已入住(check_in_application.status=4)
- **后端处理**:
  1. 查询学生的入住记录
  2. **从room表获取当前楼宇ID**(重要修复点)
  3. 填充当前房间床位信息
  4. 检查是否有待处理的换宿申请
  5. 插入`transfer_application`表
- **SQL操作**: `INSERT INTO transfer_application`

#### 步骤2: 管理员审批换宿
- **触发**: 管理员选择新床位并审批
- **后端处理**(如果同意):
  1. **释放旧床位**:
     - bed_status=1(空闲)
     - student_id=null
     - student_name=null
     - check_in_time=null
  2. **占用新床位**:
     - bed_status=2(已占用)
     - student_id=申请人ID
     - student_name=申请人姓名
     - check_in_time=当前时间
  3. **更新旧房间**:
     - available_beds+1
     - 更新room_status
  4. **更新新房间**:
     - available_beds-1
     - 更新room_status
  5. **更新入住申请记录**(核心):
     - assigned_room_id=新房间ID
     - assigned_bed_id=新床位ID
  6. **更新换宿申请状态**:
     - status=5(已完成)
- **SQL操作**:
  - `UPDATE bed` (旧床位和新床位各一次)
  - `UPDATE room` (旧房间和新房间各一次)
  - `UPDATE check_in_application` (更新入住记录，使"我的房间"显示新位置)
  - `UPDATE transfer_application` (更新状态为已完成)

---

## 三、退宿流程 (Checkout Process)

### 3.1 流程图
```
学生提交退宿申请 → 管理员审批 → 释放床位 → 更新入住状态 → 完成
    (状态1)        (状态2/3)     bed_status=1  application_status=5
```

### 3.2 状态码说明
- **1**: 待审核 (Pending)
- **2**: 审批通过 (Approved)
- **3**: 审批驳回 (Rejected)

### 3.3 涉及的API接口

| 接口路径 | HTTP方法 | 权限 | 说明 |
|---------|---------|------|------|
| `/api/checkout/apply` | POST | 学生(3) | 提交退宿申请 |
| `/api/checkout/page` | GET | 全部(1,2,3) | 查询退宿申请列表 |
| `/api/checkout/approve` | POST | 管理员/宿管(1,2) | 审批退宿申请 |

### 3.4 涉及的数据库表

| 表名 | 作用 | 关键字段 |
|------|------|----------|
| `checkout_application` | 退宿申请表 | `id`, `student_id`, `room_id`, `bed_id`, `status`, `checkout_date` |
| `check_in_application` | 入住申请表 | `application_status` - **更新为5(已退宿)** |
| `bed` | 床位表 | bed_status=1(释放) |
| `room` | 房间表 | available_beds+1 |

### 3.5 详细步骤

#### 步骤1: 学生提交退宿申请
- **前置条件**: 学生必须已入住(check_in_application.status=4)
- **后端处理**:
  1. 验证学生入住状态
  2. 检查是否有未处理的退宿申请
  3. 填充房间床位信息
  4. 插入`checkout_application`表
- **SQL操作**: `INSERT INTO checkout_application`

#### 步骤2: 管理员审批退宿
- **后端处理**(如果同意):
  1. **释放床位**:
     - bed_status=1(空闲)
  2. **更新房间**:
     - available_beds+1
     - 更新room_status
  3. **更新入住申请**:
     - application_status=5(已退宿)
- **SQL操作**:
  - `UPDATE bed SET bed_status=1`
  - `UPDATE room SET available_beds = available_beds + 1`
  - `UPDATE check_in_application SET application_status=5`
  - `UPDATE checkout_application` (更新审批信息)

---

## 四、关键设计说明

### 4.1 数据一致性保证
所有涉及多表操作的方法都使用了 `@Transactional` 注解，确保：
- 床位分配/释放
- 房间状态更新
- 申请状态更新
这些操作要么全部成功，要么全部回滚。

### 4.2 "我的房间"页面数据来源
**查询路径**:
```
check_in_application (主表，status=4)
  ├─ assigned_room_id → room.id → room.building_id → building.building_name
  ├─ assigned_room_id → room.room_number
  ├─ assigned_room_id → room.floor
  └─ assigned_bed_id → bed.bed_number
```

**重要**: 
- **楼宇名称**从 `room.building_id` JOIN `building` 获取，**不是**从 `preferred_building_id`
- 换宿成功后，更新 `check_in_application` 的 `assigned_room_id` 和 `assigned_bed_id`，页面自动显示新位置

### 4.3 床位统计
仪表盘的"床位入住"统计使用 `room` 表的聚合数据：
```sql
总床位 = SUM(room.total_beds)
可用床位 = SUM(room.available_beds)
已入住 = 总床位 - 可用床位
```

### 4.4 核心表关系
```
sys_user (学生信息)
    ↓
check_in_application (入住申请 - 核心表，记录学生当前位置)
    ├─ assigned_room_id → room (房间)
    ├─ assigned_bed_id → bed (床位)
    ├─ transfer_application (换宿申请引用)
    └─ checkout_application (退宿申请引用)
```

---

## 五、常见问题排查

### 问题1: 换宿后"我的房间"没有更新
**原因**: `check_in_application` 表的 `assigned_room_id` 和 `assigned_bed_id` 没有更新
**解决**: 确保 `TransferService.approve()` 方法中更新了 `check_in_application`

### 问题2: 楼宇显示错误
**原因**: SQL JOIN 使用了 `preferred_building_id` 而不是 `room.building_id`
**解决**: 修改 `CheckInApplicationMapper.xml` 的 JOIN 逻辑

### 问题3: Mapper 更新覆盖了其他字段
**原因**: `updateById` 使用固定SET，会将null值写入数据库
**解决**: 使用动态 SQL `<if test="field != null">`

---

## 六、角色权限说明

| 角色ID | 角色名称 | 权限范围 |
|--------|----------|----------|
| 1 | 管理员 | 所有操作 |
| 2 | 宿管 | 审批、确认、管理房间床位 |
| 3 | 学生 | 提交申请、查看自己的记录、撤销申请 |

**注解示例**:
- `@RequireRole({1, 2, 3})` - 所有角色可访问
- `@RequireRole({1, 2})` - 仅管理员和宿管
- `@RequireRole({3})` - 仅学生
