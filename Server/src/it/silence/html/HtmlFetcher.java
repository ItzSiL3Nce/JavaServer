package it.silence.html;

import it.silence.server.Protocol;
import it.silence.server.http.HttpMethod;
import it.silence.server.http.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class HtmlFetcher {

	public String error404Content = "Error 404";
	
	private static HtmlFetcher instance = null;
	
	private HtmlFetcher() {}
	
	public static HtmlFetcher getFetcher() {
		if(instance == null)
			instance = new HtmlFetcher();
		return instance;
	}
	
	public String fetch(HttpRequest request) {
		return replacements(request, fetchStatic(request.getRequestedPage()));
	}
	
	public String fetchStatic(String fileName) {
		InputStream file = getClass().getResourceAsStream("/html/" + fileName);
		if(file == null)
			return error404Content;
		
		Scanner s = new Scanner(file);
		s.useDelimiter("\\A");
		
		String html = s.hasNext() ? s.next() : "[Blank Page]";
		s.close();
		try {
			file.close();
		} catch(IOException e) {}
		
		Date now = new Date();
		return html.replace("{{file}}", fileName).replace("{{date}}", 
			new SimpleDateFormat("dd/MM/yyyy").format(now))
			.replace("{{millis}}", String.valueOf(System.currentTimeMillis()))
			.replace("{{time}}", new SimpleDateFormat("HH:mm:ss").format(now));
	}
	
	private String replacements(HttpRequest request, String html) {
		Protocol protocol = request.getProtocol();
		HttpMethod method = request.getMethod();
		return html
				.replace("{{protocol}}", protocol.name())
				.replace("{{is http}}", String.valueOf(protocol == Protocol.HTTP))
				.replace("{{is https}}", String.valueOf(protocol == Protocol.HTTPS))
				.replace("{{ip}}", request.getRequester().getHostAddress())
				.replace("{{method}}", method.name())
				.replace("{{is post}}", String.valueOf(method == HttpMethod.POST))
				.replace("{{is get}}", String.valueOf(method == HttpMethod.GET))
				.replace("{{is delete}}", String.valueOf(method == HttpMethod.DELETE))
				.replace("{{refresh}}", "")
				.replace("{{version}}", request.getHttpVersion());
	}
}
