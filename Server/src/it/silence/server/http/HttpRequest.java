package it.silence.server.http;

import it.silence.server.Protocol;

import java.io.Serializable;
import java.net.InetAddress;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class HttpRequest implements Serializable {

	private static final long serialVersionUID = 2075289067993755238L;

	@Getter @Setter
	private InetAddress requester;
	
	@Getter @Setter
	private HttpMethod method;

	@Getter @Setter 
	private String requestedPage;
	
	@Getter @Setter
	private Protocol protocol;
	
	@Getter @Setter
	private String httpVersion;
	
	//@Getter @Setter
	//private String[] body;
	
	public boolean isValid() {
		return requester != null && method != null && 
				httpVersion != null && protocol != null;
	}
}
