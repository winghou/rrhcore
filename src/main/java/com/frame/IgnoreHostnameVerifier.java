package com.frame;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 接受所有hostname
 * 
 * @author Superc
 *
 */
public class IgnoreHostnameVerifier implements HostnameVerifier {
  public boolean verify(String s, SSLSession sslsession) {
    return true;
  }
}
