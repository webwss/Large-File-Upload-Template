<template>
  <div>
    <input type="file" @change="onFileChange">
    <div v-if="uploadProgress > 0">上传进度：{{uploadProgress}}%</div>
  </div>
</template>

<script setup>

import {ref} from "vue";
import {checkFileStatus, mergeFileChunks, uploadFileChunk} from "@/apis/FileApi.js";

const uploadProgress = ref(0); // 上传进度
const chunkSize = 1024 * 1024 * 2; // 2MB

const onFileChange = async (e) => {
  const file = e.target.files[0];
  if (!file) return;
  console.log("onFileChange",file)


//   1、计算文件hash
  const fileHash = await calculateFileHash(file);
//   2、请求后端，检查是否已上传或已上传部分分片
  const {data} = await checkFileStatus(fileHash);

  if (data.uploaded) {
    alert('文件已存在（秒传）');
    return;
  }

  const uploadedChunks =  data.uploadedChunks || []
  // 3、文件分片
  const chunks = []
  let cur = 0
  let index = 0
  while (cur < file.size) {
    const chunk = file.slice(cur, Math.min(cur + chunkSize, file.size))
    chunks.push({ chunk, index })
    cur += chunkSize
    index++
  }

  console.log('chunks:', chunks)

  // 4、上传分片（跳过已上传）
  for (const { chunk, index } of chunks) {
    if (uploadedChunks.includes(String(index))) continue
    const form = new FormData()
    console.log('chunk:', chunk)
    form.append('chunk', chunk)
    form.append('fileHash', fileHash)
    form.append('index', index)
    await uploadFileChunk(form)
    uploadProgress.value = Math.floor((index + 1) / chunks.length * 100)
  }

  // 5、合并文件
  await mergeFileChunks(fileHash, chunks.length, file.name)

  alert('上传成功')
};

const calculateFileHash = (file) => {
  return new Promise((resolve) => {
    const worker = new Worker(new URL('../worker/hash.worker.js', import.meta.url), {
      type: 'module'
    })
    worker.postMessage({ file })
    worker.onmessage = (e) => {
      if (e.data.hash) resolve(e.data.hash)
    }
  })
}



</script>

<style scoped>
</style>
