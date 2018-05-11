package com.bdis.src.api.urils;//package com.src.common.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class QINiuFileUpload {
    private static   String ACCESS_KEY = "QUm083Z5qU4ITDbxrXYvqVwBd6mKypPE-4WhXRdy";
    private static   String SECRET_KEY = "V3VviNnubfeOMEK6ETQRpcoo1JsE323gKnjbVZaw";
    //要上传的空间
//    @Value("${yiweibao}")
 private static   String bucketname = "yiweibao";
    //上传到七牛后保存的文件名
 private static   String key = "1471405853.jpg";
    //上传文件的路径
 private static  String FilePath = "C:\\Users\\Administrator\\Desktop\\1471405853.jpg";

    //密钥配置


    ///////////////////////指定上传的Zone的信息//////////////////
    //第一种方式: 指定具体的要上传的zone
    //注：该具体指定的方式和以下自动识别的方式选择其一即可
    //要上传的空间(bucket)的存储区域为华东时
    // Zone z = Zone.zone0();
    //要上传的空间(bucket)的存储区域为华北时
    // Zone z = Zone.zone1();
    //要上传的空间(bucket)的存储区域为华南时
    // Zone z = Zone.zone2();


    public static  String upload(MultipartFile myfile,String url,String QN_ACCESS_KEY,String QN_SECRET_KEY,String QN_BUCKETNAME) throws IOException {
    	String realPath="";
        try {
//        	CommonsMultipartFile cf= (CommonsMultipartFile)myfile; //这个myfile是MultipartFile的
//	        DiskFileItem fi = (DiskFileItem)cf.getFileItem();
//	        File file = fi.getStoreLocation();
            FileInputStream inputStream = (FileInputStream) myfile.getInputStream();
        	Configuration cfg = new Configuration(Zone.zone2());
        	UploadManager uploadManager = new UploadManager(cfg);
        	//获取七牛token
            Auth auth = Auth.create(QN_ACCESS_KEY, QN_SECRET_KEY);
        	String au=  auth.uploadToken(QN_BUCKETNAME);
            //调用put方法上传
            Response res = uploadManager.put(inputStream, url, au, null, null);


            System.out.println(res.bodyString());
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(res.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            realPath=putRet.key.toString();
            return realPath;
            //打印返回的信息
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());
            return realPath;
        }
    }

    public static void main(String[] args) {
    	String realPath="";
    	  try {
//          	CommonsMultipartFile cf= (CommonsMultipartFile)myfile; //这个myfile是MultipartFile的
//  	        DiskFileItem fi = (DiskFileItem)cf.getFileItem();
//  	        File file = fi.getStoreLocation();
    		  File tempDirectory=new File("C:\\Users\\Administrator\\Desktop\\app-release_cient.apk");
          	Configuration cfg = new Configuration(Zone.zone2());
          	UploadManager uploadManager = new UploadManager(cfg);
          	//获取七牛token
              Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
          	String au=  auth.uploadToken(bucketname);
              //调用put方法上传
              Response res = uploadManager.put(tempDirectory, "yiweibao-app-customer-001.apk", au);


              System.out.println(res.bodyString());
              //解析上传成功的结果
              DefaultPutRet putRet = new Gson().fromJson(res.bodyString(), DefaultPutRet.class);
              System.out.println(putRet.key);
              realPath=putRet.key.toString();
              System.out.println(realPath);
              //打印返回的信息
          } catch (QiniuException e) {
              Response r = e.response;
              // 请求失败时打印的异常的信息
              System.out.println(r.toString());
              try {
                  //响应的文本信息
                  System.out.println(r.bodyString());
              } catch (QiniuException e1) {
                  //ignore
              }
//              return realPath;
          }
	}


}