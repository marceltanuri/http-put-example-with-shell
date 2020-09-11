package br.com.santander.intranet.now.executepeople;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

public class App {

	private static final String EMPTY = "";
	private static final String PRODUCTION_HOSTNAME = "localhost:6400";
	private static final String HOMOLOG_HOSTNAME = "localhost:6400";
	private static final String PATH = "/api/jsonws/ps.peoplesoft/importar-dados";
	private static final String HTTPS = "http://";

	public static void main(String[] args) throws IOException {

		String env = EMPTY;
		String credentials = EMPTY;
		boolean ssl = true;

		for (String arg : args) {
			if (arg.startsWith("-e") && arg.length() > 2)
				env = arg.substring(2);

			if (arg.startsWith("-u") && arg.length() > 2)
				credentials = arg.substring(2);
		}

		String hostname = EMPTY;

		if ("hk".equals(env.toLowerCase()))
			hostname = HOMOLOG_HOSTNAME;

		if ("prd".equals(env.toLowerCase()))
			hostname = PRODUCTION_HOSTNAME;

		String httpsURL = HTTPS + hostname + PATH;

		String encoding = Base64.getEncoder().encodeToString(credentials.getBytes());

		CloseableHttpClient httpClient;
		try {
			httpClient = buildClient();

			System.out.println("Connecting to: " + httpsURL);

			HttpGet request = new HttpGet(httpsURL);
			
			request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
			System.out.println("executing request " + request.getRequestLine());
			CloseableHttpResponse response = httpClient.execute(request);

			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String result = EntityUtils.toString(entity);
					System.out.println(result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				response.close();
				httpClient.close();
			}
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
