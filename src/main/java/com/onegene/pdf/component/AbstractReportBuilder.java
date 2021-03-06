package com.onegene.pdf.component;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.onegene.pdf.component.entity.PrintReportBean;
import com.onegene.pdf.util.Precondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author: laoliangliang
 * @description:
 * @create: 2020/5/19 17:40
 **/
@Slf4j
public abstract class AbstractReportBuilder implements IReportBuilder {

    protected PdfWriter writer;
    protected PdfDocument pdf;
    protected Document doc;
    protected PdfFont font;
    protected String outPath;
    @Setter
    @Getter
    protected Integer part = 0;
    protected String fontPath;
    protected PrintReportBean reportBean;
    protected ConverterProperties proper;
    protected Map<ExtraParam.CatalogType, java.util.List<CataLog>> cataLogsMap = new LinkedHashMap<>();
    protected Properties properties = new Properties();
    protected Set<Integer> pageSet = new HashSet<>();

    public void setPrintReportBean(PrintReportBean printReportBean) {
        this.reportBean = printReportBean;
    }

    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
        File file = new File(fontPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void initPdf(String outPath, Integer part) throws IOException {
        this.part = part;
        initPdf(outPath);
    }

    public void initPdf(String outPath) {
        this.generateDirectory(outPath);
        this.outPath = outPath;
        String inPath = outPath;
        if (part <= 0 || part > 20) {
            inPath = getInPath();
        }
        try {
            writer = new PdfWriter(new File(inPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(PageSize.A4);
        pdf.getDefaultPageSize().applyMargins(0, 0, 0, 0, true);

//        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", true);
        try {
            if (fontPath == null) {
                font = PdfFontFactory.createFont(AbstractReportBuilder.class.getClassLoader().getResource("font/SourceHanSansCN-Regular.ttf").getPath(), PdfEncodings.IDENTITY_H, true);
            } else {
                font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc = new Document(pdf);
        doc.setMargins(50, 60, 50, 60);
        doc.setFont(font);
        doc.setFontSize(10.5f);
        doc.setCharacterSpacing(0.1f);

        proper = new ConverterProperties();
        //字体设置，解决中文不显示问题
        FontSet fontSet = new FontSet();
        if (fontPath == null) {
            fontSet.addFont(AbstractReportBuilder.class.getClassLoader().getResource("font/SourceHanSansCN-Regular.ttf").getPath(), PdfEncodings.IDENTITY_H);
        } else {
            fontSet.addFont(fontPath, PdfEncodings.IDENTITY_H);
        }
        FontProvider fontProvider = new FontProvider(fontSet);
        proper.setFontProvider(fontProvider);
    }

    private void generateDirectory(String outPath) {
        String directory = outPath.substring(0, outPath.lastIndexOf("/"));
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    protected String getInPath() {
        int index = outPath.lastIndexOf("/");
        int index2 = outPath.lastIndexOf("/", index-1);
        String prefix = outPath.substring(0, index2);
        String fileName = outPath.substring(index);
        String name = fileName.split("\\.")[0];
        String pre = prefix + "/temp";
        if (!Files.exists(Paths.get(pre))) {
            try {
                Files.createDirectories(Paths.get(pre));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pre + name + "_temp.pdf";
    }

    /**
     * 通过代理调用
     */
    @Override
    public void invokePartProxy(PrintReportBean data){}

    @Data
    @AllArgsConstructor
    protected class CataLog {
        private Integer index;
        private String categoryName;
        private String name;
        private String label;
        private Integer pageNumber;
        private ExtraParam extraParam;
    }


    @Data
    public static class ExtraParam {
        public enum CatalogType {
            /**
             * 目录类别 1-需要注意 2-正常项目
             */
            ATTENTION(1), NORMAL(2);
            private Integer val;

            CatalogType(Integer val) {
                this.val = val;
            }

            public Integer val() {
                return val;
            }
        }

        /**
         * 目录类别 1-需要注意 2-正常项目
         */
        private CatalogType type;

        public ExtraParam(CatalogType type) {
            this.type = type;
        }
    }

    /**
     * 一键构建
     *
     * @return
     */
    public IReportBuilder buildAll(PrintReportBean data) {
        this.reportBean = data;
        Precondition.checkNotNull(reportBean, "报告数据为空");
        addIndex();
        addHello();
        addExaminee();
        addDetectionContent();
        addResultSummary();
        addContext();
        addThanks();
        addBackCover();
        addCatalog();
        addPageNumber();
        build();
        return this;
    }

    /**
     * 添加首页图片
     */
    @Override
    public abstract IReportBuilder addIndex();

    /**
     * say hello
     */
    @Override
    public abstract IReportBuilder addHello();

    /**
     * 添加受检人信息
     */
    @Override
    public abstract IReportBuilder addExaminee();

    public void build() {
        log.info("输出目录：" + outPath);
        pdf.close();
    }

    /**
     * 添加检测内容
     */
    @Override
    public abstract IReportBuilder addDetectionContent();

    /**
     * 检测结果概况
     */
    @Override
    public abstract IReportBuilder addResultSummary();

    /**
     * 添加正文
     */
    @Override
    public abstract IReportBuilder addContext();

    /**
     * 结束语
     */
    @Override
    public abstract IReportBuilder addThanks();

    /**
     * 封底
     */
    @Override
    public abstract IReportBuilder addBackCover();

    /**
     * 目录
     */
    @Override
    public abstract IReportBuilder addCatalog();

    /**
     * 页码
     */
    @Override
    public abstract IReportBuilder addPageNumber();

}
