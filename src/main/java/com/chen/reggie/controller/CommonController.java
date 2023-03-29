package com.chen.reggie.controller;

import com.chen.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求结束后，文件则会自动删除
        log.info(file.toString());
        //使用uuid 随机生成一个文件名，防止相同文件名进行了覆盖
        //先得到原始名
        String originalFilename = file.getOriginalFilename();
        //进行截取文件后缀
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用uuid随机生成名字，并且将后缀名进行拼接
        String s = UUID.randomUUID().toString() + substring;
        //需要创建一个目录对象
        File dir=new File(basePath);
        //进行判断，如果不存在这个目录则需要创建一个目录
        if(!dir.exists()){
            //目录不存在，需要创建一个目录
            dir.mkdir();
        }
        try {

            file.transferTo(new File(basePath+s));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //返回这个文件名称即可
        return R.success(s);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //通过输入流读取文件内容
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
            //通过输出流，将文件写回浏览器，图片在浏览器中显示
            ServletOutputStream outputStream = response.getOutputStream();
            //响应文件的格式
            response.setContentType("image/jpeg");
            //合理运用输入输出流
            int len=0;
            byte[] bytes=new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
             outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭输入输出流
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
