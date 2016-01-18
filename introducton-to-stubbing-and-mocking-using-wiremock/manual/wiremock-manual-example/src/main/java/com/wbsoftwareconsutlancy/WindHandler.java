package com.wbsoftwareconsutlancy;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

import static java.lang.String.format;
import static javax.servlet.http.HttpServletResponse.SC_OK;

class WindHandler extends AbstractHandler {
    public static final String LONDON_LATITUDE = "51.507253";
    public static final String LONDON_LONGITUDE = "-0.127755";
    private final Properties properties;

    public WindHandler(Properties properties) {
        this.properties = properties;
    }

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
            ServletException {
        if ("/wind-speed".equals(target)) {
            try {
                String windSpeed = parseWindSpeed(forecastIoFor(LONDON_LATITUDE, LONDON_LONGITUDE)) + "mph";

                response.setContentType("text/html; charset=utf-8");
                response.setStatus(SC_OK);
                response.getWriter().print(windSpeed);

                baseRequest.setHandled(true);
            } catch (JSONException e) {
                throw new ServletException(e);
            }
        }
    }

    private String parseWindSpeed(String forecastIo) throws JSONException {
        return new JSONObject(forecastIo).getJSONObject("currently").getString("windSpeed");
    }

    private String forecastIoFor(String latitude, String longitude) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(format(getForecastIoUrl() + "/%s,%s", latitude, longitude));
            httpget.addHeader("accept-encoding", "identity");
            System.out.println("Executing request " + httpget.getRequestLine());

            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            return responseBody;
        }
    }

    private String getForecastIoUrl() {
        return properties.getProperty("weather-application.forecastio.url");
    }
}
