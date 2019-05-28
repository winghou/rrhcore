package com.frame;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 接受所有的https证书
 * 
 * @author Superc
 *
 */
public class IgnoreCertificationTrustManger {
  private X509Certificate[] certificates;

  public void checkClientTrusted(X509Certificate certificates[], String authType)
      throws CertificateException {
    if (this.certificates == null) {
      this.certificates = certificates;
    }
  }

  public void checkServerTrusted(X509Certificate[] ax509certificate, String s)
      throws CertificateException {
    if (this.certificates == null) {
      this.certificates = ax509certificate;
    }

  }

  public X509Certificate[] getAcceptedIssuers() {
    return null;
  }
}
