package br.com.santander.intranet.now.executepeople;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class BasicAuth extends Authenticator {

	private String userName;
	private String pass;

	public BasicAuth(String userName, String pass) {
		this.userName = userName;
		this.pass = pass;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, pass.toCharArray());
	}

}
