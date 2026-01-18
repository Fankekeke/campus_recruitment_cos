
<template>
  <a-row :gutter="[24, 24]" class="user-container">
    <!-- 左侧用户信息表单 -->
    <a-col :xs="24" :sm="24" :md="8" class="user-form-column">
      <div class="user-info-header">
        <h2><a-icon type="user" /> 学生信息</h2>
      </div>
      <a-card
        :loading="loading"
        :bordered="false"
        class="user-form-card"
      >
        <a-form :form="form" layout="vertical">
          <a-row :gutter="[16, 16]">
            <!-- 姓名和民族 -->
            <a-col :span="12">
              <a-form-item label='学生姓名' v-bind="formItemLayout">
                <a-input
                  v-decorator="[
                    'name',
                    { rules: [{ required: true, message: '请输入学生姓名!' }] }
                  ]"
                  placeholder="请输入学生姓名"
                />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label='民族' v-bind="formItemLayout">
                <a-input
                  v-decorator="['nationality']"
                  placeholder="请输入民族"
                />
              </a-form-item>
            </a-col>

            <!-- 性别、政治面貌、籍贯 -->
            <a-col :span="8">
              <a-form-item label='性别' v-bind="formItemLayout">
                <a-input
                  v-decorator="['sex']"
                  placeholder="请输入性别"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label='政治面貌' v-bind="formItemLayout">
                <a-input
                  v-decorator="['politicalStatus']"
                  placeholder="请输入政治面貌"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label='籍贯' v-bind="formItemLayout">
                <a-input
                  v-decorator="['nativePlace']"
                  placeholder="请输入籍贯"
                />
              </a-form-item>
            </a-col>

            <!-- 工作单位、职务、通讯地址 -->
            <a-col :span="8">
              <a-form-item label='工作单位' v-bind="formItemLayout">
                <a-input
                  v-decorator="['employer']"
                  placeholder="请输入工作单位"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label='职务' v-bind="formItemLayout">
                <a-input
                  v-decorator="['position']"
                  placeholder="请输入职务"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label='通讯地址' v-bind="formItemLayout">
                <a-input
                  v-decorator="['address']"
                  placeholder="请输入通讯地址"
                />
              </a-form-item>
            </a-col>

            <!-- 手机号、专业方向 -->
            <a-col :span="8">
              <a-form-item label='手机号' v-bind="formItemLayout">
                <a-input
                  v-decorator="['phone']"
                  placeholder="请输入手机号"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label='专业方向一级' v-bind="formItemLayout">
                <a-input
                  v-decorator="['levelOne']"
                  placeholder="请输入专业方向一级"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label='专业方向二级' v-bind="formItemLayout">
                <a-input
                  v-decorator="['levelTwo']"
                  placeholder="请输入专业方向二级"
                />
              </a-form-item>
            </a-col>

            <!-- 出生日期、职称、特殊称谓 -->
            <a-col :span="8">
              <a-form-item label='出生日期' v-bind="formItemLayout">
                <a-input
                  v-decorator="['birthDate']"
                  placeholder="请输入出生日期"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label='职称' v-bind="formItemLayout">
                <a-input
                  v-decorator="['jobTitle']"
                  placeholder="请输入职称"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label='特殊称谓' v-bind="formItemLayout">
                <a-input
                  v-decorator="['specialAppellation']"
                  placeholder="请输入特殊称谓"
                />
              </a-form-item>
            </a-col>

            <!-- 邮箱、固定电话 -->
            <a-col :span="8">
              <a-form-item label='邮箱' v-bind="formItemLayout">
                <a-input
                  v-decorator="['mail']"
                  placeholder="请输入邮箱"
                />
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label='固定电话' v-bind="formItemLayout">
                <a-input
                  v-decorator="['fixedTelephone']"
                  placeholder="请输入固定电话"
                />
              </a-form-item>
            </a-col>

            <!-- 个人简介 -->
            <a-col :span="24">
              <a-form-item label='个人简介' v-bind="formItemLayout">
                <a-textarea
                  :rows="6"
                  v-decorator="[
                    'profile',
                     { rules: [{ required: true, message: '请输入个人简介!' }] }
                  ]"
                  placeholder="请输入个人简介"
                />
              </a-form-item>
            </a-col>

            <!-- 头像上传 -->
            <a-col :span="24">
              <a-form-item label='学生头像' v-bind="formItemLayout">
                <a-upload
                  name="avatar"
                  action="http://127.0.0.1:9527/file/fileUpload/"
                  list-type="picture-card"
                  :file-list="fileList"
                  @preview="handlePreview"
                  @change="picHandleChange"
                  class="avatar-uploader"
                >
                  <div v-if="fileList.length < 1" class="upload-placeholder">
                    <a-icon type="plus" />
                    <div class="ant-upload-text">点击上传</div>
                  </div>
                </a-upload>
                <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
                  <img alt="头像预览" style="width: 100%" :src="previewImage" />
                </a-modal>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>

        <div class="form-actions">
          <a-button
            key="submit"
            type="primary"
            :loading="loading"
            @click="handleSubmit"
            block
            class="submit-btn"
          >
            <a-icon type="save" /> 修改信息
          </a-button>
        </div>
      </a-card>
    </a-col>

    <!-- 右侧岗位推荐 -->
    <a-col :xs="24" :sm="24" :md="16" class="recommendation-column">
      <div class="recommendation-header">
        <h2><a-icon type="rocket" /> 推荐岗位</h2>
      </div>
      <a-card class="recommendation-card" :bordered="false">
        <div v-if="postList.length === 0" class="empty-state">
          <a-empty description="暂无推荐岗位信息" />
        </div>

        <div>
          <a-col :span="12"
                 v-for="(item, index) in postList"
                 :key="index"
                 class="post-item-wrapper"
          >
            <a-card
              :bordered="false"
              hoverable
              class="post-card"
            >
              <a-card-meta>
                <template slot="title">
                  <div class="post-title">
                    <span class="post-name">{{ item.postName }}</span>
                    <span class="post-location">{{ item.address }}</span>
                  </div>
                </template>
                <template slot="description">
                  <div class="post-description">
                    {{ item.responsibility ? (item.responsibility.slice(0, 50) + (item.responsibility.length > 50 ? '...' : '')) : '暂无描述' }}
                  </div>
                </template>
              </a-card-meta>

              <div class="post-tags">
                <a-tag v-for="tag in parseWelfare(item.welfare)" :key="tag" color="blue" class="welfare-tag">
                  {{ tag }}
                </a-tag>
              </div>

              <div class="post-company-info">
                <a-avatar
                  size="small"
                  shape="square"
                  icon="building"
                  :src="'http://127.0.0.1:9527/imagesWeb/' + item.enterpriseImages"
                />
                <div class="company-details">
                  <div class="company-name">{{ item.enterpriseName }}</div>
                  <div class="company-meta">
                    <span>{{ item.industryName || '未知行业' }}</span>
                    <span class="separator">|</span>
                    <span>{{ item.workHour || '未知工时' }}</span>
                  </div>
                </div>
              </div>

              <div class="post-footer">
                <div class="post-salary">{{ item.salary || '面议' }}</div>
                <div class="post-actions">
                  <a-button size="small" @click="chat(item)" class="action-btn">
                    <a-icon type="message" /> 沟通
                  </a-button>
                  <a-button size="small" @click="sendInter(item)" class="action-btn">
                    <a-icon type="folder-open" /> 投递
                  </a-button>
                  <a-button size="small" @click="rentDetail(item)" class="action-btn">
                    <a-icon type="eye" /> 详情
                  </a-button>
                  <a-button
                    size="small"
                    @click="toggleCollect(item)"
                    :type="checkCollect(item.id) ? 'danger' : 'default'"
                    class="action-btn"
                  >
                    <a-icon :type="checkCollect(item.id) ? 'heart' : 'heart'" :theme="checkCollect(item.id) ? 'filled' : 'outlined'" />
                    {{ checkCollect(item.id) ? '已收藏' : '收藏' }}
                  </a-button>
                </div>
              </div>
            </a-card>
          </a-col>
        </div>
      </a-card>
    </a-col>

    <rent-view
      :pluralismShow="rentView.visiable"
      :pluralismData="rentView.data"
      @close="rentView.visiable = false"
    ></rent-view>
  </a-row>
</template>

<script>import {mapState} from 'vuex'
import RentView from '../../manage/post/PostView.vue'

function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}

export default {
  name: 'User',
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  components: {RentView},
  data () {
    return {
      form: this.$form.createForm(this),
      formItemLayout,
      loading: false,
      fileList: [],
      postList: [],
      collectList: [],
      rentView: {
        visiable: false,
        data: null
      },
      previewVisible: false,
      previewImage: '',
      expertInfo: null
    }
  },
  mounted () {
    this.getExpertInfo(this.currentUser.userId)
    this.queryPostRecommend()
    this.queryCollect()
  },
  methods: {
    // 解析福利标签
    parseWelfare(welfareStr) {
      if (!welfareStr) return []
      return welfareStr.split(/[,\s，、]/).filter(tag => tag.trim()).slice(0, 3)
    },

    // 切换收藏状态
    toggleCollect(item) {
      if (this.checkCollect(item.id)) {
        this.sendNotCollect(item)
      } else {
        this.sendCollect(item)
      }
    },

    checkCollect (furnitureId) {
      if (this.collectList.length === 0) {
        return false
      }
      return this.collectList.some(item => item == furnitureId)
    },
    rentDetail (row) {
      this.rentView.visiable = true
      this.rentView.data = row
    },
    sendNotCollect (row) {
      this.$put('/cos/collect-info/sendNotCollect', {
        expertId: this.currentUser.userId,
        baseId: row.id
      }).then((r) => {
        this.$message.success('取消收藏成功')
        this.queryCollect()
      })
    },
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
    queryCollect () {
      this.$get(`/cos/collect-info/queryCollectByUser/${this.currentUser.userId}`).then((r) => {
        this.collectList = r.data.data
      })
    },
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
    queryPostRecommend () {
      this.$get(`/cos/post-info/queryPostRecommend`, {userId: this.currentUser.userId}).then((r) => {
        this.postList = r.data.data
      })
    },
    getExpertInfo (expertCode) {
      this.$get(`/cos/expert-info/detail/id/${expertCode}`).then((r) => {
        this.expertInfo = r.data.data
        console.log(this.expertInfo)
        this.setFormValues(this.expertInfo)
      })
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    setFormValues ({...expert}) {
      this.rowId = expert.id
      let fields = ['name', 'nationality', 'sex', 'politicalStatus', 'nativePlace', 'employer', 'position', 'address', 'phone', 'levelOne', 'levelTwo', 'birthDate', 'jobTitle', 'specialAppellation', 'mail', 'fixedTelephone', 'profile']
      let obj = {}
      Object.keys(expert).forEach((key) => {
        if (key === 'images') {
          this.fileList = []
          this.imagesInit(expert['images'])
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = expert[key]
        }
      })
      this.form.setFieldsValue(obj)
    },
    handleSubmit () {
      // 获取图片List
      let images = []
      this.fileList.forEach(image => {
        if (image.response !== undefined) {
          images.push(image.response)
        } else {
          images.push(image.name)
        }
      })
      this.form.validateFields((err, values) => {
        values.id = this.rowId
        values.images = images.length > 0 ? images.join(',') : null
        if (!err) {
          this.loading = true
          this.$put('/cos/expert-info', {
            ...values
          }).then((r) => {
            this.$message.success('更新成功')
            this.loading = false
            this.getExpertInfo(this.currentUser.userId)
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped>.user-container {
  padding: 20px;
  /*background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);*/
  min-height: 100vh;
}

.user-info-header, .recommendation-header {
  margin-bottom: 16px;
}

.user-info-header h2, .recommendation-header h2 {
  color: #1890ff;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-form-card, .recommendation-card {
  border-radius: 3px;
  /*box-shadow: 0 4px 12px rgba(0,0,0,0.08);*/
  overflow: hidden;
  transition: all 0.3s ease;
}

.user-form-card:hover, .recommendation-card:hover {
  box-shadow: 0 6px 16px rgba(0,0,0,0.12);
}

.user-form-column, .recommendation-column {
  padding: 0 12px;
}

.form-actions {
  margin-top: 20px;
}

.submit-btn {
  height: 44px;
  font-size: 16px;
  font-weight: 600;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
}

.post-grid {
  display: grid;
  /*grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));*/
  /*gap: 20px;*/
}

.post-item-wrapper {
  padding: 0;
}

.post-card {
  border-radius: 3px;
  overflow: hidden;
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.15) !important;
}

.post-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.post-name {
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.post-location {
  color: #888;
  font-size: 13px;
}

.post-description {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
}

.post-tags {
  margin-bottom: 12px;
}

.welfare-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}

.post-company-info {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.company-details {
  margin-left: 10px;
}

.company-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.company-meta {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #888;
}

.separator {
  margin: 0 8px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 10px;
}

.post-salary {
  font-weight: bold;
  color: #f5222d;
  font-size: 16px;
}

.post-actions {
  display: flex;
  gap: 6px;
}

.action-btn {
  padding: 2px 8px;
  font-size: 12px;
}

.avatar-uploader >>> .ant-upload {
  width: 100%;
  max-width: 150px;
  height: 150px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  cursor: pointer;
}

.upload-placeholder .anticon-plus {
  font-size: 24px;
  color: #888;
}

.upload-placeholder .ant-upload-text {
  margin-top: 8px;
  color: #888;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-container {
    padding: 10px;
  }

  .post-grid {
    grid-template-columns: 1fr;
  }

  .post-title {
    flex-direction: column;
    align-items: flex-start;
  }

  .post-location {
    margin-top: 4px;
  }

  .post-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .post-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .user-form-column, .recommendation-column {
    padding: 0 6px;
  }
}
</style>
