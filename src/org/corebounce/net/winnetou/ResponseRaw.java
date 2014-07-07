/*
 * iiuf.net.winnetou, ResponseRaw
 * 
 * Created on 19.01.2006
 */
package org.corebounce.net.winnetou;

import java.io.IOException;

/**
 * ResponseRaw
 * 
 * (c) 2006 Swisscom Innovations
 * 
 * @author shoobee
 */
public class ResponseRaw extends HTTPResponse {
	byte[] data;

	public ResponseRaw(HTTPRequest request, byte[] data) {
		super(request);
		this.data = data;
	}

	@Override
	public void responseStatus() {
	}

	@Override
	public void responseData() {
	}

	@Override
	public void respond() throws IOException {
		request.write(data);
	}
}
