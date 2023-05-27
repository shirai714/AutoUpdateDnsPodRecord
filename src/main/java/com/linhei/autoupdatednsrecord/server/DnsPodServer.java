package com.linhei.autoupdatednsrecord.server;

import com.linhei.autoupdatednsrecord.entity.CreateRecord;
import com.linhei.autoupdatednsrecord.entity.Records;

import java.io.IOException;
import java.util.List;

/**
 * @author linhei
 */
public interface DnsPodServer {

    /**
     * 获取DnsPod的域名记录列表
     *
     * @return RecordList类型的记录 用List区分每一个记录
     * @throws IOException 访问出错
     */
    List<Records> getRecordList() throws IOException;

    /**
     * 获取DnsPod的域名记录列表
     *
     * @return 以Json格式返回
     * @throws IOException 访问出错
     */
    String getRecordListJson() throws IOException;

    /**
     * 查询带参数的请求
     *
     * @param keyword 关键字
     * @return JSON的返回
     * @throws IOException 访问出错
     */
    String getRecordListJson(String keyword) throws IOException;

    /**
     * 查询单个记录的带参数的请求
     *
     * @param subDomain    子域名
     * @param recordType   记录类型
     * @param recordLine   记录线路
     * @param recordLineId 记录类型ID
     * @return JSON的返回
     * @throws IOException 访问出错
     */
    String getRecordJson(String subDomain, String recordType, String recordLine, Integer recordLineId) throws IOException;

    /**
     * 查询单个记录的带参数的请求
     *
     * @param subDomain  子域名
     * @param recordType 记录类型
     * @param recordLine 记录线路
     * @return JSON的返回
     * @throws IOException 访问出错
     */
    String getRecordJson(String subDomain, String recordType, String recordLine) throws IOException;

    /**
     * 查询单个记录的带参数的请求
     *
     * @param subDomain    子域名
     * @param recordType   记录类型
     * @param recordLineId 记录线路ID
     * @return JSON的返回
     * @throws IOException 访问出错
     */
    String getRecordJson(String subDomain, String recordType, Integer recordLineId) throws IOException;

    /**
     * 查询单个记录的带参数的请求
     *
     * @param subDomain  子域名
     * @param recordType 记录类型
     * @return JSON的返回
     * @throws IOException 访问出错
     */
    String getRecordJson(String subDomain, String recordType) throws IOException;

    /**
     * 查询单个记录的带参数的请求
     *
     * @param subDomain 子域名
     * @return JSON的返回
     * @throws IOException 访问出错
     */
    String getRecordJson(String subDomain) throws IOException;

    /**
     * 查询带参数的请求
     *
     * @param keyword 关键字
     * @return RecordList类型的记录 用List区分每一个记录
     * @throws IOException 访问出错
     */
    List<Records> getRecordList(String keyword) throws IOException;

    /**
     * 为DnsPod添加记录
     *
     * @param createRecord 记录实体类
     * @return 操作结果
     * @throws IOException 访问出错
     */
    String createRecord(CreateRecord createRecord) throws IOException;


    /**
     * 删除记录
     *
     * @param recordId 记录ID
     * @return 删除结果
     * @throws IOException 访问出错
     */
    String removeRecord(String recordId) throws IOException;

    /**
     * 根据Records修改记录
     *
     * @param record 记录对象
     * @return 修改结果
     * @throws IOException 访问出错
     */
    public String modifyRecord(Records record) throws IOException;

    /**
     * 根据List<Records>批量修改记录
     *
     * @param recordsList 记录列表
     * @return 修改结果
     * @throws IOException 访问出错
     */
    String modifyRecord(List<Records> recordsList) throws IOException;


    /**
     * 根据json修改记录
     *
     * @param json records格式的json
     * @return 修改结果
     * @throws IOException 访问出错
     */
    String modifyRecord(String json) throws IOException;

    /**
     * 获取当前网络的公网IP
     *
     * @return 公网IP
     * @throws IOException 访问出错
     */
    String getPublicIp() throws IOException;
}
