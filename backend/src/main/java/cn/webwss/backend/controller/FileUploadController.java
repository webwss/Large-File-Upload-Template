package cn.webwss.backend.controller;

import cn.webwss.backend.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/upload")
public class FileUploadController {



    @Resource
    private FileService fileService;

    // 检查是否上传过 或 返回已上传的分片列表
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> check(@RequestParam String fileHash) {
        Map<String, Object> result = fileService.check(fileHash);
        return ResponseEntity.ok(result);
    }

    // 上传分片
    @PostMapping("/chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam("fileHash") String fileHash,
            @RequestParam("index") String indexStr) throws IOException {
        String result = fileService.uploadChunk(chunk,fileHash,indexStr);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/merge")
    public ResponseEntity<Map<String, Object>> merge(
            @RequestParam String fileHash,
            @RequestParam int total,
            @RequestParam String fileName) throws IOException {
        Map<String, Object> result = fileService.merge(fileHash, total, fileName);
        return ResponseEntity.ok(result);
    }




}
