
<template>
  <a-row :gutter="[24, 24]" class="venue-container">
    <!-- 搜索表单区域 -->
    <a-col :span="24">
      <a-card class="search-card">
        <a-form layout="horizontal">
          <a-row :gutter="16">
            <a-col :md="8" :sm="24">
              <a-form-item label="企业名称" :labelCol="{ span: 6 }" :wrapperCol="{ span: 16 }">
                <a-input
                  v-model="queryParams.enterpriseName"
                  placeholder="请输入企业名称"
                  allow-clear
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="会场名称" :labelCol="{ span: 6 }" :wrapperCol="{ span: 16 }">
                <a-input
                  v-model="queryParams.name"
                  placeholder="请输入会场名称"
                  allow-clear
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24" style="display: flex; align-items: flex-end;">
              <a-button type="primary" @click="search" icon="search" class="search-btn">查询</a-button>
              <a-button @click="resetSearch" style="margin-left: 8px;" icon="sync">重置</a-button>
            </a-col>
          </a-row>
        </a-form>
      </a-card>
    </a-col>

    <!-- 会场列表区域 -->
    <a-col :span="24">
      <div class="results-header">
        <h2><a-icon type="calendar" /> 招聘会场</h2>
        <span class="result-count">共 {{ rentList.length }} 个会场</span>
      </div>

      <div v-if="rentList.length === 0" class="empty-state">
        <a-empty description="未找到符合条件的会场信息" />
      </div>

      <div v-else>
        <a-col :span="8"
               v-for="(item, index) in rentList"
               :key="index"
               class="venue-item-wrapper"
        >
          <a-card
            :bordered="false"
            hoverable
            class="venue-card"
          >
            <!-- 会场头部信息 -->
            <template slot="title">
              <div class="venue-title">
                <div class="venue-name">{{ item.name }}</div>
                <div class="venue-code">{{ item.code }}</div>
              </div>
            </template>

            <!-- 会场描述 -->
            <div class="venue-description">
              <a-icon type="environment" class="location-icon" />
              <span class="venue-address">{{ item.address }}</span>
            </div>

            <!-- 招聘时间 -->
            <div class="venue-time">
              <a-icon type="clock-circle" class="time-icon" />
              <span class="time-range">{{ item.startDate }} ~ {{ item.endDate }}</span>
            </div>

            <!-- 时间状态 -->
            <div class="venue-status">
              <a-tag :color="getStatusColor(item)">
                {{ getStatusText(item) }}
              </a-tag>
            </div>

            <!-- 企业信息 -->
            <div class="enterprise-info">
              <a-avatar
                size="large"
                shape="square"
                icon="bank"
                :src="'http://127.0.0.1:9527/imagesWeb/' + item.enterpriseImages"
                class="enterprise-logo"
              />
              <div class="enterprise-name">{{ item.enterpriseName }}</div>
            </div>

            <!-- 操作按钮 -->
            <template slot="actions" class="venue-actions">
              <a-button
                :disabled="!isInTimeRange(item.startDate, item.endDate)"
                type="primary"
                ghost
                size="small"
                @click="sendInter(item)"
                class="action-btn reserve-btn"
              >
                <a-icon type="calendar" /> 预约
              </a-button>
              <a-button
                type="default"
                size="small"
                @click="venueViewOpen(item)"
                class="action-btn detail-btn"
              >
                <a-icon type="info-circle" /> 详情
              </a-button>
            </template>
          </a-card>
        </a-col>
      </div>
    </a-col>

    <!-- 会场详情弹窗 -->
    <venue-view
      @close="handlevenueViewClose"
      :venueShow="venueView.visiable"
      :venueData="venueView.data">
    </venue-view>
  </a-row>
</template>

<script>import RentView from '../../manage/post/PostView.vue'
import {mapState} from 'vuex'
import venueView from './VenueView.vue'

export default {
  name: 'Venue',
  components: {RentView, venueView},
  data () {
    return {
      rentList: [],
      rentView: {
        visiable: false,
        data: null
      },
      venueView: {
        visiable: false,
        data: null
      },
      queryParams: {}
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  mounted () {
    this.selectRentList()
  },
  methods: {
    // 检查当前日期是否在指定时间段内
    isInTimeRange (startDate, endDate) {
      const now = new Date()
      const start = new Date(startDate)
      const end = new Date(endDate)
      return now >= start && now <= end
    },

    // 获取状态文本
    getStatusText (item) {
      if (this.isInTimeRange(item.startDate, item.endDate)) {
        return '进行中'
      } else if (new Date(item.endDate) < new Date()) {
        return '已结束'
      } else {
        return '即将开始'
      }
    },

    // 获取状态颜色
    getStatusColor (item) {
      if (this.isInTimeRange(item.startDate, item.endDate)) {
        return 'green' // 进行中 - 绿色
      } else if (new Date(item.endDate) < new Date()) {
        return 'red' // 已结束 - 红色
      } else {
        return 'orange' // 即将开始 - 橙色
      }
    },

    venueViewOpen (row) {
      this.venueView.data = row
      this.venueView.visiable = true
    },
    handlevenueViewClose () {
      this.venueView.visiable = false
    },
    search () {
      this.selectRentList({
        ...this.queryParams
      })
    },
    resetSearch () {
      this.queryParams = {}
      this.selectRentList()
    },
    sendInter (row) {
      // 检查是否在有效时间内
      if (!this.isInTimeRange(row.startDate, row.endDate)) {
        this.$message.warning('当前不在预约时间段内')
        return
      }

      this.$post('/cos/reserve-info', {
        studentId: this.currentUser.userId,
        venueId: row.id
      }).then((r) => {
        this.$message.success('预约提交成功,请等待审核')
      })
    },
    selectRentList (params = {}) {
      params.delFlag = 1
      params.size = 999
      this.$get('/cos/venue-info/page', {...params}).then((r) => {
        this.rentList = r.data.data.records
        console.log(this.rentList)
      })
    }
  }
}
</script>

<style scoped>.venue-container {
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
  width: 100%;
}

.search-card {
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.search-btn {
  margin-right: 8px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.results-header h2 {
  margin: 0;
  color: #1890ff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.result-count {
  color: #888;
  font-size: 14px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}

.venue-grid {
  display: grid;
  /*grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));*/
  /*gap: 24px;*/
}

.venue-item-wrapper {
  padding: 0;
}

.venue-card {
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.venue-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.venue-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.venue-name {
  font-weight: 600;
  font-size: 16px;
  color: #333;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.venue-code {
  font-size: 12px;
  color: #888;
  background-color: #f0f2f5;
  padding: 2px 8px;
  border-radius: 4px;
  margin-left: 8px;
  flex-shrink: 0;
}

.venue-description {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  color: #666;
}

.location-icon {
  color: #1890ff;
  margin-right: 6px;
}

.venue-address {
  font-size: 14px;
  word-break: break-word;
}

.venue-time {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  color: #666;
}

.time-icon {
  color: #1890ff;
  margin-right: 6px;
}

.time-range {
  font-size: 13px;
}

.venue-status {
  margin-bottom: 10px;
}

.enterprise-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
  flex: 1;
}

.enterprise-logo {
  margin-right: 10px;
}

.enterprise-name {
  font-weight: 600;
  color: #333;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.venue-actions {
  display: flex;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.action-btn {
  flex: 1;
  margin: 0 4px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.reserve-btn {
  border-color: #1890ff;
  color: #1890ff;
}

.detail-btn {
  border-color: #52c41a;
  color: #52c41a;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .venue-container {
    padding: 10px;
  }

  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .venue-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .venue-title {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .venue-code {
    margin-left: 0;
  }

  .enterprise-info {
    flex-direction: column;
    align-items: flex-start;
  }

  .enterprise-logo {
    margin-bottom: 10px;
  }
}
</style>
