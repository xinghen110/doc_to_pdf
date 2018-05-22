package com.cg.cm.doc_to_pdf.controller;

import com.cg.cm.doc_to_pdf.util.BaseReturn;
import com.cg.cm.doc_to_pdf.util.FileEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class TransController {

    @RequestMapping(value = "/convert" ,method = RequestMethod.POST)
    public BaseReturn convert(@RequestBody FileEntity file){
        try {
            String path = ResourceUtils.getURL("").getPath();
            if(path.startsWith("/")){
                path = path.substring(1,path.length());
            }
            System.out.println("path:"+path);
            file.trans(path);

            System.out.println("size:"+file.getFileContent().length());
            return new BaseReturn(file);
        } catch (Exception e) {
            return new BaseReturn("error", e.getMessage());
        } finally {
            //删除临时文件
            file.deleteTempFile();
        }
    }
}
