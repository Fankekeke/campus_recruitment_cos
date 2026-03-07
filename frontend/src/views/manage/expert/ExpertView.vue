<template>
  <a-modal v-model="show" title="学生详情" @cancel="onClose" :width="800">
    <template slot="footer">
      <a-button key="back" @click="onClose" type="danger">
        关闭
      </a-button>
    </template>
    <div style="font-size: 13px;font-family: SimHei" v-if="expertData !== null">
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">学生信息</span></a-col>
        <a-col :span="8"><b>学生姓名：</b>
          {{ expertData.name }}
        </a-col>
        <a-col :span="8"><b>民族：</b>
          {{ expertData.nationality }}
        </a-col>
        <a-col :span="8"><b>性别：</b>
          {{ expertData.sex }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>政治面貌：</b>
          {{ expertData.politicalStatus }}
        </a-col>
        <a-col :span="8"><b>籍贯：</b>
          {{ expertData.nativePlace }}
        </a-col>
        <a-col :span="8"><b>工作单位：</b>
          {{ expertData.employer }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>职务：</b>
          {{ expertData.position }}
        </a-col>
        <a-col :span="16"><b>通讯地址：</b>
          {{ expertData.address }}
        </a-col>
        <br/>
        <br/>
        <a-col :span="8"><b>手机号：</b>
          {{ expertData.phone }}
        </a-col>
        <a-col :span="8"><b>专业方向一级：</b>
          {{ expertData.levelOne }}
        </a-col>
      </a-row>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <br/>
        <a-col :span="8"><b>出生日期：</b>
          {{ expertData.birthDate }}
        </a-col>
        <a-col :span="16"><b>专业方向二级：</b>
          {{ expertData.levelTwo }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>职称：</b>
          {{ expertData.jobTitle }}
        </a-col>
        <a-col :span="8"><b>特殊称谓：</b>
          {{ expertData.specialAppellation }}
        </a-col>
        <a-col :span="8"><b>邮箱：</b>
          {{ expertData.mail }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="8"><b>固定电话：</b>
          {{ expertData.fixedTelephone }}
        </a-col>
        <a-col :span="8"><b>创建时间：</b>
          {{ expertData.createDate }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col :span="24"><b>个人简介：</b>
          {{ expertData.profile }}
        </a-col>
      </a-row>
      <br/>
      <a-row style="padding-left: 24px;padding-right: 24px;">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">学生照片</span></a-col>
        <a-col :span="24">
          <a-upload
            name="avatar"
            action="http://127.0.0.1:9527/file/fileUpload/"
            list-type="picture-card"
            :file-list="fileList"
            @preview="handlePreview"
            @change="picHandleChange"
          >
          </a-upload>
          <a-modal :visible="previewVisible" :footer="null" @cancel="handleCancel">
            <img alt="example" style="width: 100%" :src="previewImage" />
          </a-modal>
        </a-col>
      </a-row>
      <br/>
      <!-- 新增：就业去向信息 -->
      <a-row style="padding-left: 24px;padding-right: 24px;" v-if="employmentDestinations">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">就业去向信息</span></a-col>
        <a-col :span="8"><b>类型：</b>
          {{ employmentDestinations.type === 1 ? '就业' : employmentDestinations.type === 2 ? '升学' : '其他' }}
        </a-col>
        <a-col :span="8"><b>单位名称：</b>
          {{ employmentDestinations.companyName }}
        </a-col>
        <a-col :span="8"><b>组织机构代码：</b>
          {{ employmentDestinations.organizationCode }}
        </a-col>
      </a-row>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 15px;" v-if="employmentDestinations">
        <a-col :span="8"><b>工作城市：</b>
          {{ employmentDestinations.city }}
        </a-col>
        <a-col :span="8"><b>创建时间：</b>
          {{ employmentDestinations.createTime }}
        </a-col>
      </a-row>

      <br/>

      <!-- 新增：三方协议信息 -->
      <a-row style="padding-left: 24px;padding-right: 24px;" v-if="tripartiteAgreements">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">三方协议信息</span></a-col>
        <a-col :span="8"><b>协议编号：</b>
          {{ tripartiteAgreements.agreementNo }}
        </a-col>
        <a-col :span="8"><b>企业名称：</b>
          {{ tripartiteAgreements.enterpriseName }}
        </a-col>
        <a-col :span="8"><b>签订日期：</b>
          {{ tripartiteAgreements.signDate }}
        </a-col>
      </a-row>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 15px;" v-if="tripartiteAgreements">
        <a-col :span="8"><b>上传时间：</b>
          {{ tripartiteAgreements.uploadTime }}
        </a-col>
        <a-col :span="16">
          <b>协议文件：</b>
          <a v-if="tripartiteAgreements.fileUrl" @click="previewFile(tripartiteAgreements.fileUrl)" style="color: #1890ff;cursor: pointer;">
            <a-icon type="file-image" /> 查看协议
          </a>
          <span v-else style="color: #999;">未上传</span>
        </a-col>
      </a-row>

      <br/>

      <!-- 新增：就业证明材料信息 -->
      <a-row style="padding-left: 24px;padding-right: 24px;" v-if="employmentEvidence">
        <a-col style="margin-bottom: 15px"><span style="font-size: 15px;font-weight: 650;color: #000c17">就业证明材料信息</span></a-col>
        <a-col :span="8"><b>材料类型：</b>
          {{ employmentEvidence.materialType }}
        </a-col>
        <a-col :span="8"><b>上传时间：</b>
          {{ employmentEvidence.createTime }}
        </a-col>
        <a-col :span="8">
          <b>证明材料：</b>
          <a v-if="employmentEvidence.fileUrl" @click="previewFile(employmentEvidence.fileUrl)" style="color: #1890ff;cursor: pointer;">
            <a-icon type="file-image" /> 查看材料
          </a>
          <span v-else style="color: #999;">未上传</span>
        </a-col>
      </a-row>
      <a-row style="padding-left: 24px;padding-right: 24px;margin-top: 15px;" v-if="employmentEvidence">
        <a-col :span="24"><b>备注说明：</b>
          {{ employmentEvidence.remark || '无' }}
        </a-col>
      </a-row>
      <br/>
    </div>
  </a-modal>
</template>

<script>
import moment from 'moment'
import {mapState} from 'vuex'
moment.locale('zh-cn')
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
export default {
  name: 'expertView',
  props: {
    expertShow: {
      type: Boolean,
      default: false
    },
    expertData: {
      type: Object
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.expertShow
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      employmentDestinations: null,
      tripartiteAgreements: null,
      employmentEvidence: null
    }
  },
  watch: {
    expertShow: function (value) {
      if (value) {
        this.queryExpertDetail()
        if (this.expertData.images !== null && this.expertData.images !== '') {
          this.imagesInit(this.expertData.images)
        }
      }
    }
  },
  methods: {
    queryExpertDetail () {
      this.$get(`/cos/expert-info/queryExpertDetail`, {
        userId: this.expertData.id
      }).then((r) => {
        console.log(JSON.stringify(r.data))
        // 存储返回的就业相关数据
        if (r.data.code === 0) {
          const data = r.data
          this.employmentDestinations = data.employmentDestinations || null
          this.tripartiteAgreements = data.tripartiteAgreements || null
          this.employmentEvidence = data.employmentEvidence || null
        }
      })
    },
    previewFile (fileUrl) {
      // 预览文件，这里假设文件是图片格式
      if (fileUrl) {
        window.open(`http://127.0.0.1:9527/imagesWeb/${fileUrl}`, '_blank')
      }
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
    onClose () {
      this.$emit('close')
    }
  }
}
</script>

<style scoped>

</style>
