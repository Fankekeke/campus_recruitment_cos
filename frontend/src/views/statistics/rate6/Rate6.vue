
<template>
  <div class="rate-container" style="width: 100%">
    <!-- 就业率概览卡片 -->
    <a-row :gutter="16" style="margin-bottom: 24px;">
      <a-col :span="6">
        <a-card :bordered="false" class="stat-card">
          <a-statistic
            title="总学生数"
            :value="totalStudents"
            :value-style="{ color: '#3f86a0' }"
          >
            <template slot="prefix">
              <a-icon type="team" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" class="stat-card">
          <a-statistic
            title="已就业人数"
            :value="employedStudents"
            :value-style="{ color: '#cf1322' }"
          >
            <template slot="prefix">
              <a-icon type="check-circle" theme="twoTone" twoToneColor="#52c41a" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" class="stat-card">
          <a-statistic
            title="未就业人数"
            :value="unemployedStudents"
            :value-style="{ color: '#f5222d' }"
          >
            <template slot="prefix">
              <a-icon type="exclamation-circle" theme="twoTone" twoToneColor="#f5222d" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" class="stat-card">
          <a-statistic
            title="就业率"
            :value="employmentRate"
            :precision="2"
            suffix="%"
            :value-style="{ color: '#1890ff', fontWeight: 'bold' }"
          >
            <template slot="prefix">
              <a-icon type="rise" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- 图表区域 -->
    <a-row :gutter="16">
      <!-- 城市分布统计 -->
      <a-col :span="12">
        <a-card title="就业城市分布" :bordered="false" class="chart-card">
          <div ref="cityChart" class="chart-container"></div>
        </a-card>
      </a-col>

      <!-- 就业去向统计 -->
      <a-col :span="12">
        <a-card title="就业去向统计" :bordered="false" class="chart-card">
          <div ref="destinationChart" class="chart-container"></div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 详细数据表格 -->
    <a-row style="margin-top: 24px;">
      <a-col :span="24">
        <a-card title="城市就业详情" :bordered="false">
          <a-table
            :columns="cityColumns"
            :data-source="cityStats"
            :pagination="false"
            size="middle"
          >
            <template slot="percentageCell" slot-scope="text">
              <a-tag :color="getPercentageColor(text)">
                {{ text }}%
              </a-tag>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>

    <a-row style="margin-top: 16px;">
      <a-col :span="24">
        <a-card title="就业去向详情" :bordered="false">
          <a-table
            :columns="destinationColumns"
            :data-source="destinationStats"
            :pagination="false"
            size="middle"
          >
            <template slot="percentageCell" slot-scope="text">
              <a-tag :color="getPercentageColor(text)">
                {{ text }}%
              </a-tag>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>import * as echarts from 'echarts'

export default {
  name: 'Rate6',
  data() {
    return {
      // 就业率数据
      totalStudents: 0,
      employedStudents: 0,
      unemployedStudents: 0,
      employmentRate: 0,
      // 城市统计数据
      cityStats: [],
      // 去向统计数据
      destinationStats: [],
      // 城市表格列
      cityColumns: [
        {
          title: '城市',
          dataIndex: 'city',
          key: 'city',
          scopedSlots: { customRender: 'city' }
        },
        {
          title: '就业人数',
          dataIndex: 'count',
          key: 'count',
          sorter: (a, b) => a.count - b.count
        },
        {
          title: '占比',
          dataIndex: 'percentage',
          key: 'percentage',
          scopedSlots: { customRender: 'percentageCell' }
        }
      ],
      // 去向表格列
      destinationColumns: [
        {
          title: '去向类型',
          dataIndex: 'typeName',
          key: 'typeName'
        },
        {
          title: '类型代码',
          dataIndex: 'type',
          key: 'type',
          customRender: (text) => {
            const typeMap = {
              1: '签协议就业',
              2: '升学',
              3: '灵活就业',
              4: '自主创业',
              5: '其他'
            }
            return typeMap[text] || text
          }
        },
        {
          title: '人数',
          dataIndex: 'count',
          key: 'count',
          sorter: (a, b) => a.count - b.count
        },
        {
          title: '占比',
          dataIndex: 'percentage',
          key: 'percentage',
          scopedSlots: { customRender: 'percentageCell' }
        }
      ],
      // ECharts 实例
      cityChartInstance: null,
      destinationChartInstance: null
    }
  },
  mounted() {
    this.selectRate()
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.cityChartInstance) {
      this.cityChartInstance.dispose()
    }
    if (this.destinationChartInstance) {
      this.destinationChartInstance.dispose()
    }
  },
  methods: {
    selectRate() {
      this.$get(`/cos/interview-info/queryEmploymentRate`).then((r) => {
        console.log('就业率数据:', r.data)
        if (r.data.code === 0) {
          const data = r.data
          // 更新基础数据
          this.totalStudents = data.totalStudents || 0
          this.employedStudents = data.employedStudents || 0
          this.unemployedStudents = data.unemployedStudents || 0
          this.employmentRate = data.employmentRate || 0
          // 更新统计数据
          this.cityStats = data.cityStats || []
          this.destinationStats = data.destinationStats || []

          // 渲染图表
          this.$nextTick(() => {
            this.renderCityChart()
            this.renderDestinationChart()
          })
        }
      }).catch(error => {
        console.error('请求就业率数据出错:', error)
      })
    },

    // 渲染城市分布饼图
    renderCityChart() {
      const chartDom = this.$refs.cityChart
      if (!chartDom) return

      if (this.cityChartInstance) {
        this.cityChartInstance.dispose()
      }

      this.cityChartInstance = echarts.init(chartDom)

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}人 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: this.cityStats.map(item => item.city)
        },
        series: [
          {
            name: '就业人数',
            type: 'pie',
            radius: '65%',
            data: this.cityStats.map(item => ({
              value: item.count,
              name: item.city
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label: {
              formatter: '{b}: {c}人'
            }
          }
        ]
      }

      this.cityChartInstance.setOption(option)
    },

    // 渲染就业去向饼图
    renderDestinationChart() {
      const chartDom = this.$refs.destinationChart
      if (!chartDom) return

      if (this.destinationChartInstance) {
        this.destinationChartInstance.dispose()
      }

      this.destinationChartInstance = echarts.init(chartDom)

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c}人 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: this.destinationStats.map(item => item.typeName)
        },
        series: [
          {
            name: '就业去向',
            type: 'pie',
            radius: '65%',
            data: this.destinationStats.map(item => ({
              value: item.count,
              name: item.typeName
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label: {
              formatter: '{b}: {c}人'
            }
          }
        ]
      }

      this.destinationChartInstance.setOption(option)
    },

    // 根据百分比返回颜色
    getPercentageColor(percentage) {
      if (percentage >= 50) return 'green'
      if (percentage >= 30) return 'blue'
      if (percentage >= 10) return 'orange'
      return 'red'
    },

    // 处理窗口大小变化
    handleResize() {
      if (this.cityChartInstance) {
        this.cityChartInstance.resize()
      }
      if (this.destinationChartInstance) {
        this.destinationChartInstance.resize()
      }
    }
  }
}
</script>

<style scoped>.rate-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.stat-card {
  margin-bottom: 16px;
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.chart-card {
  margin-bottom: 24px;
  border-radius: 8px;
  min-height: 400px;
}

.chart-container {
  width: 100%;
  height: 350px;
}

:deep(.ant-statistic-title) {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
}

:deep(.ant-statistic-content) {
  font-size: 24px;
  font-weight: 600;
}

:deep(.ant-card) {
  border-radius: 8px;
}

:deep(.ant-card-head) {
  font-weight: 600;
  background: #fafafa;
}
</style>
