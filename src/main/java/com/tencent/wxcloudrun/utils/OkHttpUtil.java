package com.tencent.wxcloudrun.utils;

import com.alibaba.fastjson2.JSON;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author yozora
 * description
 **/
public class OkHttpUtil {

    private static volatile OkHttpClient okHttpClient = null;

    /**
     * 用于异步请求时，控制访问线程数，返回结果,设置为0表示只能1个线程同时访问，用于async()方法调用
     */
    private static final Semaphore semaphore = new Semaphore(0);
    private Map<String, String> headerMap;
    private Map<String, String> paramMap;
    private String url;
    private Request.Builder request;

    /**
     * 初始化okHttpClient，并且允许https访问
     */
    private OkHttpUtil(boolean isProxy) {
        isProxy = false;
        if (okHttpClient == null) {
            synchronized (OkHttpUtil.class) {
                if (okHttpClient == null) {
                    TrustManager[] trustManagers = buildTrustManagers();
                    if (isProxy) {
                        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
                        okHttpClient = new OkHttpClient.Builder().proxyAuthenticator(new Authenticator() {
                            @Override
                            public Request authenticate(Route route, Response response) throws IOException {
                                //设置代理服务器账号密码
                                String credential = Credentials.basic("", "");
                                return response.request().newBuilder().header("Proxy-Authorization", credential).build();
                            }
                        }).proxy(proxy).connectTimeout(15, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).sslSocketFactory(createSSLSocketFactory(trustManagers), (X509TrustManager) trustManagers[0]).hostnameVerifier((hostName, session) -> true).retryOnConnectionFailure(true).connectionPool(new ConnectionPool(10, 10, TimeUnit.SECONDS)).build();
                    } else {
                        okHttpClient = new OkHttpClient.Builder()
                                .connectTimeout(15, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).sslSocketFactory(createSSLSocketFactory(trustManagers), (X509TrustManager) trustManagers[0]).hostnameVerifier((hostName, session) -> true).retryOnConnectionFailure(true).connectionPool(new ConnectionPool(10, 10, TimeUnit.SECONDS)).build();
                    }
                    addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");
                }
            }
        }
    }

    /**
     * description: create OkHttpUtil instance
     *
     * @param isProxy is proxy
     * @return aof.utils.OkHttpUtil
     * @author yozora
     **/
    public static OkHttpUtil builder(boolean isProxy) {
        return new OkHttpUtil(isProxy);
    }

    /**
     * description: set url
     *
     * @param url url
     * @return aof.utils.OkHttpUtil
     * @author yozora
     **/
    public OkHttpUtil url(String url) {
        this.url = url;
        return this;
    }

    /**
     * description: set url
     *
     * @param url    url
     * @param params json params
     * @return aof.utils.OkHttpUtil
     * @author yozora
     **/
    public OkHttpUtil url(String url, String... params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        for (String param : params) {
            urlBuilder.append("/").append(param);
        }
        url = urlBuilder.toString();
        this.url = url;
        return this;
    }


    /**
     * description: add params
     *
     * @param key   参数名
     * @param value 参数值
     * @return aof.utils.OkHttpUtil
     * @author yozora
     **/
    public OkHttpUtil addParam(String key, String value) {
        if (paramMap == null) {
            paramMap = new LinkedHashMap<>(16);
        }
        paramMap.put(key, value);
        return this;
    }

    /**
     * description: add headers
     *
     * @param key   参数名
     * @param value 参数值
     * @return aof.utils.OkHttpUtil
     * @author yozora
     **/
    public OkHttpUtil addHeader(String key, String value) {
        if (headerMap == null) {
            headerMap = new LinkedHashMap<>(16);
        }
        headerMap.put(key, value);
        return this;
    }

    /**
     * description: get request
     *
     * @return aof.utils.OkHttpUtil
     * @author yozora
     **/
    public OkHttpUtil get() {
        request = new Request.Builder().get();
        StringBuilder urlBuilder = new StringBuilder(url);
        if (paramMap != null) {
            urlBuilder.append("?");
            try {
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    urlBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        request.url(urlBuilder.toString());
        return this;
    }

    /**
     * description: post request
     *
     * @param isJsonPost {true等于json的方式提交数据,类似postman里post方法的raw false等于普通的表单提交}
     * @return aof.utils.OkHttpUtil
     * @author yozora
     **/
    public OkHttpUtil post(boolean isJsonPost) {
        RequestBody requestBody;
        if (isJsonPost) {
            String json = "";
            if (paramMap != null) {
                json = JSON.toJSONString(paramMap);
            }
            System.out.println(json);
            requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        } else {
            FormBody.Builder formBody = new FormBody.Builder();
            if (paramMap != null) {
                paramMap.forEach(formBody::add);
            }
            requestBody = formBody.build();
        }
        request = new Request.Builder().post(requestBody).url(url);
        return this;
    }

    /**
     * description:
     *
     * @param json json数据
     * @return aof.utils.OkHttpUtil
     * @author yozora
     **/
    public OkHttpUtil postJson(String json) {
        // post请求
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        request = new Request.Builder()
                .post(RequestBody.create(json, mediaType)).url(url);
        return this;
    }

    /**
     * description: sync request
     *
     * @return java.lang.String
     * @author yozora
     **/
    public String sync() {
        setHeader(request);
        try {
            Response response = okHttpClient.newCall(request.build()).execute();
            assert response.body() != null;
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "request fail: " + e.getMessage();
        }
    }

    /**
     * description: async request
     *
     * @return java.lang.String 有返回值,因为使用锁，相当于同步请求
     * @author yozora
     **/
    public String async() {
        StringBuilder buffer = new StringBuilder();
        setHeader(request);
        okHttpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                semaphore.release();
                buffer.append("request fail: ").append(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                semaphore.release();
                assert response.body() != null;
                buffer.append(response.body().string());
            }
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * description: async request
     *
     * @param callback aof.utils.OkHttpUtil.Callback
     * @author yozora
     **/
    public void async(Callback callback) {
        setHeader(request);
        okHttpClient.newCall(request.build()).enqueue(callback);
    }

    /**
     * description: set header
     *
     * @param request com.squareup.okhttp.Request
     * @author yozora
     **/
    private void setHeader(Request.Builder request) {
        if (headerMap != null) {
            try {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * description: 生成安全套接字工厂，用于https请求的证书跳过
     *
     * @param trustAllCerts 是否信任所有证书
     * @return javax.net.ssl.SSLSocketFactory
     * @author yozora
     **/
    private static SSLSocketFactory createSSLSocketFactory(TrustManager[] trustAllCerts) {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }};
    }

    /**
     * 自定义一个接口回调
     */
    public interface ICallBack {

        void onSuccessful(Call call, String data);

        void onFailure(Call call, String errorMsg);

    }

}
