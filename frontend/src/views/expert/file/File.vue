
<template>
  <a-row :gutter="[24, 24]" class="user-container">
    <!-- 左侧 Tabs 容器 -->
    <a-col :xs="24" :sm="24" :md="24" class="tabs-column">
      <a-tabs default-active-key="1" class="user-tabs">
        <!-- Tab 1: 用户基本信息 -->
        <a-tab-pane key="1" tab="就业去向信息">
          <div class="tab-content">
            <a-card :loading="loading" :bordered="false" class="employment-card">
              <a-form :form="employmentForm" layout="vertical">
                <!-- 就业类型 -->
                <a-form-item label="就业类型">
                  <a-select
                    v-decorator="['type', { rules: [{ required: true, message: '请选择就业类型!' }] }]"
                    placeholder="请选择就业类型"
                  >
                    <a-select-option :value="1">签协议就业</a-select-option>
                    <a-select-option :value="2">升学</a-select-option>
                    <a-select-option :value="3">出国</a-select-option>
                    <a-select-option :value="4">创业</a-select-option>
                    <a-select-option :value="5">其他</a-select-option>
                  </a-select>
                </a-form-item>

                <!-- 单位名称 -->
                <a-form-item label="单位名称">
                  <a-input
                    v-decorator="['companyName']"
                    placeholder="请输入单位名称"
                  />
                </a-form-item>

                <!-- 统一社会信用代码 -->
                <a-form-item label="统一社会信用代码">
                  <a-input
                    v-decorator="['organizationCode']"
                    placeholder="请输入统一社会信用代码"
                  />
                </a-form-item>

                <!-- 就业城市 -->
                <a-form-item label="就业城市">
                  <a-input
                    v-decorator="['city']"
                    placeholder="请输入就业城市"
                  />
                </a-form-item>

                <!-- 提交按钮 -->
                <div class="form-actions">
                  <a-button
                    key="submit"
                    type="primary"
                    :loading="loading"
                    @click="handleEmploymentSubmit"
                    block
                    class="submit-btn"
                  >
                    <a-icon type="save" /> {{ employmentInfo ? '修改信息' : '添加信息' }}
                  </a-button>
                </div>
              </a-form>
            </a-card>
          </div>
        </a-tab-pane>

        <!-- Tab 2: 就业证明材料 -->
        <a-tab-pane key="2" tab="就业证明材料">
          <div class="tab-content">
            <a-card :bordered="false" class="material-card">
              <a-form :form="materialForm" layout="vertical">
                <!-- 材料名称 -->
                <a-form-item label="材料名称">
                  <a-input
                    v-decorator="['materialType', { rules: [{ required: true, message: '请输入材料名称!' }] }]"
                    placeholder="如：劳动合同"
                  />
                </a-form-item>

                <!-- 文件上传 -->
                <a-form-item label="上传文件">
                  <a-upload
                    name="file"
                    action="http://127.0.0.1:9527/file/fileUpload/"
                    list-type="picture-card"
                    :file-list="materialFileList"
                    @preview="handleMaterialPreview"
                    @change="materialHandleChange"
                    class="material-uploader"
                  >
                    <div v-if="materialFileList.length < 1" class="upload-placeholder">
                      <a-icon type="plus" />
                      <div class="ant-upload-text">点击上传</div>
                    </div>
                  </a-upload>
                  <a-modal :visible="materialPreviewVisible" :footer="null" @cancel="handleMaterialCancel">
                    <img alt="材料预览" style="width: 100%" :src="materialPreviewImage" />
                  </a-modal>
                </a-form-item>

                <!-- 备注 -->
                <a-form-item label="备注">
                  <a-textarea
                    v-decorator="['remark']"
                    placeholder="请输入备注信息"
                    :rows="4"
                  />
                </a-form-item>

                <!-- 提交按钮 -->
                <div class="form-actions">
                  <a-button
                    key="submit"
                    type="primary"
                    :loading="materialLoading"
                    @click="handleMaterialSubmit"
                    block
                    class="submit-btn"
                  >
                    <a-icon type="save" /> {{ materialInfo ? '修改材料' : '添加材料' }}
                  </a-button>
                </div>
              </a-form>
            </a-card>
          </div>
        </a-tab-pane>

        <!-- Tab 3: 三方协议信息 -->
        <a-tab-pane key="3" tab="三方协议信息">
          <div class="tab-content">
            <a-card :bordered="false" class="agreement-card">
              <a-form :form="agreementForm" layout="vertical">
                <!-- 协议编号 -->
                <a-form-item label="协议编号">
                  <a-input
                    v-decorator="['agreementNo', { rules: [{ required: true, message: '请输入协议编号!' }] }]"
                    placeholder="请输入协议编号"
                  />
                </a-form-item>

                <!-- 签约企业 -->
                <a-form-item label="签约企业">
                  <a-input
                    v-decorator="['enterpriseName']"
                    placeholder="请输入签约企业名称"
                  />
                </a-form-item>

                <!-- 签约时间 -->
                <a-form-item label="签约时间">
                  <a-date-picker
                    v-decorator="['signDate']"
                    format="YYYY-MM-DD"
                    placeholder="请选择签约时间"
                  />
                </a-form-item>

                <!-- 协议扫描件上传 -->
                <a-form-item label="协议扫描件">
                  <a-upload
                    name="file"
                    action="http://127.0.0.1:9527/file/fileUpload/"
                    list-type="picture-card"
                    :file-list="agreementFileList"
                    @preview="handleAgreementPreview"
                    @change="agreementHandleChange"
                    class="agreement-uploader"
                  >
                    <div v-if="agreementFileList.length < 1" class="upload-placeholder">
                      <a-icon type="plus" />
                      <div class="ant-upload-text">点击上传</div>
                    </div>
                  </a-upload>
                  <a-modal :visible="agreementPreviewVisible" :footer="null" @cancel="handleAgreementCancel">
                    <img alt="协议预览" style="width: 100%" :src="agreementPreviewImage" />
                  </a-modal>
                </a-form-item>

                <!-- 提交按钮 -->
                <div class="form-actions">
                  <a-button
                    key="submit"
                    type="primary"
                    :loading="agreementLoading"
                    @click="handleAgreementSubmit"
                    block
                    class="submit-btn"
                  >
                    <a-icon type="save" /> {{ agreementInfo ? '修改协议' : '添加协议' }}
                  </a-button>
                </div>
              </a-form>
            </a-card>
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-col>
  </a-row>
</template>

<script>import { mapState } from 'vuex'
import moment from 'moment'


export default {
  name: 'User',
  data() {
    return {
      employmentForm: this.$form.createForm(this),
      loading: false,
      employmentInfo: null, // 存储当前就业去向信息

      materialForm: this.$form.createForm(this),
      materialLoading: false,
      materialInfo: null, // 存储当前就业证明材料信息
      materialFileList: [],
      materialPreviewVisible: false,
      materialPreviewImage: '',

      agreementForm: this.$form.createForm(this),
      agreementLoading: false,
      agreementInfo: null, // 存储当前三方协议信息
      agreementFileList: [],
      agreementPreviewVisible: false,
      agreementPreviewImage: '',
    }
  },
  mounted() {
    this.loadEmploymentInfo();
    this.loadMaterialInfo();
    this.loadAgreementInfo();
  },
  methods: {
    // 加载就业去向信息
    loadEmploymentInfo() {
      this.$get(`/cos/employment-destinations/queryByUser/${this.currentUser.userId}`).then((r) => {
        this.employmentInfo = r.data.data;
        if (this.employmentInfo) {
          this.setEmploymentFormValues(this.employmentInfo);
        }
      });
    },

    // 设置表单默认值
    setEmploymentFormValues(info) {
      const fields = ['type', 'companyName', 'organizationCode', 'city'];
      const obj = {};
      fields.forEach((field) => {
        this.employmentForm.getFieldDecorator(field);
        obj[field] = info[field];
      });
      this.employmentForm.setFieldsValue(obj);
    },

    // 提交就业去向信息
    handleEmploymentSubmit() {
      this.employmentForm.validateFields((err, values) => {
        if (!err) {
          this.loading = true;
          const url = this.employmentInfo ? '/cos/employment-destinations/update' : '/cos/employment-destinations/add';
          this.$post(url, {
            ...values,
            userId: this.currentUser.userId,
            id: this.employmentInfo?.id || null,
          }).then(() => {
            this.$message.success(this.employmentInfo ? '修改成功' : '添加成功');
            this.loading = false;
            this.loadEmploymentInfo(); // 重新加载数据
          }).catch(() => {
            this.loading = false;
          });
        }
      });
    },

    // 加载就业证明材料信息
    loadMaterialInfo() {
      this.$get(`/cos/employment-evidence/queryByUser/${this.currentUser.userId}`).then((r) => {
        this.materialInfo = r.data.data;
        if (this.materialInfo) {
          this.setMaterialFormValues(this.materialInfo);
          this.materialFileList = [
            {
              uid: '-1',
              name: this.materialInfo.fileName,
              status: 'done',
              url: this.materialInfo.fileUrl,
            },
          ];
        }
      });
    },

    // 设置表单默认值
    setMaterialFormValues(info) {
      const fields = ['materialType', 'remark'];
      const obj = {};
      fields.forEach((field) => {
        this.materialForm.getFieldDecorator(field);
        obj[field] = info[field];
      });
      this.materialForm.setFieldsValue(obj);
    },

    // 处理文件上传变化
    materialHandleChange({ fileList }) {
      this.materialFileList = fileList;
    },

    // 文件预览
    handleMaterialPreview(file) {
      this.materialPreviewImage = file.url || file.preview;
      this.materialPreviewVisible = true;
    },

    // 关闭预览弹窗
    handleMaterialCancel() {
      this.materialPreviewVisible = false;
    },

    // 提交就业证明材料
    handleMaterialSubmit() {
      this.materialForm.validateFields((err, values) => {
        if (!err) {
          this.materialLoading = true;
          const fileUrl = this.materialFileList[0]?.response?.data || this.materialFileList[0]?.url;
          const fileName = this.materialFileList[0]?.name;

          const url = this.materialInfo ? '/cos/employment-evidence/update' : '/cos/employment-evidence/add';
          this.$post(url, {
            ...values,
            userId: this.currentUser.userId,
            id: this.materialInfo?.id || null,
            fileUrl,
            fileName,
          }).then(() => {
            this.$message.success(this.materialInfo ? '修改成功' : '添加成功');
            this.materialLoading = false;
            this.loadMaterialInfo(); // 重新加载数据
          }).catch(() => {
            this.materialLoading = false;
          });
        }
      });
    },

    // 加载三方协议信息
    loadAgreementInfo() {
      this.$get(`/cos/tripartite-agreements/queryByUser/${this.currentUser.userId}`).then((r) => {
        this.agreementInfo = r.data.data;
        if (this.agreementInfo) {
          this.setAgreementFormValues(this.agreementInfo);
          this.agreementFileList = [
            {
              uid: '-1',
              name: this.agreementInfo.fileName,
              status: 'done',
              url: this.agreementInfo.fileUrl,
            },
          ];
        }
      });
    },

    // 设置表单默认值
    setAgreementFormValues(info) {
      const fields = ['agreementNo', 'enterpriseName', 'signDate'];
      const obj = {};
      fields.forEach((field) => {
        this.agreementForm.getFieldDecorator(field);
        obj[field] = info[field];
      });
      this.agreementForm.setFieldsValue(obj);
    },

    // 处理文件上传变化
    agreementHandleChange({ fileList }) {
      this.agreementFileList = fileList;
    },

    // 文件预览
    handleAgreementPreview(file) {
      this.agreementPreviewImage = file.url || file.preview;
      this.agreementPreviewVisible = true;
    },

    // 关闭预览弹窗
    handleAgreementCancel() {
      this.agreementPreviewVisible = false;
    },

    // 提交三方协议信息
    handleAgreementSubmit() {
      this.agreementForm.validateFields((err, values) => {
        if (!err) {
          this.agreementLoading = true;
          const fileUrl = this.agreementFileList[0]?.response?.data || this.agreementFileList[0]?.url;
          const fileName = this.agreementFileList[0]?.name;

          const url = this.agreementInfo ? '/cos/tripartite-agreements/update' : '/cos/tripartite-agreements/add';
          this.$post(url, {
            ...values,
            userId: this.currentUser.userId,
            id: this.agreementInfo?.id || null,
            fileUrl,
            fileName,
          }).then(() => {
            this.$message.success(this.agreementInfo ? '修改成功' : '添加成功');
            this.agreementLoading = false;
            this.loadAgreementInfo(); // 重新加载数据
          }).catch(() => {
            this.agreementLoading = false;
          });
        }
      });
    },


  },
};
</script>

<style scoped>.user-tabs {
  background: #fff;
  border-radius: 3px;
  overflow: hidden;
}

.tab-content {
  padding: 20px;
}

.material-uploader >>> .ant-upload {
  width: 100%;
  max-width: 150px;
  height: 150px;
}

.agreement-card {
  border-radius: 3px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
</style>
