package cn.webwss.backend.service;

import cn.webwss.backend.domain.UploadFile;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
* @author admin
* @description 针对表【file】的数据库操作Service
* @createDate 2025-08-08 16:32:37
*/
public interface FileService extends IService<UploadFile> {


    /**
     * 检查是否上传过 或 返回已上传的分片列表
     * @param fileHash
     * @return
     */
    Map<String, Object> check(String fileHash);


    /**
     * 上传分片
     * @param chunk
     * @param fileHash
     * @param indexStr
     * @return
     */
    String uploadChunk(MultipartFile chunk, String fileHash, String indexStr);

    /**
     * 合并分片
     * @param fileHash
     * @param total
     * @param fileName
     * @return
     */
    Map<String, Object> merge(String fileHash, int total, String fileName) throws IOException;
}
