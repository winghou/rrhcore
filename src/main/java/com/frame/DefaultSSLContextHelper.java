package com.frame;

import javax.net.ssl.HttpsURLConnection;

public class DefaultSSLContextHelper {
  public static void setupDefaultSSLContext() {
    HttpsURLConnection.setDefaultHostnameVerifier(new IgnoreHostnameVerifier());
  }
}
