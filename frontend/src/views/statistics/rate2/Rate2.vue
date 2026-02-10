
<template>
  <div class="rate-container" style="width: 100%;">
    <div class="chart-area">
      <!-- 左侧图表区域 -->
      <div class="chart-section">
        <!-- 数据加载完成后的图表 -->
        <div>
          <div class="chart-card">
            <h3>岗位区域占比折线图</h3>
            <a-skeleton v-if="loading" active />
            <apexchart
              v-else
              type="line"
              :options="lineChartOptions"
              :series="lineSeries"
              height="400"
            ></apexchart>
          </div>
          <div class="chart-card">
            <h3>岗位区域占比柱状图</h3>
            <a-skeleton v-if="loading" active />
            <apexchart
              v-else
              type="bar"
              :options="barChartOptions"
              :series="barSeries"
              height="400"
            ></apexchart>
          </div>
        </div>
      </div>

      <!-- 右侧列表区域 -->

      <div class="list-section">
        <!-- 加载中 Skeleton -->
        <a-skeleton v-if="loading" active />
        <!-- 数据加载完成后的列表 -->
        <div v-else>
          <h3>岗位区域占比列表</h3>
          <div class="province-list">
            <div
              v-for="(item, index) in provinceData"
              :key="index"
              class="province-item"
            >
              <div class="province-info">
                <span class="province-name">{{ item.province }}</span>
                <span class="province-count">{{ item.count }} ({{ item.percentage }}%)</span>
              </div>
              <div class="progress-bar">
                <div
                  class="progress-fill"
                  :style="{ width: `${item.percentage}%` }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Rate2',
  data () {
    return {
      loading: true, // 控制 Skeleton 显示
      provinceData: [],
      maxCount: 0,
      lineChartOptions: {
        chart: {
          id: 'line-chart'
        },
        xaxis: {
          categories: [],
          title: {
            text: '省份'
          }
        },
        yaxis: {
          title: {
            text: '数量'
          }
        },
        stroke: {
          curve: 'smooth'
        },
        markers: {
          size: 4
        }
      },
      lineSeries: [{
        name: '数量',
        data: []
      }],
      barChartOptions: {
        chart: {
          id: 'bar-chart'
        },
        xaxis: {
          categories: [],
          title: {
            text: '省份'
          }
        },
        yaxis: {
          title: {
            text: '数量'
          }
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '85%',
            endingShape: 'rounded'
          }
        }
      },
      barSeries: [{
        name: '数量',
        data: []
      }]
    }
  },
  mounted () {
    this.selectRate()
  },
  methods: {
    selectRate () {
      this.loading = true // 开始加载时显示 Skeleton
      this.$get(`/cos/interview-info/queryAreaScenicNumRate`).then((r) => {
        this.provinceData = r.data.data.map(item => ({
          province: item.city,
          count: item.count,
          percentage: item.percentage || 0
        }))
        this.processData()
        setTimeout(() => {
          this.loading = false
        }, 300) // 数据加载完成隐藏 Skeleton
      }).catch(error => {
        console.error('请求省份数量数据出错:', error)
        this.loading = false // 出错时也隐藏 Skeleton
      })
    },

    processData () {
      // 按岗位数量降序排序
      this.provinceData.sort((a, b) => b.count - a.count)

      // 计算总量
      const totalCount = this.provinceData.reduce((sum, item) => sum + item.count, 0)

      // 动态计算百分比
      this.provinceData.forEach(item => {
        item.percentage = ((item.count / totalCount) * 100).toFixed(2)
      })

      // 计算最大数量用于进度条
      this.maxCount = Math.max(...this.provinceData.map(item => item.count), 1)

      // 提取省份名称和数量用于图表
      const provinces = this.provinceData.map(item => item.province)
      const counts = this.provinceData.map(item => item.count)

      // 更新折线图数据
      this.lineChartOptions = {
        ...this.lineChartOptions,
        xaxis: {
          categories: provinces,
          title: {
            text: '城市'
          }
        }
      }
      this.lineSeries = [{
        name: '岗位数量',
        data: counts
      }]

      // 更新柱状图数据
      this.barChartOptions = {
        ...this.barChartOptions,
        xaxis: {
          categories: provinces,
          title: {
            text: '城市'
          }
        }
      }
      this.barSeries = [{
        name: '岗位数量',
        data: counts
      }]
    }
  }
}
</script>

<style scoped>.rate-container {
  padding: 20px;
}

.chart-area {
  display: flex;
  gap: 20px;
}

.chart-section {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.list-section {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.list-section h3 {
  margin-top: 0;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.province-list {
  max-height: 700px;
  overflow-y: auto;
}

.province-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.province-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.province-name {
  font-weight: bold;
  color: #333;
}

.province-count {
  color: #409eff;
  font-weight: bold;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #66b1ff);
  transition: width 0.3s ease;
}

@media (max-width: 768px) {
  .chart-area {
    flex-direction: column;
  }

  .chart-section {
    order: 2;
  }

  .list-section {
    order: 1;
  }
}

.skeleton-wrapper {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>
