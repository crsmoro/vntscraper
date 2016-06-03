package com.shuffle.vnt.web.servlets;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadImageImdb implements HttpServlet {

    @Override
    public void setWebServer(WebServer webServer) {
	
    }

    @Override
    public void doGet(IHTTPSession session, Response response) {
	try {
	    CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
	    String url = session.getParms().get("image");
	    HttpGet httpGet = new HttpGet(url);
	    httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0.3");
	    httpGet.addHeader("Accept-Encoding", "gzip");
	    CloseableHttpResponse imdbResponse = httpClient.execute(httpGet);
	    HttpEntity httpEntity = imdbResponse.getEntity();

	    response.setChunkedTransfer(true);
	    response.setMimeType("image/jpeg");
	    response.setData(httpEntity.getContent());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void doPost(IHTTPSession session, Response response) {
	
    }

    @Override
    public void doPut(IHTTPSession session, Response response) {
	
    }

    @Override
    public void doDelete(IHTTPSession session, Response response) {
	
    }

}
