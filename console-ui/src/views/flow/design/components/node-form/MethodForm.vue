<script lang="ts" setup>
import SuiteSelect from '@/components/form/SuiteSelect.vue';
import ApiSelect from '@/components/form/ApiSelect.vue';
import OutputRuleSetting from '@/components/form/OutputRuleSetting.vue';
import InputRuleSetting from '@/components/form/InputRuleSetting.vue';
import { computed, PropType, ref, watch } from 'vue';
import { ElementType, FlowVariableType, MethodInfo, RawData } from '../../types';
import { apiService } from '@/service';
import { InputParams, valueType } from '@/typings';
import { useFlowDataInject } from '../../hooks/flow-data';
import { cloneDeep } from 'lodash-es';
import { ElMessage } from 'element-plus';

const flowContext = useFlowDataInject();

type MethodRawData = RawData & { method: MethodInfo };

function getDefaultData() {
  return {
    key: '',
    name: '',
    outgoings: [],
    incomings: [],
    elementType: ElementType.METHOD,
    desc: '',
    method: {
      methodId: null as unknown as number,
      suiteId: null as unknown as number,
      url: '',
      requestType: '',
      requestContentType: '',
      inputParamSchemas: [],
      headerFillRules: [],
      inputFillRules: [],
      outputFillRules: [],
    },
  };
}

const emit = defineEmits(['update', 'cancel']);
const props = defineProps({
  data: {
    type: Object as PropType<MethodRawData>,
    required: true,
  },
});

const nodeData = ref(getDefaultData() as MethodRawData);
watch(
  () => props.data,
  val => {
    if (val !== nodeData.value) {
      nodeData.value = Object.assign(getDefaultData(), cloneDeep(val));
      if (nodeData.value.method.methodId) {
        initApiSourceList(nodeData.value.method.methodId);
      }
    }
  },
  { immediate: true }
);

function onSuiteChange() {
  nodeData.value.method = {
    ...getDefaultData().method,
    suiteId: nodeData.value.method?.suiteId as number,
  };
}

function paramToRule(param: { dataType: string; paramKey: string; paramName: string; required?: boolean }, sourceType: valueType) {
  const result = {
    source: '',
    sourceDataType: null,
    sourceType: valueType.VARIABLE,
    target: param.paramKey,
    targetDataType: param.dataType,
    targetType: sourceType,
    required: true,
  };
  return result;
}

async function initApiSourceList(val: number) {
  const res = await apiService.queryApiInfo(val);
  if (res.result) {
    const result = res.result;
    headerSourceList.value = result.apiHeaders.map(item => ({ ...item, targetType: valueType.HEADER }));
    inputSourceList.value = result.apiInputParams.map(item => ({ ...item, targetType: valueType.INPUT_PARAM }));
    outputSourceList.value = result.apiOutputParams.map(item => ({ ...item, sourceType: valueType.OUTPUT_PARAM }));
    // 默认必填 - 头参
    const headerRequired = result.apiHeaders.filter(item => item.required);
    headerRequiredKeys.value = headerRequired.map(item => item.paramKey);
    // 默认必填 - 入参
    const inputRequired = result.apiInputParams.filter(item => item.required);
    inputRequiredKeys.value = inputRequired.map(item => item.paramKey);
  }
}

async function onApiChange(val: number) {
  const res = await apiService.queryApiInfo(val);
  if (res.result) {
    const result = res.result;
    const method = nodeData.value.method;
    method.requestType = result.apiRequestType;
    method.requestContentType = result.apiRequestContentType;
    method.inputParamSchemas = result.apiInputParams.map((item: InputParams) => {
      return {
        paramKey: item.paramKey,
        paramName: item.paramName,
        dataType: item.dataType,
        position: item.paramPosition,
      };
    });
    headerSourceList.value = result.apiHeaders.map(item => ({ ...item, targetType: valueType.HEADER }));
    inputSourceList.value = result.apiInputParams.map(item => ({ ...item, targetType: valueType.INPUT_PARAM }));
    outputSourceList.value = result.apiOutputParams.map(item => ({ ...item, sourceType: valueType.OUTPUT_PARAM }));
    // 默认必填 - 头参
    const headerRequired = result.apiHeaders.filter(item => item.required);
    method.headerFillRules = headerRequired.map(item => paramToRule(item, valueType.HEADER));
    headerRequiredKeys.value = headerRequired.map(item => item.paramKey);

    // 默认必填 - 入参
    const inputRequired = result.apiInputParams.filter(item => item.required);
    method.inputFillRules = inputRequired.map(item => paramToRule(item, valueType.INPUT_PARAM));
    inputRequiredKeys.value = inputRequired.map(item => item.paramKey);

    method.outputFillRules = [];
    method.url = result.apiUrl;
  }
}

// 参数
const headerSourceList = ref<any[]>([]);
const inputSourceList = ref<any[]>([]);
const outputSourceList = ref<any[]>([]);
// 必填，不可删除
const headerRequiredKeys = ref<string[]>([]);
const inputRequiredKeys = ref<string[]>([]);

const inputTargetList = computed(() => {
  const flowVariables = flowContext.data.value.flowVariables;
  return flowVariables.filter(item => [FlowVariableType.INPUT, FlowVariableType.TEMP].includes(item.envType));
});

const outputTargetList = computed(() => {
  const flowVariables = flowContext.data.value.flowVariables;
  return flowVariables.filter(item => [FlowVariableType.OUTPUT, FlowVariableType.TEMP].includes(item.envType));
});

function validateParam(param: any) {
  if (!param.source) {
    return false;
  }
  if (!param.target) {
    return false;
  }
  return true;
}

function validate() {
  if (!nodeData.value.name) {
    ElMessage.error('节点名称不能为空');
    return false;
  }
  const method = nodeData.value.method;
  if (!method.methodId) {
    ElMessage.error('服务接口不能为空');
    return false;
  }
  if (method.headerFillRules.length > 0 && !method.headerFillRules.every(validateParam)) {
    ElMessage.error('请求头赋值不完整');
    return false;
  }
  if (method.inputFillRules.length > 0 && !method.inputFillRules.every(validateParam)) {
    ElMessage.error('入参赋值不完整');
    return false;
  }
  if (method.outputFillRules.length > 0 && !method.outputFillRules.every(validateParam)) {
    ElMessage.error('出参赋值不完整');
    return false;
  }
  return true;
}

function onSubmit() {
  if (!validate()) {
    return;
  }
  emit('update', cloneDeep(nodeData.value));
}
function onCancel() {
  emit('cancel');
}
</script>

<template>
  <div class="node-method-form">
    <el-form label-position="top">
      <el-form-item label="节点编码">
        <span>{{ nodeData.key }}</span>
      </el-form-item>
      <el-form-item label="节点名称" required>
        <el-input v-model="nodeData.name" placeholder="请输入"></el-input>
      </el-form-item>
      <el-form-item label="节点描述">
        <el-input v-model="nodeData.desc" placeholder="请输入" :rows="2" type="textarea"></el-input>
      </el-form-item>
      <el-form-item label="套件" required>
        <SuiteSelect v-model="nodeData.method.suiteId" :auto="true" @change="onSuiteChange" />
      </el-form-item>
      <el-form-item label="服务接口" required>
        <ApiSelect v-model="nodeData.method.methodId" :suiteId="nodeData.method.suiteId" @change="onApiChange" />
      </el-form-item>
      <el-form-item label="请求头赋值">
        <InputRuleSetting
          v-model="nodeData.method.headerFillRules"
          addText="新增请求头赋值"
          showRequired
          :sourceList="headerSourceList"
          :targetList="inputTargetList"
          :requiredKeys="headerRequiredKeys"
        />
      </el-form-item>
      <el-form-item label="入参赋值">
        <InputRuleSetting
          v-model="nodeData.method.inputFillRules"
          addText="新增入参赋值"
          showRequired
          :sourceList="inputSourceList"
          :targetList="inputTargetList"
          :requiredKeys="inputRequiredKeys"
        />
      </el-form-item>
      <el-form-item label="出参赋值">
        <OutputRuleSetting
          v-model="nodeData.method.outputFillRules"
          addText="新增出参赋值"
          :sourceList="outputSourceList"
          :targetList="outputTargetList"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">确定</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
