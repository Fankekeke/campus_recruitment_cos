<template>
  <div class="rate-container" style="width: 100%;">

    <div class="chart-section">
      <!-- 学历分布饼图 -->
      <div class="chart-card">
        <h3>学历分布统计</h3>
        <a-skeleton v-if="chartLoading" active />
        <apexchart
          v-else
          type="pie"
          :options="pieChartOptions"
          :series="pieSeries"
          height="350"
        ></apexchart>
      </div>

      <!-- 学历分布柱状图 -->
      <div class="chart-card">
        <h3>各学历人数统计</h3>
        <a-skeleton v-if="chartLoading" active />
        <apexchart
          v-else
          type="bar"
          :options="barChartOptions"
          :series="barSeries"
          height="350"
        ></apexchart>
      </div>
    </div>

    <!-- 右侧学历列表 -->
    <div class="list-section">
      <h3>详细学历统计</h3>
      <a-skeleton v-if="chartLoading" active :paragraph="{ rows: 5 }" />
      <div v-else class="score-list">
        <div
          v-for="item in academicData"
          :key="item.academic"
          class="score-item"
          :class="{ 'highlight': item.count > 0 }"
        >
          <span class="score-label">{{ item.academic }}</span>
          <span class="score-count">{{ item.count }} 人</span>
          <div class="progress-bar">
            <div
              class="progress-fill"
              :style="{ width: `${(item.count / maxCount) * 100}%` }"
            ></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AcademicDistribution',
  data () {
    return {
      academicData: [],
      chartLoading: false,
      pieChartOptions: {
        chart: {
          id: 'pie-chart'
        },
        labels: [], // 动态设置学历标签
        title: {
          text: '学历分布统计'
        },
        legend: {
          position: 'bottom'
        }
      },
      pieSeries: [], // 动态设置饼图数据
      barChartOptions: {
        chart: {
          id: 'bar-chart'
        },
        xaxis: {
          categories: [], // 动态设置学历标签
          title: {
            text: '学历'
          }
        },
        yaxis: {
          title: {
            text: '人数'
          }
        },
        title: {
          text: '各学历人数统计'
        }
      },
      barSeries: [{
        name: '人数',
        data: []
      }],
      maxCount: 0
    }
  },
  mounted () {
    this.selectAcademicData()
  },
  methods: {
    selectAcademicData() {
      this.chartLoading = true;
      this.$get(`/cos/interview-info/queryEvaluateRate`).then((r) => {
        this.academicData = r.data.data;
        this.processAcademicData();
        setTimeout(() => {
          this.chartLoading = false;
        }, 500);
      }).catch(error => {
        console.error('请求学历数据出错:', error);
        this.chartLoading = false;
      });
    },

    processAcademicData() {
      const labels = [];
      const pieData = [];
      const barCategories = [];
      const barData = [];

      // 找到最大数量用于进度条显示
      this.maxCount = Math.max(...this.academicData.map(item => item.count), 1);

      this.academicData.forEach(item => {
        labels.push(item.academic);
        pieData.push(item.count);
        barCategories.push(item.academic);
        barData.push(item.count);
      });

      // 更新饼图配置
      this.pieChartOptions = {
        ...this.pieChartOptions,
        labels: labels
      };

      this.pieSeries = pieData;

      // 更新柱状图配置
      this.barChartOptions = {
        ...this.barChartOptions,
        xaxis: {
          categories: barCategories,
          title: {
            text: '学历'
          }
        }
      };

      this.barSeries = [
        {
          name: '人数',
          data: barData
        }
      ];
    }
  }
}
</script>

<style scoped>.rate-container {
  display: flex;
  padding: 20px;
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

.score-list {
  max-height: 500px;
  overflow-y: auto;
}

.score-item {
  padding: 10px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s;
}

.score-item.highlight {
  background-color: #f0f9ff;
  border-left: 3px solid #409eff;
}

.score-label {
  font-weight: bold;
  color: #333;
}

.score-count {
  float: right;
  color: #666;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background-color: #e0e0e0;
  border-radius: 3px;
  margin-top: 5px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #409eff;
  transition: width 0.3s ease;
}

@media (max-width: 768px) {
  .rate-container {
    flex-direction: column;
  }
}
</style>
