// Base64FormatException.java
// $Id: Base64FormatException.java,v 1.5 2006/06/13 07:55:09 bounce Exp $
// (c) COPYRIGHT MIT and INRIA, 1996.
// Please first read the full copyright statement in file COPYRIGHT.html

package org.corebounce.util;

/**
 * Exception for invalid BASE64 streams.
 */
public class Base64FormatException extends Exception {
	private static final long serialVersionUID = -1507555864005858775L;

	public Base64FormatException(String msg) {
		super(msg);
	}
}
