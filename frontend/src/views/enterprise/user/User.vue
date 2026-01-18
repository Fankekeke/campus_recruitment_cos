
<template>
  <div>
    <a-row :gutter="[24, 24]" class="user-container">
      <!-- 左侧信息展示区域 -->
      <a-col :xs="24" :sm="24" :md="8" class="info-column">
        <div class="info-card">
          <div class="info-header">
            <h2><a-icon type="bank" /> 企业信息</h2>
          </div>
          <div class="info-content">
            <div class="info-image">
              <img alt="企业形象图" src="/static/img/data.png" />
            </div>
            <div class="info-summary">
              <p><strong>企业名称：</strong>{{ enterpriseInfo.name ? enterpriseInfo.name : '未设置' }}</p>
              <p><strong>所属行业：</strong>{{ enterpriseInfo.industry ? enterpriseInfo.industry : '未设置' }}</p>
              <p><strong>注册地址：</strong>{{ enterpriseInfo.registeredAddress ? enterpriseInfo.registeredAddress : '未设置' }}</p>
              <p><strong>经营状态：</strong>{{ enterpriseInfo.status ? enterpriseInfo.status : '未设置' }}</p>
            </div>
          </div>
        </div>
      </a-col>

      <!-- 右侧表单区域 -->
      <a-col :xs="24" :sm="24" :md="16" class="form-column">
        <a-card class="form-card">
          <div class="form-header">
            <h2><a-icon type="edit" /> 编辑企业信息</h2>
            <p class="form-subtitle">请填写完整的企业信息以便于招聘流程顺利进行</p>
          </div>

          <a-form :form="form" layout="vertical" class="info-form">
            <a-row :gutter="[16, 16]">
              <!-- 第一行 -->
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='企业名称' v-bind="formItemLayout">
                  <a-input
                    v-decorator="[
                    'name',
                    { rules: [{ required: true, message: '请输入企业名称!' }] }
                  ]"
                    placeholder="请输入企业名称"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='单位简称或代号' v-bind="formItemLayout">
                  <a-input
                    v-decorator="[
                    'abbreviation',
                    { rules: [{ required: true, message: '请输入单位简称!' }] }
                  ]"
                    placeholder="请输入单位简称"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='统一社会信用代码' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['creditCode']"
                    placeholder="请输入统一社会信用代码"
                  />
                </a-form-item>
              </a-col>

              <!-- 第二行 -->
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='单位性质' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['nature']"
                    placeholder="请输入单位性质"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='二级企业单位性质' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['natureTwo']"
                    placeholder="请输入二级企业单位性质"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='经营状态' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['status']"
                    placeholder="请输入经营状态"
                  />
                </a-form-item>
              </a-col>

              <!-- 第三行 -->
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='法定代表人' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['corporateRepresentative']"
                    placeholder="请输入法定代表人"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='法定代表人身份证号' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['corporateRepresentativeId']"
                    placeholder="请输入法定代表人身份证号"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='法定代表人电话' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['corporateRepresentativePhone']"
                    placeholder="请输入法定代表人电话"
                  />
                </a-form-item>
              </a-col>

              <!-- 第四行 -->
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='注册资本（万元）' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['registeredCapital']"
                    placeholder="请输入注册资本"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='注册资金币种' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['registeredCapitalCurrency']"
                    placeholder="请输入注册资金币种"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='成立日期' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['establishmentDate']"
                    placeholder="请输入成立日期"
                  />
                </a-form-item>
              </a-col>

              <!-- 第五行 -->
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='营业期限始期' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['businessBeginDate']"
                    placeholder="请输入营业期限始期"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='营业期限止期' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['businessEndDate']"
                    placeholder="请输入营业期限止期"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='注册地址' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['registeredAddress']"
                    placeholder="请输入注册地址"
                  />
                </a-form-item>
              </a-col>

              <!-- 第六行 -->
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='经营范围' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['businessScope']"
                    placeholder="请输入经营范围"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='省' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['province']"
                    placeholder="请输入省"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='市' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['city']"
                    placeholder="请输入市"
                  />
                </a-form-item>
              </a-col>

              <!-- 第七行 -->
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='区' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['district']"
                    placeholder="请输入区"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='英文企业名称' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['enName']"
                    placeholder="请输入英文企业名称"
                  />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="12" :md="8">
                <a-form-item label='所属行业' v-bind="formItemLayout">
                  <a-input
                    v-decorator="['industry']"
                    placeholder="请输入所属行业"
                  />
                </a-form-item>
              </a-col>

              <!-- 单位简介 -->
              <a-col :xs="24">
                <a-form-item label='单位简介' v-bind="formItemLayout">
                  <a-textarea
                    :rows="6"
                    v-decorator="['unitDescription']"
                    placeholder="请输入单位简介"
                  />
                </a-form-item>
              </a-col>

              <!-- 企业照片 -->
              <a-col :xs="24">
                <a-form-item label='企业照片' v-bind="formItemLayout">
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
                    <img alt="企业照片预览" style="width: 100%" :src="previewImage" />
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
              class="submit-btn"
            >
              <a-icon type="save" /> 保存修改
            </a-button>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>import {mapState} from 'vuex'

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
  data () {
    return {
      form: this.$form.createForm(this),
      formItemLayout,
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      enterpriseInfo: null
    }
  },
  mounted () {
    this.getEnterpriseInfo(this.currentUser.userId)
  },
  methods: {
    getEnterpriseInfo (expertId) {
      this.$get(`/cos/enterprise-info/detail/id/${expertId}`).then((r) => {
        this.enterpriseInfo = r.data.data
        console.log(this.enterpriseInfo)
        this.setFormValues(this.enterpriseInfo)
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
    setFormValues ({...enterprise}) {
      this.rowId = enterprise.id
      let fields = ['name', 'abbreviation', 'creditCode', 'code', 'nature', 'natureTwo', 'status', 'corporateRepresentative', 'corporateRepresentativeId', 'corporateRepresentativePhone', 'registeredCapital', 'registeredCapitalCurrency', 'establishmentDate', 'businessBeginDate', 'businessEndDate', 'registeredAddress', 'businessScope', 'source', 'province', 'city', 'district', 'enName', 'industry', 'unitDescription', 'images']
      let obj = {}
      Object.keys(enterprise).forEach((key) => {
        if (key === 'images') {
          this.fileList = []
          this.imagesInit(enterprise['images'])
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = enterprise[key]
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
          this.$put('/cos/enterprise-info', {
            ...values
          }).then((r) => {
            this.$message.success('更新成功')
            this.loading = false
            this.getEnterpriseInfo(this.currentUser.userId)
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
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.info-column, .form-column {
  padding: 0 12px;
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.info-header {
  text-align: center;
  margin-bottom: 20px;
}

.info-header h2 {
  color: #1890ff;
  font-weight: 600;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.info-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.info-image {
  text-align: center;
  margin-bottom: 20px;
}

.info-image img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.info-summary p {
  margin: 8px 0;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}

.info-summary strong {
  color: #333;
}

.form-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  height: 100%;
}

.form-header {
  margin-bottom: 24px;
  text-align: center;
}

.form-header h2 {
  color: #1890ff;
  font-weight: 600;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.form-subtitle {
  color: #888;
  margin: 0;
}

.info-form {
  margin-bottom: 24px;
}

.form-actions {
  text-align: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.submit-btn {
  height: 44px;
  font-size: 16px;
  font-weight: 600;
  padding: 0 30px;
}

.avatar-uploader >>> .ant-upload {
  width: 100%;
  max-width: 150px;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
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

  .info-column, .form-column {
    padding: 0 6px;
  }

  .info-card, .form-card {
    padding: 16px;
  }

  .info-image img {
    height: 300px;
    object-fit: cover;
  }

  .info-summary p {
    font-size: 13px;
  }

  .form-header h2 {
    font-size: 18px;
  }
}
</style>
