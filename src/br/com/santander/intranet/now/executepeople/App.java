package br.com.santander.intranet.now.executepeople;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

public class App {

	private static final String EMPTY = "";
	private static final String PRODUCTION_HOSTNAME = "now.santanderbr.corp";
	private static final String HOMOLOG_HOSTNAME = "now.santanderbr.pre.corp";
	private static final String PATH = "/api/jsonws/ps.peoplesoft/importar-dados";
	private static final String HTTPS = "https://";

	public static void main(String[] args) throws IOException {

		String env = EMPTY;
		String credentials = EMPTY;
		boolean ssl = true;

		for (String arg : args) {
			if (arg.startsWith("-e") && arg.length() > 2)
				env = arg.substring(2);

			if (arg.startsWith("-u") && arg.length() > 2)
				credentials = arg.substring(2);

			if (arg.startsWith("-s") && arg.length() > 2)
				ssl = !arg.substring(2).equals("0");
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
			httpClient = buildClient(ssl);

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

	private static CloseableHttpClient buildClient(boolean ssl)
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		CloseableHttpClient httpClient;

		if (!ssl) {
			httpClient = buildClientWithNoSSL();
		} else {
			httpClient = HttpClients.createDefault();
		}
		return httpClient;
	}

	private static CloseableHttpClient buildClientWithNoSSL()
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		final SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (x509CertChain, authType) -> true)
				.build();

		CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLContext(sslContext)
				.setConnectionManager(new PoolingHttpClientConnectionManager(RegistryBuilder
						.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
						.register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
						.build()))
				.build();
		return httpClient;
	}
}
