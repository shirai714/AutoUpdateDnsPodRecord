package com.linhei.autoupdatednsrecord.entity;

/**
 * @author linhei
 */
public class RecordType {
    public static final String A;
    public static final String CNAME;
    public static final String MX;
    public static final String TXT;
    public static final String AAAA;
    public static final String NS;
    public static final String CAA;
    public static final String SRV;
    public static final String HTTPS;
    public static final String SVCB;
    public static final String SPF;


    static {
        A = "A";
        CNAME = "CNAME";
        MX = "MX";
        TXT = "TXT";
        AAAA = "AAAA";
        NS = "NS";
        CAA = "CAA";
        SRV = "SRV";
        HTTPS = "HTTPS";
        SVCB = "SVCB";
        SPF = "SPF";
    }
}
