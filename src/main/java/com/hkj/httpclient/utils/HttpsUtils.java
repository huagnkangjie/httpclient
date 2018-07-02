package com.hkj.httpclient.utils;

import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpsUtils {  
    static CloseableHttpClient httpClient;
    static CloseableHttpResponse httpResponse;  
  
    public static CloseableHttpClient createSSLClientDefault() {  
        try {  
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {  
                // 信任所有  
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;  
                }  
            }).build();  
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;  
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);  
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();  
        } catch (KeyManagementException e) {  
            e.printStackTrace();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (KeyStoreException e) {  
            e.printStackTrace();  
        }  
        return HttpClients.createDefault();  
  
    }  
  
    /**  
     * 发送https请求  
     *   
     * @throws Exception
     */  
    public static String sendByHttp(JSONObject json, String url, String access_key) {
        try {  
            HttpPost httpPost = new HttpPost(url);  
            StringEntity entity = new StringEntity(json.toString(),"utf-8");//解决中文乱码问题    
            entity.setContentType("application/json");
            entity.setContentEncoding("UTF-8");    
            httpPost.setEntity(entity);
            httpPost.addHeader("access_key",access_key);
            httpPost.addHeader("Content-Type", "application/json");
            httpClient = HttpsUtils.createSSLClientDefault();  
            httpResponse = httpClient.execute(httpPost);  
            HttpEntity httpEntity = httpResponse.getEntity();  
            if (httpEntity != null) {  
                String jsObject = EntityUtils.toString(httpEntity, "UTF-8");  
                return jsObject;  
            } else {  
                return null;  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        } finally {  
            try {  
                httpResponse.close();  
                httpClient.close();  
                System.out.println("请求流关闭完成");
            } catch (IOException e) {  
                System.out.println("请求流关闭出错");
                e.printStackTrace();  
            }  
        }  
    }

    public static void send(String openid, String token){
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("openid", openid);
        jsonParam.put("score", 1);
        jsonParam.put("score_type", "A");
        jsonParam.put("oper_type", 0);
        jsonParam.put("token", token);
        System.out.println(jsonParam);
        String presp = HttpsUtils.sendByHttp(jsonParam, "http://127.0.0.1:7459/score/third/consume/nocheck","a993ba5646fe4a25a8d347b4c0f430fb");
        System.out.println(presp);
        JSONObject jsonobject = JSONObject.fromObject(presp);
        String code = jsonobject.get("code").toString();
        System.out.println(code);
    }
  
    public static void main(String[] args) throws Exception {
        for(int i = 1; i < 80;i++) {
            try {

                send("bb17",i+"-1");
                System.out.println(">>>>>>>>>>>>> i = " + i);
                //Thread.sleep(1000);
            } catch (Exception e){

            }

        }

    }  
   
    
}  
