<script lang="ts" setup>
defineProps({
  dataRows: Array,
  pageNum: Number,
  pageSize: Number,
  dataTotal: Number,
  loading: Boolean,
});
const emit = defineEmits(['pageChange', 'flowVersionStatusChange', 'delete']);

function deleteRow(row: any, index: number) {
  emit('delete', row, index);
}

function updateFlowVersionStatus(row: any) {
  emit('flowVersionStatusChange', row);
}

function flowVersionStatusOptFormat(flowVersionStatus: number) {
  if (flowVersionStatus == 0) {
    return '启用';
  } else {
    return '禁用';
  }
}

function buildFullTriggerUrl(triggerUrl: string) {
  const origin = window.location.origin;
  return origin + triggerUrl;
}
</script>

<template>
  <el-table v-loading="loading" :data="dataRows" size="large" header-cell-class-name="table-header">
    <el-table-column prop="flowName" label="流程名称" width="120" />
    <el-table-column prop="flowVersion" label="版本" width="60" />
    <el-table-column prop="flowVersion" label="流程状态" width="100">
      <template #default="scope">
        <el-tag v-if="scope.row.flowVersionStatus == 1" type="success">启用</el-tag>
        <el-tag v-else type="danger">禁用</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="triggerUrl" label="访问地址" width="480">
      <template #default="scope">
        {{ buildFullTriggerUrl(scope.row.triggerUrl) }}
      </template>
    </el-table-column>
    <el-table-column prop="flowVersionRemark" label="版本说明" width="260" show-overflow-tooltip />
    <el-table-column label="操作" width="250">
      <template #default="scope">
        <el-button link type="primary" size="small" @click.prevent="updateFlowVersionStatus(scope.row)">
          {{ flowVersionStatusOptFormat(scope.row.flowVersionStatus) }}
        </el-button>
        <el-button link type="primary" size="small" @click.prevent="deleteRow(scope.row, scope.$index)"> 删除 </el-button>
      </template>
    </el-table-column>
  </el-table>
  <div class="table-pagination">
    <el-pagination
      :currentPage="pageNum"
      :pageSize="pageSize"
      background
      layout="total, prev, pager, next"
      :total="dataTotal"
      @currentChange="(val: number) => $emit('pageChange', val)"
    />
  </div>
</template>

<style lang="less" scoped>

</style>
