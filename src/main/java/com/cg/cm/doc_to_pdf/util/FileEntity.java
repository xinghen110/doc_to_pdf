package com.cg.cm.doc_to_pdf.util;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;

import java.io.File;

public class FileEntity {
    private String fileName = "";
    private String fileContent = "";
    private String fileType = "";

    protected String sfile = "";
    protected String tofile = "";

    public void setFileName(String fileName) throws TransException{

        if(fileName == null || "".equals(fileName)) throw new TransException("错误：文件名不能为空！");
        this.fileName = fileName;

        String ext = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());

        switch (ext){
            case "doc": this.fileType = "word";return;
            case "docx": this.fileType = "word";return;
            case "txt": this.fileType = "word";return;
            case "htm": this.fileType = "word";return;
            case "html": this.fileType = "word";return;
            case "png": this.fileType = "word";return;
            case "jpeg": this.fileType = "word";return;
            case "jpg": this.fileType = "word";return;

            case "xls": this.fileType = "excel";return;
            case "xlsx": this.fileType = "excel";return;
            case "csv": this.fileType = "excel";return;

            case "ppt": this.fileType = "ppt";return;
            case "pptx": this.fileType = "ppt";return;

            default:throw new TransException("错误：不支持的文件后缀" + ext);
        }
    }

    public void trans(String path) throws TransException{
        long start = System.currentTimeMillis();
        TransPDF pdf = new TransPDF( path, this.fileName, this.fileContent, this);

        ComThread.InitSTA(true);
        ActiveXComponent app = null;

        if(fileType.equals("word")){
            app = new ActiveXComponent(TransPDF.APP_WORD);
            pdf.wordToPDF(app);
        }else if(fileType.equals("excel")){
            app = new ActiveXComponent(TransPDF.APP_EXCEL);
            pdf.excelToPDF(app);
        }else if(fileType.equals("ppt")){
            app = new ActiveXComponent(TransPDF.APP_PPT);
            pdf.pptToPDF(app);
        }

        app.invoke("Quit");
        long end = System.currentTimeMillis();
        System.out.println("转换完成，耗时:" + (end - start) + "ms.");
        // 如果没有这行代码，winword.exe进程将不会关闭
        ComThread.Release();
        this.fileContent = pdf.toBase64(this);
        return;
    }
    //删除临时文件
    public void deleteTempFile(){
        new File(sfile).delete();
        new File(tofile).delete();
    }

    public FileEntity() {
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
