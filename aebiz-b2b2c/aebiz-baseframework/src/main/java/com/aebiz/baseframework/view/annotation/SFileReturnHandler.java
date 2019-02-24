package com.aebiz.baseframework.view.annotation;

import com.google.typography.font.tools.sfnttool.SfntTool;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.nutz.img.Images;
import org.nutz.lang.*;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.itextpdf.text.pdf.BaseFont;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2017/3/31.
 */
public class SFileReturnHandler implements HandlerMethodReturnValueHandler {
    private static final Log log = Logs.get();
    public static final boolean DISABLE_RANGE_DOWNLOAD = false; // 禁用断点续传
    private static final int big4G = Integer.MAX_VALUE;
    protected String contentType;
    protected static byte[] defaultFontData;

    public static void setDefaultFontPath(String path) {
        if (path == null)
            defaultFontData = null;
        else
            defaultFontData = Files.readBytes(path);
    }

    static {
        String[] paths = new String[]{
                "fonts/pdf.ttc",
                "fonts/pdf.ttf",
                "C:\\windows\\fonts\\msyhl.ttc",
                "/usr/share/fonts/msyhl.ttc",
                "/System/Library/Fonts/msyhl.ttc"
        };
        for (String path : paths) {
            try {
                if (new File(path).exists()) {
                    setDefaultFontPath(path);
                    log.debug("微软雅黑Light found");
                    break;
                }

            } catch (Exception e) {
            }
        }
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(SFile.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleReturnValue(Object obj, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        SFile myContentType = returnType.getMethodAnnotation(SFile.class);
        if (myContentType == null || Strings.isEmpty(myContentType.value()))
            return;
        this.contentType = Strings.sNull(contentTypeMap.get(myContentType.value().toLowerCase()), contentType);
        // 如果用户自行设置了,那就不要再设置了!
        if (response.getContentType() == null) {
            if (!Lang.isAndroid
                    && obj != null
                    && obj instanceof BufferedImage
                    && "text/plain".equals(contentType)) {
                contentType = contentTypeMap.get("png");
            }
            response.setContentType(contentType);
        }
        if (obj == null)
            return;
        if (!Lang.isAndroid && obj instanceof BufferedImage) {
            OutputStream out = response.getOutputStream();
            if (contentType.contains("png"))
                ImageIO.write((BufferedImage) obj, "png", out);
                // @see
                // https://code.google.com/p/webm/source/browse/java/src/main/java/com/google/imageio/?repo=libwebp&name=sandbox%2Fpepijnve%2Fwebp-imageio#imageio%2Fwebp
            else if (contentType.contains("webp"))
                ImageIO.write((BufferedImage) obj, "webp", out);
            else
                Images.writeJpeg((BufferedImage) obj, out, 0.8f);
        } else if (obj instanceof File) {
            File file = (File) obj;
            long fileSz = file.length();
            if (log.isDebugEnabled())
                log.debug("File downloading ... " + file.getAbsolutePath());
            if (!file.exists() || file.isDirectory()) {
                log.debug("File downloading ... Not Exist : " + file.getAbsolutePath());
                response.sendError(404);
                return;
            }
            if (!response.containsHeader("Content-Disposition")) {
                String filename = URLEncoder.encode(file.getName(), Encoding.UTF8);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            }

            String rangeStr = request.getHeader("Range");
            OutputStream out = response.getOutputStream();
            if (DISABLE_RANGE_DOWNLOAD
                    || fileSz == 0
                    || (rangeStr == null || !rangeStr.startsWith("bytes=") || rangeStr.length() < "bytes=1".length())) {
                response.setHeader("Content-Length", "" + fileSz);
                Streams.writeAndClose(out, Streams.fileIn(file));
            } else {
                // log.debug("Range Download : " + req.getHeader("Range"));
                List<RangeRange> rs = new ArrayList<RangeRange>();
                if (!parseRange(rangeStr, rs, fileSz)) {
                    response.setStatus(416);
                    return;
                }
                // 暂时只实现了单range
                if (rs.size() != 1) {
                    // TODO 完成多range的下载
                    log.info("multipart/byteranges is NOT support yet");
                    response.setStatus(416);
                    return;
                }
                long totolSize = 0;
                for (RangeRange rangeRange : rs) {
                    totolSize += (rangeRange.end - rangeRange.start);
                }
                response.setStatus(206);
                response.setHeader("Content-Length", "" + totolSize);
                response.setHeader("Accept-Ranges", "bytes");

                // 暂时只有单range,so,简单起见吧
                RangeRange rangeRange = rs.get(0);
                response.setHeader("Content-Range", String.format("bytes %d-%d/%d",
                        rangeRange.start,
                        rangeRange.end - 1,
                        fileSz));
                writeFileRange(file, out, rangeRange);
            }
        }
        // 字节数组
        else if (obj instanceof byte[]) {
            response.setHeader("Content-Length", "" + ((byte[]) obj).length);
            OutputStream out = response.getOutputStream();
            Streams.writeAndClose(out, (byte[]) obj);
        }
        // 字符数组
        else if (obj instanceof char[]) {
            Writer writer = response.getWriter();
            writer.write((char[]) obj);
            writer.flush();
        }
        // 文本流
        else if (obj instanceof Reader) {
            Streams.writeAndClose(response.getWriter(), (Reader) obj);
        }
        // 二进制流
        else if (obj instanceof InputStream) {
            OutputStream out = response.getOutputStream();
            Streams.writeAndClose(out, (InputStream) obj);
        }
        // 二进制流
        else if (obj instanceof Context) {
            if ("pdf".equalsIgnoreCase(myContentType.value())) {
                Context cnt = (Context) obj;
                InputStream ins;
                File f = Files.findFile(cnt.getString("tpl","one.pdf"));
                if (f == null) {
                    List<NutResource> resources = Scans.me().scan(cnt.getString("tpl","one.pdf"));
                    if (resources.isEmpty()) {
                        log.info("pdf tmpl not found --> " + cnt.getString("tpl","one.pdf"));
                        response.sendError(404);
                        return;
                    }
                    ins = resources.get(0).getInputStream();
                } else {
                    ins = Streams.fileIn(f);
                }
                try {

                    if (!response.containsHeader("Content-Disposition") && !cnt.getBoolean("*viewOnly")) {
                        String filename = URLEncoder.encode(cnt.getString("filename", "out.pdf"),
                                Encoding.UTF8);
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                    }
                    PdfReader reader = new PdfReader(ins);
                    OutputStream out = response.getOutputStream();
                    PdfStamper ps = new PdfStamper(reader, out);
                    AcroFields fields = ps.getAcroFields();
                    StringBuilder sb = new StringBuilder();
                    for (String key : fields.getFields().keySet()) {
                        sb.append(Strings.sBlank(cnt.get(key)));
                    }
                    BaseFont bf = subFont(sb.toString());
                    for (String key : fields.getFields().keySet()) {
                        fields.setField(key, Strings.sBlank(cnt.get(key)));
                        if (bf != null)
                            fields.setFieldProperty(key, "textfont", bf, null);
                    }
                    ps.setFormFlattening(true);
                    Callback<PdfStamper> callback = cnt.getAs(Callback.class, "*callback");
                    if (callback != null)
                        callback.invoke(ps);
                    ps.close();
                } finally {
                    Streams.safeClose(ins);
                }
            }
        }
        // 普通对象
        else {
            byte[] data = String.valueOf(obj).getBytes(Encoding.UTF8);
            response.setHeader("Content-Length", "" + data.length);
            OutputStream out = response.getOutputStream();
            Streams.writeAndClose(out, data);
        }
    }

    protected static final Map<String, String> contentTypeMap = new HashMap<String, String>();

    static {
        contentTypeMap.put("xml", "application/xml");
        contentTypeMap.put("html", "text/html");
        contentTypeMap.put("htm", "text/html");
        contentTypeMap.put("css", "text/css");
        contentTypeMap.put("stream", "application/octet-stream");
        contentTypeMap.put("js", "application/javascript");
        contentTypeMap.put("json", "application/json");
        contentTypeMap.put("jpg", "image/jpeg");
        contentTypeMap.put("jpeg", "image/jpeg");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("pdf", "application/pdf");
        contentTypeMap.put("webp", "image/webp");
    }

    public static void writeDownloadRange(DataInputStream in,
                                          OutputStream out,
                                          RangeRange rangeRange) {
        try {
            if (rangeRange.start > 0) {
                long start = rangeRange.start;
                while (start > 0) {
                    if (start > big4G) {
                        start -= big4G;
                        in.skipBytes(big4G);
                    } else {
                        in.skipBytes((int) start);
                        break;
                    }
                }
            }
            byte[] buf = new byte[8192];
            BufferedInputStream bin = new BufferedInputStream(in);
            long pos = rangeRange.start;
            int len = 0;
            while (pos < rangeRange.end) {
                if (rangeRange.end - pos > 8192) {
                    len = bin.read(buf);
                } else {
                    len = bin.read(buf, 0, (int) (rangeRange.end - pos));
                }
                if (len == -1) {// 有时候,非常巧合的,文件已经读取完,就悲剧开始了...
                    break;
                }
                if (len > 0) {
                    out.write(buf, 0, len);
                    pos += len;
                }
            }
            out.flush();
        } catch (Throwable e) {
            throw Lang.wrapThrow(e);
        }
    }

    public static void writeFileRange(File file, OutputStream out, RangeRange rangeRange) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fin);
            writeDownloadRange(in, out, rangeRange);
        } catch (Throwable e) {
            throw Lang.wrapThrow(e);
        } finally {
            Streams.safeClose(fin);
        }
    }

    public static class RangeRange {
        public RangeRange(long start, long end) {
            this.start = start;
            this.end = end;
        }

        long start;
        long end = -1;

    }

    public static final boolean parseRange(String rangeStr, List<RangeRange> rs, long maxSize) {
        rangeStr = rangeStr.substring("bytes=".length());
        String[] ranges = rangeStr.split(",");
        for (String range : ranges) {
            if (range == null || Strings.isBlank(range)) {
                log.debug("Bad Range -->    " + rangeStr);
                return false;
            }
            range = range.trim();
            try {
                // 首先是从后往前算的 bytes=-100 取最后100个字节
                if (range.startsWith("-")) {

                    // 注意,这里是负数
                    long end = Long.parseLong(range);
                    long start = maxSize + end;
                    if (start < 0) {
                        log.debug("Bad Range -->    " + rangeStr);
                        return false;
                    }
                    rs.add(new RangeRange(start, maxSize));
                    continue;
                }

                // 然后就是从开头到最后 bytes=1024-
                if (range.endsWith("-")) {
                    // 注意,这里是负数
                    long start = Long.parseLong(range.substring(0, range.length() - 1));
                    if (start < 0) {
                        log.debug("Bad Range -->    " + rangeStr);
                        return false;
                    }
                    rs.add(new RangeRange(start, maxSize));
                    continue;
                }

                // 哦也,是最标准的有头有尾?
                if (range.contains("-")) {
                    String[] tmp = range.split("-");
                    long start = Long.parseLong(tmp[0]);
                    long end = Long.parseLong(tmp[1]);
                    if (start > end) {
                        log.debug("Bad Range -->    " + rangeStr);
                        return false;
                    }
                    rs.add(new RangeRange(start, end + 1)); // 这里需要调查一下
                } else {
                    // 操!! 单个字节?!!
                    long start = Long.parseLong(range);
                    rs.add(new RangeRange(start, start + 1));
                }
            } catch (Throwable e) {
                log.debug("Bad Range -->    " + rangeStr, e);
                return false;
            }
        }
        return !rs.isEmpty();
    }

    public static BaseFont subFont(String strs) {
        try {
            byte[] buf = SfntTool.sub(defaultFontData, strs, false);
            return BaseFont.createFont("pdfview." + R.UU32() + ".ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, false, buf, null);
        } catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }
}
