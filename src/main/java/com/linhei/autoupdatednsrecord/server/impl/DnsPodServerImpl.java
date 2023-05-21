package com.linhei.autoupdatednsrecord.server.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.linhei.autoupdatednsrecord.entity.CreateRecord;
import com.linhei.autoupdatednsrecord.entity.Records;
import com.linhei.autoupdatednsrecord.entity.Record;
import com.linhei.autoupdatednsrecord.server.DnsPodServer;
import okhttp3.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 腾讯云DNSPod的API使用
 *
 * @author linhei
 */
public class DnsPodServerImpl implements DnsPodServer {
    /**
     * token
     * 返回格式
     * 返回语言
     * 域名
     * 正确访问返回code
     * 返回信息
     */
    final static String LOGIN_TOKEN = "dnsPod token";
    final static String FORMAT = "json";
    final static String LANG = "cn";
    final static String DOMAIN = "domain";
    final static String SUCCESSFUL = "\"code\": \"1\"";
    final static String MESSAGE = "message";


    OkHttpClient client = new OkHttpClient();
    Record record = new Record(LOGIN_TOKEN, FORMAT, LANG, DOMAIN);
    FormBody.Builder builder = new FormBody.Builder();

    /**
     * 将token和域名信息添加到 FormBody.Builder 中
     */
    private void setBuilder() {
        setParameter(JSON.parseObject(record.toJson()));
    }

    /**
     * 设置参数
     *
     * @param json jsonObject格式的记录值
     */
    private void setParameter(JSONObject json) {
        for (Map.Entry<String, Object> entry : json.entrySet())
            builder.add(entry.getKey(), String.valueOf(entry.getValue()));
    }


    @Override
    public List<Records> getRecordList() throws IOException {
        String json = getRecordListJson();
        if (json.contains(SUCCESSFUL)) return JSON.parseArray(json, Records.class);
        return new ArrayList<>();
    }

    @Override
    public String getRecordListJson() throws IOException {
        builder = new FormBody.Builder();
        // 将公共参数添加到请求体中
        setBuilder();

        // 获取返回值
        JSONObject jsonObject = getResponse("https://dnsapi.cn/Record.List");
        JSONObject status = (JSONObject) jsonObject.get("status");
        // 异常处理 若返回code不为1则说明执行出错；抛出运行异常。否则证明执行成功将 json 转换为 List<Records>
        if (isCode(status)) throw new RuntimeException(String.valueOf(status.get(MESSAGE)));
        else {
            return jsonObject.get("records").toString();
        }

    }


    @Override
    public String getRecordListJson(String keyword) throws IOException {
        this.record.setKeyword(keyword);
        return getRecordListJson();
    }

    @Override
    public List<Records> getRecordList(String keyword) throws IOException {
        record.setKeyword(keyword);
        return getRecordList();
    }


    @Override
    public String createRecord(CreateRecord createRecord) throws IOException {
        builder = new FormBody.Builder();
        setBuilder();
        setParameter(JSON.parseObject(createRecord.toJson()));
        JSONObject response = getResponse("https://dnsapi.cn/Record.Create");
        JSONObject status = (JSONObject) response.get("status");
        return getResponseId(response, status);
    }


    @Override
    public String removeRecord(String recordId) throws IOException {
        builder = new FormBody.Builder();
        setBuilder();
        builder.add("record_id", recordId);
        JSONObject response = (JSONObject) getResponse("https://dnsapi.cn/Record.Remove").get("status");
        return String.valueOf(response.get("message"));
    }

    @Override
    public String modifyRecord(Records record) throws IOException {
        return modifyRecordHelp(record.toJson());
    }

    @Override
    public String modifyRecord(List<Records> recordsList) throws IOException {
        StringBuilder res = new StringBuilder();
        for (Records records : recordsList) res.append(modifyRecord(records)).append("\n");
        return res.toString();
    }

    @Override
    public String modifyRecord(String json) throws IOException {
        if (json.contains("[")) {
            List<Records> list = JSON.parseArray(json, Records.class);
            return modifyRecord(list);
        } else return modifyRecordHelp(json);

    }

    /**
     * 修改记录的辅助方法
     *
     * @param record 记录的json
     * @return 修改完成的id
     * @throws IOException 访问异常
     */
    private String modifyRecordHelp(String record) throws IOException {
        builder = new FormBody.Builder();
        setParameter(JSON.parseObject(record));
        return getModifyRecordResponse();
    }

    /**
     * @return 记录的id
     * @throws IOException 访问异常
     */
    private String getModifyRecordResponse() throws IOException {
        setBuilder();
        JSONObject response = getResponse("https://dnsapi.cn/Record.Modify");
        JSONObject status = (JSONObject) response.get("status");
        return getResponseId(response, status);
    }

    /**
     * @param status 状态
     * @return 状态值是否为正确返回值
     */
    private static boolean isCode(JSONObject status) {
        return !"1".equals(status.get("code"));
    }

    /**
     * 获取返回的记录id
     *
     * @param response 响应
     * @param status   状态信息
     * @return 记录值的ID
     */
    private static String getResponseId(JSONObject response, JSONObject status) {
        if (isCode(status)) throw new RuntimeException(String.valueOf(status.get(MESSAGE)));
        else return String.valueOf(((JSONObject) response.get("record")).get("id"));
    }

    /**
     * 通用请求方法
     *
     * @param url 请求URL
     * @return 请求结果
     * @throws IOException 请求出错
     */
    public JSONObject getResponse(String url) throws IOException {
        // 创建请求
        Request req = new Request.Builder().url(url).post(builder.build()).build();

        // 执行请求
        Response res = client.newCall(req).execute();
        if (res.isSuccessful()) {
            ResponseBody body = res.body();
            if (body != null) {
                return JSON.parseObject(body.string());
            } else throw new RuntimeException(String.valueOf(res.code()));
        } else throw new RuntimeException(String.valueOf(res.code()));
    }
}
