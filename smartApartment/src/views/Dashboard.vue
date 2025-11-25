<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon building">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.buildingCount }}</div>
              <div class="stat-label">楼宇总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon room">
              <el-icon><House /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.roomCount }}</div>
              <div class="stat-label">房间总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon bed">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.occupiedBeds }}/{{ dashboardData.totalBeds }}</div>
              <div class="stat-label">床位入住</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon rate">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ dashboardData.occupancyRate }}%</div>
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
            <div class="card-header">
              <span>房间状态统计</span>
            </div>
          </template>
          <div ref="roomChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>报修类型统计</span>
            </div>
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
            <div class="card-header">
              <span>待办事项</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="todo-item">
                <el-icon color="#409EFF" :size="24"><Document /></el-icon>
                <div class="todo-content">
                  <div class="todo-count">{{ dashboardData.pendingCheckIn }}</div>
                  <div class="todo-label">待审批入住申请</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="todo-item">
                <el-icon color="#E6A23C" :size="24"><Tools /></el-icon>
                <div class="todo-content">
                  <div class="todo-count">{{ dashboardData.pendingRepair }}</div>
                  <div class="todo-label">待处理报修</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="todo-item">
                <el-icon color="#F56C6C" :size="24"><Wallet /></el-icon>
                <div class="todo-content">
                  <div class="todo-count">{{ dashboardData.unpaidFee }}</div>
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
  const res = await getDashboardData()
  dashboardData.value = res.data
}

const initRoomChart = async () => {
  const res = await getRoomStatusStatistics()
  const data = res.data
  
  const chart = echarts.init(roomChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      bottom: '5%'
    },
    series: [
      {
        name: '房间状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: data.available, name: '空闲' },
          { value: data.partiallyOccupied, name: '部分入住' },
          { value: data.full, name: '已满' },
          { value: data.maintenance, name: '维修中' }
        ]
      }
    ]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

const initRepairChart = async () => {
  const res = await getRepairTypeStatistics()
  const data = res.data
  
  const chart = echarts.init(repairChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: ['水电', '家具', '网络', '其他']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '报修数量',
        type: 'bar',
        data: [data.water_electric, data.furniture, data.network, data.other],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

onMounted(() => {
  loadDashboardData()
  initRoomChart()
  initRepairChart()
})
</script>

<style scoped>
.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  margin-right: 15px;
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

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.card-header {
  font-weight: 500;
}

.todo-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.todo-content {
  margin-left: 12px;
}

.todo-count {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.todo-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}
</style>
