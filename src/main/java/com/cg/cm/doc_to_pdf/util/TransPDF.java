package com.cg.cm.doc_to_pdf.util;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import java.io.*;
import java.util.Base64;

public class TransPDF {

    private static final int wdFormatPDF = 17;// PDF 格式
    private static final int xlTypePDF = 0;
    private static final int ppSaveAsPDF = 32;

    public static final String APP_WORD = "Word.Application";
    public static final String APP_EXCEL = "Excel.Application";
    public static final String APP_PPT = "PowerPoint.Application";

    private static final String TYPE_WORD = "Documents";
    private static final String TYPE_EXCEL = "Workbooks";
    private static final String TYPE_PPT = "Presentations";

    private String path = "/trans/";
    private String sfileName = "";
    private String tofileName = "";

    private Base64.Encoder encoder = Base64.getEncoder();
    private Base64.Decoder decoder = Base64.getDecoder();

    /***
     * 预处理源文件及PDF文件
     *
     * @param path      根目录
     * @param fileName  源文件名
     * @param content   源文件内容（base64编码）
     */
    public TransPDF(String path, String fileName, String content, FileEntity entity) throws TransException{
        //预设临时文件路径
        this.path = path+this.path;
        this.sfileName = path + "doc/" + fileName;
        this.tofileName = path + "pdf/" + fileName + ".pdf";

        File tofile = new File(this.tofileName);
        File sfile = new File(this.sfileName);

        tofile.mkdirs();
        sfile.mkdirs();
        //如果文件存在则删除
        if (tofile.exists()) {
            tofile.delete();
        }
        if (sfile.exists()) {
            sfile.delete();
        }
        //源文件写入临时文件夹
        try (FileOutputStream os = new FileOutputStream(sfile)) {
            os.write(decoder.decode(content));
            os.flush();
            //临时文件路径
            entity.sfile = this.sfileName;
            System.out.println("待转换文件：" + sfile.getAbsolutePath() + "已保存至磁盘");
        }catch (Exception e){
            throw new TransException(e.getMessage());
        }
    }

    /***
     * 返回文件的Base64编码后的字符串
     *
     * @return pdf文件Base64编码后的字符串
     */
    public String toBase64(FileEntity entity) throws TransException {
        File f = new File(tofileName);
        Long size = f.length();
        byte[] b;
        try (FileInputStream is = new FileInputStream(f)){
            b = new byte[size.intValue()];
            is.read(b);
            //临时文件路径
            entity.tofile = this.tofileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new TransException(e.getMessage());
        }
        return encoder.encodeToString(b);
    }
    /***
     * Excel转化成PDF
     *
     * @param activeX ActiveXComponent
     */
    public void wordToPDF(ActiveXComponent activeX) throws TransException{
        Dispatch dis = null;
        try {
            System.out.println("打开文档:" + this.sfileName);
            activeX.setProperty("Visible", new Variant(false));
            dis = Dispatch.call(activeX.getProperty(this.TYPE_WORD).toDispatch(), "Open", this.sfileName).toDispatch();

            System.out.println("转换后保存PDF:" + this.tofileName);
            Dispatch.call(dis, "SaveAs", this.tofileName, this.wdFormatPDF);
        } catch (Exception e) {
            System.out.println("错误：文档转换失败：" + e.getMessage());
            e.printStackTrace();
            throw new TransException("错误：" + e.getMessage());
        } finally {
            if(dis!=null){
                Dispatch.call(dis, "Close", false);
            }
            System.out.println("关闭文档");
        }
    }
    /***
     * Excel转化成PDF
     *
     * @param activeX ActiveXComponent
     */
    public void excelToPDF(ActiveXComponent activeX) throws TransException{
        Dispatch dis = null;
        try {
            System.out.println("打开文档:" + sfileName);
            activeX.setProperty("Visible", new Variant(false));
            activeX.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
            dis = Dispatch.call(activeX.getProperty(this.TYPE_EXCEL).toDispatch(),"Open",sfileName.replaceAll("/","\\\\")).toDispatch();

            System.out.println("转换后保存PDF:" + tofileName);
            Dispatch.invoke(dis, "ExportAsFixedFormat", Dispatch.Method,
                    new Object[] { new Variant(this.xlTypePDF), // PDF格式=0
                    tofileName, new Variant(0) // 0=标准 (生成的PDF图片不会变模糊) 1=最小文件 (生成的PDF图片糊的一塌糊涂)
            }, new int[1]);
        } catch (Exception es) {
            System.out.println("******************  Error:文档转换失败：" + es.getMessage());
            es.printStackTrace();
            throw new TransException("错误：" + es.getMessage());
        } finally {
            System.out.println("关闭文档");
            if(dis!=null){
                Dispatch.call(dis, "Close");
            }
        }
    }

    /***
     * ppt转化成PDF
     *
     * @param activeX ActiveXComponent
     */
    public void pptToPDF(ActiveXComponent activeX) throws TransException{
        Dispatch ppt = null;
        try {
            System.out.println("打开文档:" + sfileName);
            activeX.setProperty("Visible", true);
            ppt = Dispatch.call(activeX.getProperty(this.TYPE_PPT).toDispatch(), "Open",
                    sfileName, true, // ReadOnly
                    // false, // Untitled指定文件是否有标题
                    false// WithWindow指定文件是否可见
            ).toDispatch();

            System.out.println("转换后保存PDF:" + tofileName);
            Dispatch.call(ppt, "SaveAs", tofileName.replaceAll("/","\\\\"),this.ppSaveAsPDF);
        } catch (Exception e) {
            System.out.println("******************  Error:文档转换失败：" + e.getMessage());
            e.printStackTrace();
            throw new TransException("错误：" + e.getMessage());
        }finally {
            if(ppt!=null){
                Dispatch.call(ppt, "Close");
            }
        }
    }

    public static void main(String[] args) {
/*        try {
            long start = System.currentTimeMillis();
            TransPDF pdf = new TransPDF("","CM_合同管理模块接口设计_OA_v0.1_20150809.ppt","");
            ComThread.InitSTA(true);
            ActiveXComponent app = new ActiveXComponent(pdf.APP_PPT);
            pdf.pptToPDF(app);
            app.invoke("Quit");
            long end = System.currentTimeMillis();
            System.out.println("转换完成，耗时:" + (end - start) + "ms.");
            // 如果没有这行代码，winword.exe进程将不会关闭
            ComThread.Release();
        } catch (TransException e) {
            e.printStackTrace();
        }*/

    }
}
