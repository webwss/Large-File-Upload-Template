import request from "@/utils/request.js"

// 检查文件状态（用于秒传和断点续传）
export const checkFileStatus = (fileHash) => {
    return request({
        url: '/upload/check',
        method: 'POST',
        params: {
            fileHash
        }
    })
};

// 上传文件分片
export const uploadFileChunk = (form) => {
    return request({
        url: '/upload/chunk',
        method: 'POST',
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        data: form
    })
};

// 合并文件分片
export const mergeFileChunks = (hash, total, fileName) => {
    return request({
        url: '/upload/merge',
        method: 'POST',
        params: {
            fileHash: hash,
            total,
            fileName
        }
    })

};
