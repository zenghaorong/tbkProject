package com.aebiz.app.web.commons.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ComposeImageTest {



    /**
     * 生成海报主逻辑
     * @param storeId
     * @param accountId
     * @return
     * @throws Exception
     */
    public static String generatePosterImg(String storeId,String accountId,String myUrl,String sysConf) throws Exception {

        JSONObject jsonObject = JSON.parseObject(sysConf);

        String filePath = "/data/hb/";
//        String filePath = "D:\\海康启动目录\\";
        String hbImgName = storeId+accountId+"hbImgName.png";

        //生成二维码逻辑,获取保存在本地的二维码路径
        String codeImgPath = MatrixToImageWriter.code(myUrl,filePath,storeId,accountId);
        //主图片url地址
        String mainImgUrl = jsonObject.getString("mainImg");
        int x = jsonObject.getInteger("x");
        int y = jsonObject.getInteger("y");
        InputStream imagein =  MatrixToImageWriter.getImageStream(mainImgUrl);
        InputStream imagein2 = new FileInputStream(codeImgPath);
        BufferedImage image = ImageIO.read(imagein);
        BufferedImage image2 = ImageIO.read(imagein2);
        Graphics g = image.getGraphics();
        g.drawImage(image2, x, y,
                image2.getWidth(), image2.getHeight(), null);

        OutputStream outImage = new FileOutputStream(filePath + hbImgName);
//        String formatName = dstName.substring(dstName.lastIndexOf(".") + 1);
//        ImageIO.write(image, /*"GIF"*/ formatName /* format desired */ , new File("custom" + j + "-" + i + ".jpg") /* target */ );
        //生成图片
        ImageIO.write(image, "png", outImage);
        imagein.close();
        imagein2.close();
        outImage.close();
        return hbImgName;
    }


    /**
     * 生成海报主逻辑-本地测试
     * @param storeId
     * @param accountId
     * @param mainImgUrl 主图片url地址
     * @return
     * @throws Exception
     */
    public static String generatePosterImgBdTest(String storeId,String accountId,String mainImgUrl,String myUrl) throws Exception {
//        String filePath = "/data/hb/";
        String filePath = "D:\\海康启动目录\\";
        String hbImgName = storeId+accountId+"hbImgName.png";

        //生成二维码逻辑,获取保存在本地的二维码路径
        String codeImgPath = MatrixToImageWriter.code(myUrl,filePath,storeId,accountId);


        InputStream imagein =  MatrixToImageWriter.getImageStream(mainImgUrl);
        InputStream imagein2 = new FileInputStream(codeImgPath);
        BufferedImage image = ImageIO.read(imagein);
        BufferedImage image2 = ImageIO.read(imagein2);
        Graphics g = image.getGraphics();
        g.drawImage(image2, 310, 480,
                image2.getWidth(), image2.getHeight(), null);

        OutputStream outImage = new FileOutputStream(filePath + hbImgName);
//        String formatName = dstName.substring(dstName.lastIndexOf(".") + 1);
//        ImageIO.write(image, /*"GIF"*/ formatName /* format desired */ , new File("custom" + j + "-" + i + ".jpg") /* target */ );

        //生成图片
        ImageIO.write(image, "png", outImage);
        imagein.close();
        imagein2.close();
        outImage.close();
        return hbImgName;
    }


    /**
     * 图片合成
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {


        generatePosterImgBdTest("1000007","dsf23kdl43kf3sa990232c",
                "http://120.92.153.163/group1/M00/00/00/CgADA16BuamAYQNdAAHJev3IZYc740.jpg"
                ,"http://dazhela.net.cn/activity/index.html?storeId=2017060000000001&sourceAccountId=315da0ca358d44cd9a8c4bd376590144");

    }
}
