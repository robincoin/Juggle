<script lang="ts" setup>
defineProps({
  dataRows: Array,
  pageNum: Number,
  pageSize: Number,
  dataTotal: Number,
  loading: Boolean,
});
const emit = defineEmits(['pageChange', 'connect', 'edit', 'delete']);

function connect(row: any) {
  emit('connect', row);
}
function editRow(row: any) {
  emit('edit', row);
}

function deleteRow(row: any, index: number) {
  emit('delete', row, index);
}
</script>

<template>
  <el-table v-loading="loading" :data="dataRows" size="large" header-cell-class-name="table-header">
    <el-table-column prop="dataSourceName" label="数据源名称" />
    <el-table-column prop="dataSourceType" label="数据源类型" />
    <el-table-column prop="address" label="连接地址" width="380" />
    <el-table-column label="操作" width="180">
      <template #default="scope">
        <el-button link type="primary" size="small" @click.prevent="connect(scope.row)"> 连接 </el-button>
        <el-button link type="primary" size="small" @click.prevent="editRow(scope.row)"> 编辑 </el-button>
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
