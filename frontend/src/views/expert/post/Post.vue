
<template>
  <a-row :gutter="20" style="width: 100%; margin-top: 30px">
    <!-- 搜索表单区域 -->
    <a-col :span="24">
      <a-form layout="horizontal" :form="form" style="background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.05);">
        <a-row :gutter="15">
          <a-col :md="8" :sm="24">
            <a-form-item label="企业名称" :labelCol="{ span: 6 }" :wrapperCol="{ span: 16 }">
              <a-input v-model="queryParams.enterpriseName" placeholder="请输入企业名称" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="工作地点" :labelCol="{ span: 6 }" :wrapperCol="{ span: 16 }">
              <a-input v-model="queryParams.address" placeholder="请输入工作地点" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="岗位名称" :labelCol="{ span: 6 }" :wrapperCol="{ span: 16 }">
              <a-input v-model="queryParams.postName" placeholder="请输入岗位名称" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :span="24" style="text-align: center; margin-top: 10px;">
            <a-button type="primary" @click="search" icon="search">查询</a-button>
            <a-button @click="resetSearch" style="margin-left: 10px;">重置</a-button>
          </a-col>
        </a-row>
      </a-form>
    </a-col>

    <!-- 岗位列表区域 -->
    <a-col :span="24" style="margin-top: 20px;">
      <div v-if="rentList.length === 0" class="no-data-container">
        <a-empty description="未找到符合的岗位信息" />
      </div>

      <div class="post-list-container">
        <a-col :span="8"
               v-for="(item, index) in rentList"
               :key="index"
               class="post-item-wrapper"
        >
          <a-card
            :bordered="false"
            hoverable
            class="post-card"
          >
            <!-- 岗位基本信息 -->
            <template slot="title">
              <div class="post-title">
                <h3 class="post-name">{{ item.postName }}</h3>
                <div class="post-location">{{ item.address }}</div>
              </div>
            </template>

            <!-- 岗位职责摘要 -->
            <div class="post-description">
              <p>{{ item.responsibility ? (item.responsibility.slice(0, 60) + (item.responsibility.length > 60 ? '...' : '')) : '暂无描述' }}</p>
            </div>

            <!-- 福利标签 -->
            <div class="welfare-tags">
              <a-tag color="#108ee9" v-for="tag in parseWelfare(item.welfare)" :key="tag">{{ tag }}</a-tag>
            </div>

            <!-- 公司信息 -->
            <div class="company-info">
              <div class="company-logo">
                <a-avatar
                  size="large"
                  shape="square"
                  icon="bank"
                  :src="'http://127.0.0.1:9527/imagesWeb/' + item.enterpriseImages"
                />
              </div>
              <div class="company-details">
                <div class="company-name">{{ item.enterpriseName }}</div>
                <div class="industry-hour">
                  <span class="industry">{{ item.industryName || '未知行业' }}</span>
                  <span class="divider">|</span>
                  <span class="work-hour">{{ item.workHour || '未知工时' }}</span>
                </div>
              </div>
            </div>

            <!-- 薪资信息 -->
            <div class="salary-section">
              <span class="salary">{{ item.salary || '面议' }}</span>
            </div>

            <!-- 操作按钮 -->
            <template slot="actions" class="ant-card-actions">
              <div @click="sendInter(item)" class="action-item">
                <a-icon type="folder-open" theme="filled" /> 投递
              </div>
              <div @click="rentDetail(item)" class="action-item">
                <a-icon type="profile" /> 详情
              </div>
              <div
                @click="toggleCollect(item)"
                class="action-item"
                :class="{ collected: checkCollect(item.id) }"
              >
                <a-icon
                  :type="checkCollect(item.id) ? 'heart' : 'heart'"
                  :theme="checkCollect(item.id) ? 'filled' : 'outlined'"
                />
                {{ checkCollect(item.id) ? '取消' : '收藏' }}
              </div>
              <div @click="startAIVideoInterview(item)" class="action-item ai-interview-btn">
                <a-icon type="robot" theme="filled" /> AI面试
              </div>
              <div @click="chat(item)" class="action-item">
                <a-icon type="message" /> 联系
              </div>
            </template>
          </a-card>
        </a-col>
      </div>
    </a-col>

    <div style="text-align: center; margin-top: 20px;">
      <a-pagination
        v-model="pagination.current"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        @change="handlePageChange"
      />
    </div>

    <!-- 岗位详情弹窗 -->
    <rent-view
      :pluralismShow="rentView.visiable"
      :pluralismData="rentView.data"
      @close="rentView.visiable = false"
    ></rent-view>

    <!-- AI面试弹窗 -->
    <a-modal
      v-model="aiInterviewModal.visible"
      title="AI面试"
      :footer="null"
      :width="800"
      :bodyStyle="{ padding: '0' }"
      @cancel="endAIVideoInterview"
    >
      <div class="ai-interview-container">
        <div class="ai-interview-header">
          <div class="ai-interview-info">
            <h3>{{ aiInterviewModal.jobTitle }}</h3>
            <p>{{ aiInterviewModal.companyName }}</p>
          </div>
          <div class="ai-interview-timer">
            <a-icon type="clock-circle" />
            <span>{{ formatTime(aiInterviewModal.timeRemaining) }}</span>
          </div>
        </div>

        <div class="ai-interview-content">
          <div class="ai-interview-video">
            <div class="ai-interview-avatar">
              <a-avatar size="large" icon="robot" style="background-color: #1890ff; font-size: 24px;" />
              <span class="ai-interview-status" :class="{ active: aiInterviewModal.isActive }"></span>
            </div>
            <div class="ai-interview-question">
              <p v-if="aiInterviewModal.currentQuestion">{{ aiInterviewModal.currentQuestion }}</p>
              <p v-else>欢迎参加AI面试！请准备回答以下问题...</p>
            </div>
          </div>

          <div class="ai-interview-user-video">
            <div class="user-video-placeholder" v-if="!aiInterviewModal.videoStream">
              <a-icon type="user" style="fontSize: 48px; color: #aaa;" />
              <p>摄像头画面</p>
            </div>
            <video
              v-else
              ref="userVideoRef"
              autoplay
              muted
              style="width: 100%; height: 150px; object-fit: cover;"
            ></video>
          </div>
        </div>

        <div class="ai-interview-controls">
          <a-button
            v-if="!aiInterviewModal.isActive"
            type="primary"
            @click="startInterviewSession"
            class="interview-btn"
          >
            <a-icon type="play-circle" /> 开始面试
          </a-button>
          <a-button
            v-else
            type="danger"
            @click="endAIVideoInterview"
            class="interview-btn"
          >
            <a-icon type="stop" /> 结束面试
          </a-button>
          <a-button
            @click="skipToNextQuestion"
            class="interview-btn"
            :disabled="!aiInterviewModal.isActive"
          >
            <a-icon type="step-forward" /> 下一题
          </a-button>
        </div>

        <div class="ai-interview-questions">
          <h4>面试问题列表</h4>
          <ul>
            <li
              v-for="(question, index) in aiInterviewModal.questions"
              :key="index"
              :class="{ current: index === aiInterviewModal.currentIndex }"
              @click="selectQuestion(index)"
            >
              <a-icon :type="index < aiInterviewModal.currentIndex ? 'check-circle' : 'question-circle'"
                      :style="{ color: index < aiInterviewModal.currentIndex ? '#52c41a' : '#faad14' }" />
              {{ question }}
            </li>
          </ul>
        </div>
        <div class="ai-interview-user-video">
<!--          <div class="user-video-placeholder" v-if="!aiInterviewModal.videoStream">-->
<!--            <a-icon type="user" style="fontSize: 48px; color: #aaa;" />-->
<!--            <p>摄像头画面</p>-->
<!--          </div>-->
<!--          <video-->
<!--            v-else-->
<!--            ref="userVideoRef"-->
<!--            autoplay-->
<!--            muted-->
<!--            style="width: 100%; height: 200px; object-fit: cover;"-->
<!--          ></video>-->

          <!-- 文字回答输入区域 -->
          <div class="answer-input-section">
            <a-textarea
              v-model="aiInterviewModal.currentAnswerText"
              placeholder="请输入您的回答..."
              :rows="3"
              @change="onAnswerTextChanged"
            />
          </div>
        </div>
      </div>
    </a-modal>
  </a-row>
</template>

<script>import RentView from '../../manage/post/PostView.vue'
import { mapState } from 'vuex'

export default {
  name: 'Post',
  components: { RentView },
  data () {
    return {
      pagination: {
        current: 1,       // 当前页码
        pageSize: 9,     // 每页显示条数
        total: 0          // 总记录数
      },
      form: this.$form.createForm(this),
      rentList: [],
      collectList: [],
      rentView: {
        visiable: false,
        data: null
      },
      queryParams: {},
      // AI面试相关数据
      aiInterviewModal: {
        visible: false,
        jobTitle: '',
        companyName: '',
        timeRemaining: 1800, // 30分钟倒计时
        isActive: false,
        currentQuestion: '',
        currentIndex: 0,
        questions: [
          '请简单介绍一下你自己',
          '为什么想要申请这个职位？',
          '你认为自己最大的优势是什么？',
          '描述一次你解决困难问题的经历',
          '你对未来的职业规划是什么？',
          '你有什么想问我们的问题吗？'
        ],
        timerInterval: null,
        userAnswers: [], // 存储用户的回答
        currentAnswerText: '', // 当前输入的文字回答
        videoStream: null // 存储视频流
      }
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  mounted () {
    this.selectRentList()
    this.queryCollect()
  },
  methods: {
    // 启动摄像头
    async startCamera() {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          video: true
        })
        this.aiInterviewModal.videoStream = stream

        // 直接设置srcObject，不需要等待$nextTick
        this.$nextTick(() => {
          if (this.$refs.userVideoRef) {
            this.$refs.userVideoRef.srcObject = stream
            // 确保视频开始播放
            this.$refs.userVideoRef.play().catch(error => {
              console.warn('视频播放失败:', error)
            })
          }
        })
      } catch (err) {
        console.error('无法访问摄像头:', err)
        this.$message.warning('无法访问摄像头，请检查权限设置')
      }
    },

    // 初始化媒体录制器
    initMediaRecorder (stream) {
      this.aiInterviewModal.mediaRecorder = new MediaRecorder(stream)
      this.aiInterviewModal.recordedChunks = []

      this.aiInterviewModal.mediaRecorder.ondataavailable = event => {
        if (event.data.size > 0) {
          this.aiInterviewModal.recordedChunks.push(event.data)
        }
      }

      this.aiInterviewModal.mediaRecorder.onstop = () => {
        // 创建Blob并保存录制内容
        const blob = new Blob(this.aiInterviewModal.recordedChunks, {
          type: 'video/webm'
        })
        this.saveCurrentAnswer(blob)
      }
    },

    // 开始录音
    startRecording () {
      if (this.aiInterviewModal.mediaRecorder &&
        this.aiInterviewModal.mediaRecorder.state === 'inactive') {
        this.aiInterviewModal.recordedChunks = []
        this.aiInterviewModal.mediaRecorder.start()
        this.aiInterviewModal.recordingStatus = 'recording'
      }
    },

    // 暂停录音
    pauseRecording () {
      if (this.aiInterviewModal.mediaRecorder &&
        this.aiInterviewModal.mediaRecorder.state === 'recording') {
        this.aiInterviewModal.mediaRecorder.stop()
        this.aiInterviewModal.recordingStatus = 'paused'
      }
    },
    // 页码切换
    handlePageChange(page, pageSize) {
      this.pagination.current = page;
      this.pagination.pageSize = pageSize;
      this.selectRentList(); // 重新加载数据
    },

    // 每页条数变更
    handlePageSizeChange(current, size) {
      this.pagination.current = 1; // 重置到第一页
      this.pagination.pageSize = size;
      this.selectRentList(); // 重新加载数据
    },
    // 继续录音
    resumeRecording () {
      if (this.aiInterviewModal.mediaRecorder &&
        this.aiInterviewModal.mediaRecorder.state === 'paused') {
        this.aiInterviewModal.mediaRecorder.resume()
        this.aiInterviewModal.recordingStatus = 'recording'
      }
    },

    // 文本回答变化处理
    onAnswerTextChanged () {
      // 实时更新当前回答文本
      // 可以在这里添加自动保存逻辑
    },

    // 保存当前问题的回答
    saveCurrentAnswer (recordedBlob = null) {
      const answerData = {
        questionIndex: this.aiInterviewModal.currentIndex,
        questionText: this.aiInterviewModal.questions[this.aiInterviewModal.currentIndex],
        answerText: this.aiInterviewModal.currentAnswerText,
        answerTime: new Date(),
        recordedBlob: recordedBlob ? URL.createObjectURL(recordedBlob) : null,
        questionScore: null // 可用于后续AI评分
      }

      // 更新用户回答数组
      this.aiInterviewModal.userAnswers[this.aiInterviewModal.currentIndex] = answerData

      // 如果有后端API，可以发送到服务器保存
      this.submitAnswerToServer(answerData)

      // 清空当前回答文本
      this.aiInterviewModal.currentAnswerText = ''
    },

    // 将回答提交到服务器
    async submitAnswerToServer (answerData) {
      try {
        await this.$post('/cos/ai-interview-answer/save', {
          userId: this.currentUser.userId,
          jobId: this.aiInterviewModal.jobId, // 需要在开始面试时保存
          questionIndex: answerData.questionIndex,
          questionText: answerData.questionText,
          answerText: answerData.answerText,
          answerTime: answerData.answerTime,
          recordedData: answerData.recordedBlob // 如果是文件，可能需要特殊处理
        })

        this.$message.success(`第${answerData.questionIndex + 1}题回答已保存`)
      } catch (error) {
        console.error('保存回答失败:', error)
        this.$message.error('回答保存失败，请重试')
      }
    },

    // 重写跳转到下一题的方法
    skipToNextQuestion () {
      // 保存当前回答到数组
      this.aiInterviewModal.userAnswers[this.aiInterviewModal.currentIndex] = {
        questionIndex: this.aiInterviewModal.currentIndex,
        questionText: this.aiInterviewModal.questions[this.aiInterviewModal.currentIndex],
        answerText: this.aiInterviewModal.currentAnswerText,
        answerTime: new Date()
      }

      if (this.aiInterviewModal.currentIndex < this.aiInterviewModal.questions.length - 1) {
        this.aiInterviewModal.currentIndex++
        this.aiInterviewModal.currentQuestion = this.aiInterviewModal.questions[this.aiInterviewModal.currentIndex]
        // 清空当前回答文本
        this.aiInterviewModal.currentAnswerText = ''
      } else {
        // 所有问题完成，提交所有回答
        this.submitAllAnswers()
        this.$message.info('所有问题已完成！正在保存回答...')
        this.endAIVideoInterview()
      }
    },
    async submitAllAnswers () {
      try {
        // 准备数据
        const interviewData = {
          userId: this.currentUser.userId,
          postId: this.aiInterviewModal.jobId, // 需要在开始面试时获取
          jobTitle: this.aiInterviewModal.jobTitle,
          answers: JSON.stringify(this.aiInterviewModal.userAnswers.filter(Boolean)), // 过滤掉可能的 undefined
          totalQuestions: this.aiInterviewModal.questions.length,
          completionTime: new Date()
        }
        console.log(JSON.stringify(interviewData))
        // 提交到后端
        await this.$post('/cos/ai-interview', interviewData)
        this.$message.success('面试回答已成功保存')
      } catch (error) {
        console.error('保存面试回答失败:', error)
        this.$message.error('面试回答保存失败，请重试')
      }
    },

    // 结束AI面试时保存所有回答
    endAIVideoInterview() {
      // 如果用户提前结束面试且有未保存的回答，也保存
      if (this.aiInterviewModal.currentAnswerText) {
        this.aiInterviewModal.userAnswers[this.aiInterviewModal.currentIndex] = {
          questionIndex: this.aiInterviewModal.currentIndex,
          questionText: this.aiInterviewModal.questions[this.aiInterviewModal.currentIndex],
          answerText: this.aiInterviewModal.currentAnswerText,
          answerTime: new Date()
        }
      }

      // 停止摄像头
      if (this.aiInterviewModal.videoStream) {
        const tracks = this.aiInterviewModal.videoStream.getTracks()
        tracks.forEach(track => {
          track.stop()
        })
        this.aiInterviewModal.videoStream = null
      }

      // 关闭模态框
      this.aiInterviewModal.visible = false
      this.aiInterviewModal.isActive = false

      // 清除定时器
      if (this.aiInterviewModal.timerInterval) {
        clearInterval(this.aiInterviewModal.timerInterval)
        this.aiInterviewModal.timerInterval = null
      }

      // 重置面试状态
      this.aiInterviewModal.currentIndex = 0
      this.aiInterviewModal.currentQuestion = ''
      this.aiInterviewModal.userAnswers = []
      this.aiInterviewModal.currentAnswerText = ''

      // 如果不是通过 skipToNextQuestion 完成的，也要提交回答
      if (this.aiInterviewModal.userAnswers.some(Boolean)) {
        this.submitAllAnswers()
      }
    },

    // 在开始面试会话时启动摄像头
    startInterviewSession () {
      this.aiInterviewModal.isActive = true
      this.startTimer()
      this.startCamera() // 启动摄像头

      // 不再调用录音相关的函数，因为只需要纯文本输入
    },
    // 解析福利字符串为标签数组
    parseWelfare (welfareStr) {
      if (!welfareStr) return []
      return welfareStr.split(/[,\s，、]/).filter(tag => tag.trim())
    },

    // 检查是否已收藏
    checkCollect (furnitureId) {
      if (this.collectList.length === 0) {
        return false
      }
      return this.collectList.some(item => item == furnitureId)
    },

    // 切换收藏状态
    toggleCollect (item) {
      if (this.checkCollect(item.id)) {
        this.sendNotCollect(item)
      } else {
        this.sendCollect(item)
      }
    },

    // 查询收藏列表
    queryCollect () {
      this.$get(`/cos/collect-info/queryCollectByUser/${this.currentUser.userId}`).then((r) => {
        this.collectList = r.data.data
      })
    },

    // 聊天功能
    chat (item) {
      this.$post(`/cos/chat-info/saveFirst`, {
        expertId: this.currentUser.userId,
        enterpriseCode: item.enterpriseCode,
        type: 1,
        content: '你好'
      }).then((r) => {
        this.$router.push('/expert/chat')
      })
    },

    // 搜索功能
    search() {
      this.pagination.current = 1; // 重置到第一页
      this.selectRentList({
        ...this.queryParams
      });
    },

    // 重置搜索条件
    resetSearch() {
      this.queryParams = {};
      this.pagination.current = 1; // 重置到第一页
      this.selectRentList();
    },

    // 查看岗位详情
    rentDetail (row) {
      this.rentView.visiable = true
      this.rentView.data = row
    },

    // 取消收藏
    sendNotCollect (row) {
      this.$put('/cos/collect-info/sendNotCollect', {
        expertId: this.currentUser.userId,
        baseId: row.id
      }).then((r) => {
        this.$message.success('取消收藏成功')
        this.queryCollect()
      })
    },

    // 添加收藏
    sendCollect (row) {
      this.$post('/cos/collect-info', {
        expertId: this.currentUser.userId,
        baseId: row.id,
        type: 3,
        enterpriseId: row.enterpriseId
      }).then((r) => {
        this.$message.success('收藏成功')
        this.queryCollect()
      })
    },

    // 投递简历
    sendInter (row) {
      this.$post('/cos/interview-info', {
        expertId: this.currentUser.userId,
        baseId: row.id,
        type: 2,
        enterpriseId: row.enterpriseId
      }).then((r) => {
        this.$message.success('投递成功')
      })
    },

    // 获取岗位列表
    selectRentList(params = {}) {
      // 合并分页参数
      const pageParams = {
        delFlag: 1,
        size: this.pagination.pageSize,   // 每页大小
        current: this.pagination.current, // 当前页码
        userId: this.currentUser.userId
      };

      // 发送请求
      this.$get('/cos/post-info/selectPostRecommend', { ...pageParams, ...params }).then((r) => {
        this.rentList = r.data.data.records;         // 岗位列表数据
        this.pagination.total = r.data.data.total;   // 更新总记录数
      });
    },

    // 开始AI视频面试
    startAIVideoInterview (item) {
      // 设置面试信息
      this.aiInterviewModal.jobTitle = item.postName
      this.aiInterviewModal.companyName = item.enterpriseName
      this.aiInterviewModal.jobId = item.id // 保存岗位ID
      this.aiInterviewModal.currentIndex = 0
      this.aiInterviewModal.currentQuestion = this.aiInterviewModal.questions[0]
      this.aiInterviewModal.timeRemaining = 1800 // 30分钟

      // 显示模态框
      this.aiInterviewModal.visible = true
    },

    // 开始倒计时
    startTimer () {
      if (this.aiInterviewModal.timerInterval) {
        clearInterval(this.aiInterviewModal.timerInterval)
      }

      this.aiInterviewModal.timerInterval = setInterval(() => {
        if (this.aiInterviewModal.timeRemaining > 0) {
          this.aiInterviewModal.timeRemaining--
        } else {
          this.endAIVideoInterview()
        }
      }, 1000)
    },

    // 格式化时间显示
    formatTime (seconds) {
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },

    // 选择特定问题
    selectQuestion (index) {
      if (this.aiInterviewModal.isActive) {
        this.aiInterviewModal.currentIndex = index
        this.aiInterviewModal.currentQuestion = this.aiInterviewModal.questions[index]
      }
    }
  }
}
</script>

<style scoped>/* 主容器 */
.post-list-container {
  display: flex;
  flex-wrap: wrap;
}

/* 卡片容器 */
.post-item-wrapper {
  padding: 10px;
  margin-bottom: 15px;
}

.post-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.15) !important;
}

/* 标题区域 */
.post-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.post-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  flex: 1;
}

.post-location {
  font-size: 12px;
  color: #888;
  white-space: nowrap;
  margin-left: 10px;
}

/* 描述区域 */
.post-description {
  margin-bottom: 12px;
  color: #666;
  line-height: 1.5;
}

.post-description p {
  margin: 0;
  font-size: 13px;
}

/* 福利标签 */
.welfare-tags {
  margin-bottom: 15px;
}

.welfare-tags .ant-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

/* 公司信息 */
.company-info {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.company-logo {
  margin-right: 10px;
}

.company-details {
  flex: 1;
}

.company-name {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.industry-hour {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #888;
}

.divider {
  margin: 0 8px;
}

/* 薪资部分 */
.salary-section {
  text-align: right;
}

.salary {
  font-size: 18px;
  font-weight: bold;
  color: #f5222d;
}

/* 操作按钮 */
.ant-card-actions {
  border-top: 1px solid #f0f0f0;
}

.action-item {
  text-align: center;
  cursor: pointer;
  transition: color 0.3s;
  padding: 8px 0;
  color: #666;
}

.action-item:hover {
  color: #1890ff;
}

.action-item.collected {
  color: #f5222d;
}

.action-item.ai-interview-btn:hover {
  color: #177ddc;
}

/* 无数据提示 */
.no-data-container {
  background: #fff;
  padding: 40px 20px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-title {
    flex-direction: column;
    align-items: flex-start;
  }

  .post-location {
    margin-left: 0;
    margin-top: 5px;
  }

  .company-info {
    flex-direction: column;
    align-items: flex-start;
  }

  .company-details {
    margin-top: 10px;
  }
}

/* AI面试弹窗样式 */
.ai-interview-container {
  height: 800px;
  display: flex;
  flex-direction: column;
}

.ai-interview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  background-color: #fafafa;
}

.ai-interview-info h3 {
  margin: 0;
  color: #333;
}

.ai-interview-info p {
  margin: 4px 0 0 0;
  color: #888;
  font-size: 14px;
}

.ai-interview-timer {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #f5222d;
}

.ai-interview-content {
  flex: 1;
  display: flex;
  padding: 24px;
  gap: 24px;
  overflow-y: auto;
}

.ai-interview-video {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
}

.ai-interview-avatar {
  position: relative;
  margin-bottom: 20px;
}

.ai-interview-status {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #ccc;
  border: 2px solid white;
}

.ai-interview-status.active {
  background-color: #52c41a;
}

.ai-interview-question {
  font-size: 16px;
  color: #333;
  line-height: 1.6;
}

.ai-interview-user-video {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
  border-radius: 8px;
  padding: 20px;
}

.user-video-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #888;
}

.ai-interview-controls {
  display: flex;
  justify-content: center;
  padding: 16px;
  border-top: 1px solid #e8e8e8;
  background-color: #fff;
  gap: 12px;
}

.interview-btn {
  min-width: 120px;
}

.ai-interview-questions {
  padding: 16px;
  border-top: 1px solid #e8e8e8;
  background-color: #fafafa;
  max-height: 250px;
  overflow-y: auto;
}

.ai-interview-questions h4 {
  margin-top: 0;
  margin-bottom: 12px;
  color: #333;
}

.ai-interview-questions ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.ai-interview-questions li {
  padding: 8px 12px;
  margin-bottom: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.ai-interview-questions li:hover {
  background-color: #e6f7ff;
}

.ai-interview-questions li.current {
  background-color: #bae7ff;
  border-left: 4px solid #1890ff;
}

.ai-interview-questions li.current::before {
  content: "当前";
  font-size: 12px;
  background-color: #1890ff;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  margin-right: 8px;
}

.answer-input-section {
  margin-top: 15px;
  width: 100%;
}

.answer-input-section .ant-input {
  margin-bottom: 10px;
}

.recording-controls {
  display: flex;
  justify-content: center;
  gap: 10px;
}

</style>
