package cn.webwss.backend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName file
 */
@TableName(value ="uploadfile")
@Data
public class UploadFile {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文件hash
     */
    private String fileHash;

    /**
     * 文件路径
     */
    private String fileUrl;
}