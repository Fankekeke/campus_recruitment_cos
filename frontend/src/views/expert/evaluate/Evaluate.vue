<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
<!--          <div :class="advanced ? null: 'fold'">-->
<!--            <a-col :md="6" :sm="24">-->
<!--              <a-form-item-->
<!--                label="标题"-->
<!--                :labelCol="{span: 5}"-->
<!--                :wrapperCol="{span: 18, offset: 1}">-->
<!--                <a-input v-model="queryParams.title"/>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
<!--            <a-col :md="6" :sm="24">-->
<!--              <a-form-item-->
<!--                label="内容"-->
<!--                :labelCol="{span: 5}"-->
<!--                :wrapperCol="{span: 18, offset: 1}">-->
<!--                <a-input v-model="queryParams.content"/>-->
<!--              </a-form-item>-->
<!--            </a-col>-->
<!--          </div>-->
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
<!--        <a-button type="primary" ghost @click="add">新增</a-button>-->
        <a-button @click="batchDelete">删除</a-button>
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="titleShow" slot-scope="text, record">
          <template>
            <a-badge status="processing" v-if="record.rackUp === 1"/>
            <a-badge status="error" v-if="record.rackUp === 0"/>
            <a-tooltip>
              <template slot="title">
                {{ record.title }}
              </template>
              {{ record.title.slice(0, 8) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="contentShow" slot-scope="text, record">
          <template>
            <a-tooltip>
              <template slot="title">
                {{ record.content }}
              </template>
              {{ record.content.slice(0, 25) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon
            type="eye"
            theme="twoTone"
            twoToneColor="#1890ff"
            @click="view(record)"
            title="查 看">
          </a-icon>
        </template>
      </a-table>
    </div>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'Bulletin',
  components: {RangeDate},
  data () {
    return {
      advanced: false,
      bulletinAdd: {
        visiable: false
      },
      bulletinEdit: {
        visiable: false
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      evaluationView: {
        visiable: false,
        data: null
      },
      userList: []
    }
  },

  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),

    columns () {
      return [{
        title: '应聘者',
        dataIndex: 'expertName',
        width: 120,
        customRender: (text, record) => {
          return text || '- -'
        }
      }, {
        title: '应聘岗位',
        dataIndex: 'postName',
        width: 200,
        scopedSlots: { customRender: 'postShow' }
      }, {
        title: '企业名称',
        dataIndex: 'enterpriseName',
        width: 180,
        customRender: (text, record) => {
          return text || '- -'
        }
      }, {
        title: '工作地点',
        dataIndex: 'address',
        width: 120,
        customRender: (text, record) => {
          return text || '- -'
        }
      }, {
        title: '薪资范围',
        dataIndex: 'salary',
        width: 120,
        customRender: (text, record) => {
          return text || '面议'
        }
      }, {
        title: '满意度',
        dataIndex: 'satisfy',
        width: 100,
        customRender: (text, record) => {
          if (text) {
            let color = 'default'
            switch (text) {
              case '非常满意':
                color = 'green'
                break
              case '满意':
                color = '#52c41a'
                break
              case '一般':
                color = '#faad14'
                break
              case '不满意':
                color = '#ff7875'
                break
              case '非常不满意':
                color = 'red'
                break
              default:
                color = 'default'
            }
            return <a-tag color={color}>{text}</a-tag>
          } else {
            return <a-tag>未评价</a-tag>
          }
        }
      }, {
        title: '评分',
        dataIndex: 'score',
        width: 80,
        customRender: (text, record) => {
          if (text !== null && text !== undefined) {
            let color = '#f5222d' // 红色 - 低分
            if (text >= 8) {
              color = '#52c41a' // 绿色 - 高分
            } else if (text >= 6) {
              color = '#faad14' // 黄色 - 中等
            }
            return (
              <span style={{color: color, fontWeight: 'bold'}}>
                {text}分
              </span>
            )
          }
          return 'N/A'
        }
      }, {
        title: '评价内容',
        dataIndex: 'content',
        width: 200,
        customRender: (text, record) => {
          if (text) {
            return (
              <a-tooltip>
                <template slot="title">{text}</template>
                {text.length > 20 ? text.substring(0, 20) + '...' : text}
              </a-tooltip>
            )
          }
          return '- -'
        }
      }, {
        title: '评价时间',
        dataIndex: 'createDate',
        width: 160,
        customRender: (text, record) => {
          return text || '- -'
        }
      }, {
        title: '操作',
        dataIndex: 'operation',
        width: 150,
        scopedSlots: { customRender: 'operation' }
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    view (record) {
      // 这里可以打开查看弹窗或跳转到详情页面
      this.$info({
        title: '评价详情',
        content: h => h('div', [
          h('p', `应聘者：${record.expertName || '- -'}`),
          h('p', `应聘岗位：${record.postName}`),
          h('p', `企业名称：${record.enterpriseName}`),
          h('p', `满意度：${record.satisfy}`),
          h('p', `评分：${record.score}分`),
          h('p', `评价内容：${record.content}`),
          h('p', `评价时间：${record.createDate}`)
        ]),
        width: 600,
        okText: '关闭'
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.bulletinAdd.visiable = true
    },
    handleBulletinAddClose () {
      this.bulletinAdd.visiable = false
    },
    handleBulletinAddSuccess () {
      this.bulletinAdd.visiable = false
      this.$message.success('新增公告成功')
      this.search()
    },
    edit (record) {
      this.$refs.bulletinEdit.setFormValues(record)
      this.bulletinEdit.visiable = true
    },
    handleBulletinEditClose () {
      this.bulletinEdit.visiable = false
    },
    handleBulletinEditSuccess () {
      this.bulletinEdit.visiable = false
      this.$message.success('修改公告成功')
      this.search()
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/evaluate-info/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      params.expertId = this.currentUser.userId
      this.$get('/cos/evaluate-info/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
