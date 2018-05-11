package com.bdis.src.api.controller;


import com.bdis.src.api.urils.QINiuFileUpload;
import com.bdis.src.api.urils.ResponseRestful;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value="/upload")
public class PublicUploadController {
    private static final Logger logger = LogManager.getLogger(PublicUploadController.class);

    @Value("${com.tlk.QN_PAHT}")
    private String QN_PAHT;
    @Value("${com.tlk.QN_BUCKETNAME}")
    private String QN_BUCKETNAME;
    @Value("${com.tlk.QN_ACCESS_KEY}")
    private String QN_ACCESS_KEY;
    @Value("${com.tlk.QN_SECRET_KEY}")
    private String QN_SECRET_KEY;
    @Value("${com.tlk.maxSize}")
    private Long maxSize;

    // 定义允许上传的文件扩展名
    private static final Map<String, String> extMap = new HashMap<String, String>();

    static {
        // 其中images,flashs,medias,files,对应文件夹名称,对应dirName
        // key文件夹名称
        // value该文件夹内可以上传文件的后缀名
        extMap.put("images", "gif,GIF,jpg,JPG,jpeg,JPEG,png,PNG,bmp,BMP");
        extMap.put("flashs", "swf,SWF,flv,FLV");
        extMap.put("medias", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb,SWF,FLV,MP3,WAV,WMA,WMV,MID,AVI,MPG,ASF,RM,RMVB");
        extMap.put("files", "gif,GIF,jpg,JPG,jpeg,JPEG,png,PNG,bmp,BMP,doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2,DOC,DOCX,XLS,XLSX,PPT,HTM,HTML,TXT,ZIP,RAR,GZ,BZ2");
        extMap.put("sensitive", "txt,TXT");
    }
    /**
     * 文件大小转换为字符串格式
     * @param size 文件大小(单位B)
     * @return
     */
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

//    获取七牛图片详细信息
//    GET <imageDownloadUri>?imageInfo HTTP/1.1
//         返回   {
//            "size":         "<ImageSize         int>",
//            "format":       "<ImageType         string>",
//            "width":         <ImageWidth        int>,
//            "height":        <ImageHeight       int>,
//            "colorModel":   "<ImageColorModel   string>",
//            "frameNumber":   <ImageFrameNumber  int>
//                  }

//    获取七牛生成缩略图
//    GET <imageDownloadUri>?imageView2/0/w/<LongEdge>/h/<ShortEdge>
//    限定缩略图的长边最多为<LongEdge>，短边最多为<ShortEdge>，
//    进行等比缩放，不裁剪。如果只指定 w 参数则表示限定长边（短边自适应），
//    只指定 h 参数则表示限定短边（长边自适应）。



    /**
     * 附件上传
     * @param request
     * @param response
     * @param file
     * @return 数组
     */
    @ResponseBody
    @RequestMapping(value = "/files")
    public ResponseRestful files(HttpServletRequest request, HttpServletResponse response,@RequestParam MultipartFile[] file) {
        // 设置响应给前台内容的数据格式
        response.setContentType("text/plain; charset=UTF-8");

        List<String> list=new ArrayList<String>();
         int fs= file.length;
         if(fs==0){
               return new ResponseRestful(100,"请选择文件后上传",null);
         }
         String msg ="";
        for(MultipartFile filse :file){
            //获取文件类型
            //filse.getContentType();

            if (filse.isEmpty()) {
                msg = "请选择文件后上传";
                break;
            } else if (maxSize < filse.getSize()) {
                msg = "您上传的文件太大,系统允许最大文件"+convertFileSize(maxSize)+"M!";
                break;
            } else {
                try {
                // 获取文件名
                String    originalFilename = filse.getOriginalFilename();
                // 文件名后缀处理---start---
                String suffix = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
                String filePath = "files/"+ UUID.randomUUID().toString()+suffix;
                    String imgUlr= QINiuFileUpload.upload(filse,filePath,QN_ACCESS_KEY,QN_SECRET_KEY,QN_BUCKETNAME);
                    if(!imgUlr.equals("")){
                        list.add(QN_PAHT+imgUlr);
                    }else {
                        msg ="上传失败";
                        break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    msg ="上传失败";
                    break;
                }
            }
        }




        if(list.size()==fs){
            logger.info("上传成功");
            return new ResponseRestful(200,"上传成功",list);
        }else{
            logger.info(msg);
            return new ResponseRestful(100,msg,null);
        }


    }



}