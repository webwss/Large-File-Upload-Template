package cn.webwss.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.webwss.backend.domain.UploadFile;
import cn.webwss.backend.service.FileService;
import cn.webwss.backend.mapper.UploadFileMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author admin
* @description 针对表【file】的数据库操作Service实现
* @createDate 2025-08-08 16:32:37
*/
@Service
public class FileServiceImpl extends ServiceImpl<UploadFileMapper, UploadFile>
    implements FileService{

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 检查是否上传过 或 返回已上传的分片列表
     * @param fileHash
     * @return
     */
    @Override
    public Map<String, Object> check(String fileHash) {
        File fileDir = new File(uploadPath + fileHash);
//        File mergedFile = new File(uploadPath, fileHash); // 合并后的文件在uploadPath目录下

        Map<String, Object> res = new HashMap<>();

        //        校验文件Hash是否已存在
        UploadFile file = this.getOne(new QueryWrapper<UploadFile>().eq("file_hash", fileHash));
        if (file != null) {
            res.put("uploaded", true);
        }else {
            res.put("uploaded", false);
            if (!fileDir.exists()) {
                res.put("uploadedChunks", Collections.emptyList());
            } else {
                String[] uploadedChunks = fileDir.list((dir, name) -> name.matches("\\d+"));
                res.put("uploadedChunks", uploadedChunks != null ? List.of(uploadedChunks) : List.of());
            }
        }

//        if (mergedFile.exists()) {
//            res.put("uploaded", true);
//        } else {
//            res.put("uploaded", false);
//            if (!fileDir.exists()) {
//                res.put("uploadedChunks", Collections.emptyList());
//            } else {
//                String[] uploadedChunks = fileDir.list((dir, name) -> name.matches("\\d+"));
//                res.put("uploadedChunks", uploadedChunks != null ? List.of(uploadedChunks) : List.of());
//            }
//        }
        return res;
    }

    /**
     * 上传分片
     * @param chunk
     * @param fileHash
     * @param indexStr
     * @return
     */
    @Override
    public String uploadChunk(MultipartFile chunk, String fileHash, String indexStr) {
        int index = Integer.parseInt(indexStr);
        File dir = new File(uploadPath + fileHash);
        if (!dir.exists()) dir.mkdirs();
        File chunkFile = new File(dir, String.valueOf(index));
        try {
            chunk.transferTo(chunkFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "chunk saved";
    }


    /**
     * 合并分片
     * @param fileHash
     * @param total
     * @param fileName
     * @return
     */
    @Override
    public Map<String, Object> merge(String fileHash, int total, String fileName) throws IOException {
        File dir = new File(uploadPath + fileHash);

        // ✅ 获取文件后缀名
        String suffix = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            suffix = fileName.substring(dotIndex); // 例如 ".mp4"
        }

        // ✅ 生成唯一文件名
        String randomFileName = fileHash + suffix;

        // ✅ 构造合并后的目标文件
        File mergedFile = new File(uploadPath, randomFileName);

        try (FileOutputStream fos = new FileOutputStream(mergedFile, true)) {
            for (int i = 0; i < total; i++) {
                File chunkFile = new File(dir, String.valueOf(i));
                if (!chunkFile.exists()) {
                    return Map.of("error", "Missing chunk: " + i);
                }
                try (FileInputStream fis = new FileInputStream(chunkFile)) {
                    StreamUtils.copy(fis, fos);
                }
            }
        }

        // 删除分片文件和目录
        for (int i = 0; i < total; i++) {
            new File(dir, String.valueOf(i)).delete();
        }
        dir.delete();

        // ✅ 返回文件名给前端
        Map<String, Object> response = new HashMap<>();
        response.put("message", "merge complete");
        response.put("fileName", randomFileName);
        response.put("url", "/files/" + randomFileName); // 如果你支持静态访问的话
//        保存到数据库
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFileHash(fileHash);
        uploadFile.setFileUrl("/files/" + randomFileName);
        save(uploadFile);
        return response;
    }
}




