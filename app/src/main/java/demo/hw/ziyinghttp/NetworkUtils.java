package demo.hw.ziyinghttp;


import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * 网络请求工具类型
 */

public class NetworkUtils {
    public static final String TAG = "NetworkUtils";

    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";

    /**
     * Timeout (in ms) we specify for each http request
     */
    public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;

    private static String sUserAgent = null;

    public enum HttpMethod {
        GET, // Get方法
        POST, // Post方法,UrlForm方式
        POST_JSON, // Post方法,JSON方式
        DELETE
    }

    public static String requestFromNetwork(String url, Map<String, String> params, String requestMethod) {
        String result = "";
        try {
            URL mUrl = new URL(url);
            if (mUrl.getProtocol().toLowerCase().equals("https")) {
                result = httpsRequest(url, null, buildParam(params), requestMethod);
            } else if (mUrl.getProtocol().toLowerCase().equals("http")) {
                result = httpRequest(url, null, buildParam(params), requestMethod);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.toString(), e);
        }

        return result;
    }

    public static String requestFromNetwork(String url, Map<String, String> headers, String data, String requestMethod) {
        String result = "";
        try {
            URL mUrl = new URL(url);
            if (mUrl.getProtocol().toLowerCase().equals("https")) {
                result = httpsRequest(url, headers, data, requestMethod);
            } else if (mUrl.getProtocol().toLowerCase().equals("http")) {
                result = httpRequest(url, headers, data, requestMethod);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.toString(), e);
        }

        return result;
    }

    private static String httpRequest(String url, Map<String, String> headers, String data, String requestMethod) {
        StringBuffer stringBuffer = null;
        String result = "";
        long execStartTime = System.currentTimeMillis();
        try {
            HttpURLConnection connection;
            if (requestMethod.equalsIgnoreCase(REQUEST_METHOD_GET)) {
                if (!TextUtils.isEmpty(data)) {
                    url = url + "?" + data;
                }
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod(REQUEST_METHOD_GET);
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                putHeaders(headers, connection);
                connection.setDoInput(true);
            } else {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod(REQUEST_METHOD_POST);
                connection.setRequestProperty("accept", "*/*");
                connection.setRequestProperty("connection", "Keep-Alive");
                connection.setRequestProperty("user-agent", getUserAgent());
                putHeaders(headers, connection);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data == null ? "".getBytes() : data.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            stringBuffer = new StringBuffer();
            String readLine = new String();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            while ((readLine = bufferedReader.readLine()) != null) {
                stringBuffer.append(readLine).append("\n");
            }
            bufferedReader.close();
            long timeCost = System.currentTimeMillis() - execStartTime;
            long size = stringBuffer.toString().getBytes().length;
            int statusCode = connection.getResponseCode();
        } catch (IOException e) {
            long timeCost = System.currentTimeMillis() - execStartTime;
            Log.e(TAG, "IOException: " + e.toString(), e);
        }
        if (null != stringBuffer) {
            result = stringBuffer.toString();
        }
        return result;
    }


    private static String httpsRequest(String url, Map<String, String> headers, String data, String requestMethod) {
        StringBuffer stringBuffer = null;
        String result = "";
        long execStartTime = System.currentTimeMillis();
        try {
            HttpsURLConnection connection;
            if (requestMethod.equalsIgnoreCase(REQUEST_METHOD_GET)) {
                if (!TextUtils.isEmpty(data)) {
                    url = url + "?" + data;
                }
                connection = (HttpsURLConnection) new URL(url).openConnection();
                connection.setRequestMethod(REQUEST_METHOD_GET);
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                putHeaders(headers, connection);
                connection.setDoInput(true);
            } else {
                connection = (HttpsURLConnection) new URL(url).openConnection();
                connection.setRequestMethod(REQUEST_METHOD_POST);
                connection.setRequestProperty("accept", "*/*");
                connection.setRequestProperty("connection", "Keep-Alive");
                connection.setRequestProperty("user-agent", getUserAgent());
                putHeaders(headers, connection);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data == null ? "".getBytes() : data.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            stringBuffer = new StringBuffer();
            String readLine = new String();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            while ((readLine = bufferedReader.readLine()) != null) {
                stringBuffer.append(readLine).append("\n");
            }
            bufferedReader.close();
            long timeCost = System.currentTimeMillis() - execStartTime;
            long size = stringBuffer.toString().getBytes().length;
            int statusCode = connection.getResponseCode();
        } catch (IOException e) {
            long timeCost = System.currentTimeMillis() - execStartTime;
            Log.e(TAG, "IOException: " + e.toString(), e);
        }
        if (null != stringBuffer) {
            result = stringBuffer.toString();
        }
        return result;
    }

    public static String buildParam(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        } else {
            String result = "";
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                result = result + entry.getKey() + "=" + entry.getValue() + "&";
            }
            result = result.substring(0, result.length() - 1);
            Log.d(TAG, "buildParam:" + result);
            return result;
        }
    }

    private static void putHeaders(Map<String, String> headers, HttpURLConnection connection) {
        if (headers != null && !headers.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    public static String MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }

    public static String getUserAgent() {
        synchronized (NetworkUtils.class) {
            if (sUserAgent == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(Build.MODEL);
                sb.append("; MIUI/");
                sb.append(Build.VERSION.INCREMENTAL);
                try {
                    Class<?> buildClass = Class.forName("miui.os.Build");
                    Field isAlphaField = buildClass.getField("IS_ALPHA_BUILD");
                    boolean isAlphaBuild = (Boolean) (isAlphaField.get(null));
                    if (isAlphaBuild) {
                        sb.append(' ');
                        sb.append("ALPHA");
                    }
                } catch (ClassNotFoundException e) {
                    Log.d(TAG, "Not in MIUI in getUserAgent");
                } catch (NoSuchFieldException e) {
                    Log.d(TAG, "Not in MIUI in getUserAgent");
                } catch (IllegalAccessException e) {
                    Log.d(TAG, "Not in MIUI in getUserAgent");
                } catch (IllegalArgumentException e) {
                    Log.d(TAG, "Not in MIUI in getUserAgent");
                }
                sUserAgent = sb.toString();
            }
            return sUserAgent;
        }
    }
}
