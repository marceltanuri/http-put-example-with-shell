package br.com.mtanuri.http.put.example;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class App {

	private static final String EMPTY = "";
	private static final String PRODUCTION1_HOSTNAME = "srvieppvlbr07:9200";
	private static final String PRODUCTION2_HOSTNAME = "srvieppvlbr08:9200";
	private static final String PRODUCTION3_HOSTNAME = "srvieppvlbr09:9200";
	private static final String HOMOLOG_HOSTNAME = "srviephalbr01:9200";
	private static final String DEV_HOSTNAME = "localhost:9201";
	private static final String PATH_PUT = "/_all/_settings?preserve_existing=true";
	private static final String PATH_GET = "/_all/_settings";
	private static final String HTTP = "http://";

	public static void main(String[] args) throws IOException {

		String env = EMPTY;
		String max = EMPTY;

		for (String arg : args) {
			if (arg.startsWith("-e") && arg.length() > 2)
				env = arg.substring(2);

				if (arg.startsWith("-m") && arg.length() > 2)
				max = arg.substring(2);
		}

		String hostname = EMPTY;

		if ("hk".equals(env.toLowerCase()))
			hostname = HOMOLOG_HOSTNAME;

		if ("prd1".equals(env.toLowerCase()))
			hostname = PRODUCTION1_HOSTNAME;

			if ("prd2".equals(env.toLowerCase()))
			hostname = PRODUCTION2_HOSTNAME;

			if ("prd3".equals(env.toLowerCase()))
			hostname = PRODUCTION3_HOSTNAME;

			if ("dev".equals(env.toLowerCase()))
			hostname = DEV_HOSTNAME;

		String httpPutURL = HTTP + hostname + PATH_PUT;
		String httpGetURL = HTTP + hostname + PATH_GET;


		CloseableHttpClient httpClient;
		try {
			httpClient = buildClient();

			System.out.println("Connecting to: " + httpGetURL);

			HttpPut httpPut = new HttpPut(httpPutURL);
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("Content-type", "application/json");
			String json = "{\"index.max_result_window\" : \""+max+"\"}";
			System.out.println(json);
		  	StringEntity stringEntity = new StringEntity(json);
			httpPut.setEntity(stringEntity);

			ResponseHandler< String > responseHandler = response -> {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			};

			String responseBody = httpClient.execute(httpPut, responseHandler);
			System.out.println("----------------------------------------");
            System.out.println(responseBody);

			HttpGet request = new HttpGet(httpGetURL);
			System.out.println("executing request " + request.getRequestLine());
			CloseableHttpResponse response = httpClient.execute(request);

			
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String result = EntityUtils.toString(entity);
					System.out.println(result);
				}
				
				response.close();
				httpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static CloseableHttpClient buildClient()
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		return httpClient;
	}
}
