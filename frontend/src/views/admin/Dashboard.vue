<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon building">
              <el-icon :size="40"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.buildingCount || 0 }}</div>
              <div class="stat-label">楼宇总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon room">
              <el-icon :size="40"><House /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.roomCount || 0 }}</div>
              <div class="stat-label">房间总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon bed">
              <el-icon :size="40"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.occupiedBeds || 0 }}/{{ dashboardData.totalBeds || 0 }}</div>
              <div class="stat-label">床位入住</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon rate">
              <el-icon :size="40"><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.occupancyRate || 0 }}%</div>
              <div class="stat-label">入住率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>房间状态统计</span>
          </template>
          <div ref="roomChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>报修类型统计</span>
          </template>
          <div ref="repairChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待办事项 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>待办事项</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="todo-item">
                <el-icon color="#409EFF" :size="24"><Document /></el-icon>
                <div class="todo-content">
                  <div class="todo-count">{{ dashboardData.pendingCheckIn || 0 }}</div>
                  <div class="todo-label">待审批入住申请</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="todo-item">
                <el-icon color="#E6A23C" :size="24"><Tools /></el-icon>
                <div class="todo-content">
                  <div class="todo-count">{{ dashboardData.pendingRepair || 0 }}</div>
                  <div class="todo-label">待处理报修</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="todo-item">
                <el-icon color="#F56C6C" :size="24"><Wallet /></el-icon>
                <div class="todo-content">
                  <div class="todo-count">{{ dashboardData.unpaidFee || 0 }}</div>
                  <div class="todo-label">未缴费记录</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardData, getRoomStatusStatistics, getRepairTypeStatistics } from '@/api/statistics'

const dashboardData = ref({})
const roomChartRef = ref(null)
const repairChartRef = ref(null)

const loadDashboardData = async () => {
  try {
    const res = await getDashboardData()
    dashboardData.value = res.data
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const initRoomChart = async () => {
  try {
    const res = await getRoomStatusStatistics()
    const data = res.data
    
    const chart = echarts.init(roomChartRef.value)
    const option = {
      tooltip: { trigger: 'item' },
      legend: { bottom: '5%' },
      series: [{
        name: '房间状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: data.available || 0, name: '空闲' },
          { value: data.partiallyOccupied || 0, name: '部分入住' },
          { value: data.full || 0, name: '已满' },
          { value: data.maintenance || 0, name: '维修中' }
        ]
      }]
    }
    chart.setOption(option)
    window.addEventListener('resize', () => chart.resize())
  } catch (error) {
    console.error('加载房间统计失败', error)
  }
}

const initRepairChart = async () => {
  try {
    const res = await getRepairTypeStatistics()
    const data = res.data
    
    const chart = echarts.init(repairChartRef.value)
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      xAxis: {
        type: 'category',
        data: ['水电维修', '家具维修', '网络故障', '其他']
      },
      yAxis: { type: 'value' },
      series: [{
        name: '报修数量',
        type: 'bar',
        data: [
          data.waterElectric || 0,
          data.furniture || 0,
          data.network || 0,
          data.other || 0
        ],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }]
    }
    chart.setOption(option)
    window.addEventListener('resize', () => chart.resize())
  } catch (error) {
    console.error('加载报修统计失败', error)
  }
}

onMounted(() => {
  loadDashboardData()
  initRoomChart()
  initRepairChart()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-icon.building {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.room {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.bed {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.rate {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.todo-item:hover {
  background: #e4e7ed;
}

.todo-content {
  flex: 1;
}

.todo-count {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.todo-label {
  font-size: 14px;
  color: #606266;
}
</style>
