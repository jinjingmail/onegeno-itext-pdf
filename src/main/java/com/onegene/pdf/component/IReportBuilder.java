package com.onegene.pdf.component;

import com.onegene.pdf.component.entity.PrintReportBean;

/**
 * @author: laoliangliang
 * @description:
 * @create: 2020/5/29 11:20
 **/
public interface IReportBuilder {

    /**
     * 通过代理调用
     */
    void invokePartProxy(PrintReportBean data);

    /**
     * 添加首页图片
     */
    AbstractReportBuilder addIndex();

    /**
     * say hello
     */
    AbstractReportBuilder addHello();

    /**
     * 添加受检人信息
     */
    AbstractReportBuilder addExaminee();

    /**
     * 添加检测内容
     */
    GenoReportBuilder addDetectionContent();

    /**
     * 检测结果概况
     */
    GenoReportBuilder addResultSummary();

    /**
     * 添加正文
     */
    GenoReportBuilder addContext();

    /**
     * 结束语
     */
    GenoReportBuilder addThanks();

    /**
     * 封底
     */
    GenoReportBuilder addBackCover();

    /**
     * 目录
     */
    GenoReportBuilder addCatalog();

    /**
     * 页码
     */
    GenoReportBuilder addPageNumber();
}
