/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.ssl;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import edu.vt.middleware.ldap.LdapUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link DefaultHostnameVerifier}.
 * Generate key with: openssl genrsa -aes256 -out test.key 2048
 * Generate cert with:
 * openssl req -new -x509 -sha1 -days 3650 -key test.key -out test.crt \
 *   -subj "/CN=a.foo.com/DC=ldaptive/DC=org" -config openssl.cnf \
 *   -extensions my_ext
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class DefaultHostnameVerifierTest
{

  /** Instance of the default hostname verifier. */
  private static final DefaultHostnameVerifier DEFAULT_VERIFIER =
    new DefaultHostnameVerifier();

  /** Instance of the default startTLS hostname verifier. */
  private static final SunTLSHostnameVerifier SUN_VERIFIER =
    new SunTLSHostnameVerifier();

  /** Certificate with CN=a.foo.com. */
  private static final String A_FOO_COM_CERT =
    "MIIDrzCCApegAwIBAgIJAK+nL4I3GkjeMA0GCSqGSIb3DQEBBQUAMEMxEjAQBgNV" +
    "BAMTCWEuZm9vLmNvbTEYMBYGCgmSJomT8ixkARkWCGxkYXB0aXZlMRMwEQYKCZIm" +
    "iZPyLGQBGRYDb3JnMB4XDTEyMDExNzIxNDAxNVoXDTIyMDExNDIxNDAxNVowQzES" +
    "MBAGA1UEAxMJYS5mb28uY29tMRgwFgYKCZImiZPyLGQBGRYIbGRhcHRpdmUxEzAR" +
    "BgoJkiaJk/IsZAEZFgNvcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB" +
    "AQDGRxBVvGZqHFWbYdbpOZaBf4H68b7zjiqbpXXq+mTfVOehIUeyL0624JsdmHLx" +
    "oMNC7K9hAaM88wxcyhBRLRfo4ar1DJspzcFUoz2kFD7ytWGS2zwvV+VWnoXpPNiw" +
    "9QuK6bdA/UYLIg/fk3TwshuoIT9VBJ4L3TRdOYYgH6WJBerQ2L5vMu91B9nBhNqR" +
    "4RG8VFqwgwW9IoXBXC8XTZS5jd0bVoEoeA+PWVENQ3my5ilP4VUqo9h/jPdb8dFW" +
    "3TNoaVHjOiTUOIpH+5cUmi0OkH2NzhTaWmCVoWuzFpvvB6PFHHxut2pDe8eGgc4x" +
    "NdEvZDizbfY6JEb/fkwZ+Im9AgMBAAGjgaUwgaIwHQYDVR0OBBYEFPUscUXspD8Z" +
    "LP3b6yybVhXhp5C2MHMGA1UdIwRsMGqAFPUscUXspD8ZLP3b6yybVhXhp5C2oUek" +
    "RTBDMRIwEAYDVQQDEwlhLmZvby5jb20xGDAWBgoJkiaJk/IsZAEZFghsZGFwdGl2" +
    "ZTETMBEGCgmSJomT8ixkARkWA29yZ4IJAK+nL4I3GkjeMAwGA1UdEwQFMAMBAf8w" +
    "DQYJKoZIhvcNAQEFBQADggEBALam5DdoM7cyOS2GbiA7QAfZTJkBcVr4Fef9aDWR" +
    "cG3kzbEbu1OXf3lkRW11H7gPLOgZGebSsxsv6YhKgAtz7py3lyH5QNkrN0OGI1ZA" +
    "eXf76eSR4T26pYjxln26xyZUW/dcddQ0nSj9Yl52oFCWj38DqGaxP6hIu3DHGlcE" +
    "PtpM2T4ZjWgrsqxL8N59zMb0Re9V4Xop7KmsLs3ThF3RWwmZdC1ba5LRPK6lKNF5" +
    "CnSl5YzFUMnpzFZtneUhAHeFxrF+RV4f3bHLNs+sWjlmJo0ukCCnOzoiyE4oOJiL" +
    "AhDym4nIfzng6fgYBeLT1Hp/bKHivQP4ef4wgre6r1ztnFA=";

  /** Certificate with CN=*.foo.com. */
  private static final String WC_FOO_COM_CERT =
    "MIIDrzCCApegAwIBAgIJAJycqMrRasIKMA0GCSqGSIb3DQEBBQUAMEMxEjAQBgNV" +
    "BAMUCSouZm9vLmNvbTEYMBYGCgmSJomT8ixkARkWCGxkYXB0aXZlMRMwEQYKCZIm" +
    "iZPyLGQBGRYDb3JnMB4XDTEyMDExNzIyMjQ1N1oXDTIyMDExNDIyMjQ1N1owQzES" +
    "MBAGA1UEAxQJKi5mb28uY29tMRgwFgYKCZImiZPyLGQBGRYIbGRhcHRpdmUxEzAR" +
    "BgoJkiaJk/IsZAEZFgNvcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB" +
    "AQDGRxBVvGZqHFWbYdbpOZaBf4H68b7zjiqbpXXq+mTfVOehIUeyL0624JsdmHLx" +
    "oMNC7K9hAaM88wxcyhBRLRfo4ar1DJspzcFUoz2kFD7ytWGS2zwvV+VWnoXpPNiw" +
    "9QuK6bdA/UYLIg/fk3TwshuoIT9VBJ4L3TRdOYYgH6WJBerQ2L5vMu91B9nBhNqR" +
    "4RG8VFqwgwW9IoXBXC8XTZS5jd0bVoEoeA+PWVENQ3my5ilP4VUqo9h/jPdb8dFW" +
    "3TNoaVHjOiTUOIpH+5cUmi0OkH2NzhTaWmCVoWuzFpvvB6PFHHxut2pDe8eGgc4x" +
    "NdEvZDizbfY6JEb/fkwZ+Im9AgMBAAGjgaUwgaIwHQYDVR0OBBYEFPUscUXspD8Z" +
    "LP3b6yybVhXhp5C2MHMGA1UdIwRsMGqAFPUscUXspD8ZLP3b6yybVhXhp5C2oUek" +
    "RTBDMRIwEAYDVQQDFAkqLmZvby5jb20xGDAWBgoJkiaJk/IsZAEZFghsZGFwdGl2" +
    "ZTETMBEGCgmSJomT8ixkARkWA29yZ4IJAJycqMrRasIKMAwGA1UdEwQFMAMBAf8w" +
    "DQYJKoZIhvcNAQEFBQADggEBACG6nq5fSL8F1zHH0CP+sPWHJEh5OXErdhOfAKVc" +
    "g0tfvYSI5gsyYTk87TZPTWkmpUUDn1keoVYqyXEaG8qAwL5cNUeYTze6R0GfB0UP" +
    "jwmkCxZwKhZnN/ryXhzPIEJQHRsg2fYM0P2S6jUG9m92eyCUWrbolmwfkDotbvsS" +
    "YE6m8oc7OaOVHQ20LDSLML3JOabONKSZW/BODI/ZzWzLNNU45xT4bGbtoyVwEerT" +
    "WWsGAYdXbsREzuV9q3naEd4wl5CJRBFZtTIizM1RdxxbFrAhTkiDtURTERxLmFxY" +
    "Nv3gLLhxykIoUIEtTxDjHgAiA02r3yBy5HfIC409WzmdVQI=";

  /** Certificate with CN=*.foo.bar.com. */
  private static final String WC_FOO_BAR_COM_CERT =
    "MIIDuzCCAqOgAwIBAgIJAOxfZwQylIyjMA0GCSqGSIb3DQEBBQUAMEcxFjAUBgNV" +
    "BAMUDSouZm9vLmJhci5jb20xGDAWBgoJkiaJk/IsZAEZFghsZGFwdGl2ZTETMBEG" +
    "CgmSJomT8ixkARkWA29yZzAeFw0xMjAxMTgxNzE2MTBaFw0yMjAxMTUxNzE2MTBa" +
    "MEcxFjAUBgNVBAMUDSouZm9vLmJhci5jb20xGDAWBgoJkiaJk/IsZAEZFghsZGFw" +
    "dGl2ZTETMBEGCgmSJomT8ixkARkWA29yZzCCASIwDQYJKoZIhvcNAQEBBQADggEP" +
    "ADCCAQoCggEBAMZHEFW8ZmocVZth1uk5loF/gfrxvvOOKpulder6ZN9U56EhR7Iv" +
    "Trbgmx2YcvGgw0Lsr2EBozzzDFzKEFEtF+jhqvUMmynNwVSjPaQUPvK1YZLbPC9X" +
    "5Vaehek82LD1C4rpt0D9RgsiD9+TdPCyG6ghP1UEngvdNF05hiAfpYkF6tDYvm8y" +
    "73UH2cGE2pHhEbxUWrCDBb0ihcFcLxdNlLmN3RtWgSh4D49ZUQ1DebLmKU/hVSqj" +
    "2H+M91vx0VbdM2hpUeM6JNQ4ikf7lxSaLQ6QfY3OFNpaYJWha7MWm+8Ho8UcfG63" +
    "akN7x4aBzjE10S9kOLNt9jokRv9+TBn4ib0CAwEAAaOBqTCBpjAdBgNVHQ4EFgQU" +
    "9SxxReykPxks/dvrLJtWFeGnkLYwdwYDVR0jBHAwboAU9SxxReykPxks/dvrLJtW" +
    "FeGnkLahS6RJMEcxFjAUBgNVBAMUDSouZm9vLmJhci5jb20xGDAWBgoJkiaJk/Is" +
    "ZAEZFghsZGFwdGl2ZTETMBEGCgmSJomT8ixkARkWA29yZ4IJAOxfZwQylIyjMAwG" +
    "A1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADggEBABMssljEmqtJ1+2ci+l+8zzk" +
    "Ak+xrkYNMWSjNVJ7B5pmD6MguMxfAiT2QNc0JaI0Zv4h+EprZeELQN3XsCwKRc13" +
    "v+YuMyBH7xlXzvRQ+/0Y3x5BJKTUELzOdc95vhtwnPVfEwmNhzJAUXxfi0BnT9XZ" +
    "J02ikAQ8RmtgeTUKDXLZP2xoIJ0YLc8dtdQ/M+ET6WH14kO01vqmk4ZX7oekHP2R" +
    "W1oko9r9zXl9AKWqEd2p/hD8GiHdK2oS+Ob4Hc3k9UqxaAUxidsQmhRLBJKuHjIt" +
    "GVqUK9J39FNxChacraSWTdx8yRQOxaKO5PfJDQRgCPg/9aV1AXQW+Y60ILvvHVA=";

  /**
   * Certificate with CN=a-c.foo.com
   *                  subjAltName=DNS:a.foo.com,DNS:b.foo.com,DNS:c.foo.com.
   */
  private static final String A_FOO_COM_ALTNAME_CERT =
    "MIIEAzCCAuugAwIBAgIJAMMwgpWWMq0YMA0GCSqGSIb3DQEBBQUAMEUxFDASBgNV" +
    "BAMTC2EtYy5mb28uY29tMRgwFgYKCZImiZPyLGQBGRYIbGRhcHRpdmUxEzARBgoJ" +
    "kiaJk/IsZAEZFgNvcmcwHhcNMTIwMTE4MTYxMDQwWhcNMjIwMTE1MTYxMDQwWjBF" +
    "MRQwEgYDVQQDEwthLWMuZm9vLmNvbTEYMBYGCgmSJomT8ixkARkWCGxkYXB0aXZl" +
    "MRMwEQYKCZImiZPyLGQBGRYDb3JnMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB" +
    "CgKCAQEAxkcQVbxmahxVm2HW6TmWgX+B+vG+844qm6V16vpk31TnoSFHsi9OtuCb" +
    "HZhy8aDDQuyvYQGjPPMMXMoQUS0X6OGq9QybKc3BVKM9pBQ+8rVhkts8L1flVp6F" +
    "6TzYsPULium3QP1GCyIP35N08LIbqCE/VQSeC900XTmGIB+liQXq0Ni+bzLvdQfZ" +
    "wYTakeERvFRasIMFvSKFwVwvF02UuY3dG1aBKHgPj1lRDUN5suYpT+FVKqPYf4z3" +
    "W/HRVt0zaGlR4zok1DiKR/uXFJotDpB9jc4U2lpglaFrsxab7wejxRx8brdqQ3vH" +
    "hoHOMTXRL2Q4s232OiRG/35MGfiJvQIDAQABo4H1MIHyMAwGA1UdEwEB/wQCMAAw" +
    "CwYDVR0PBAQDAgTwMBMGA1UdJQQMMAoGCCsGAQUFBwMBMB0GA1UdDgQWBBT1LHFF" +
    "7KQ/GSz92+ssm1YV4aeQtjB1BgNVHSMEbjBsgBT1LHFF7KQ/GSz92+ssm1YV4aeQ" +
    "tqFJpEcwRTEUMBIGA1UEAxMLYS1jLmZvby5jb20xGDAWBgoJkiaJk/IsZAEZFghs" +
    "ZGFwdGl2ZTETMBEGCgmSJomT8ixkARkWA29yZ4IJAMMwgpWWMq0YMCoGA1UdEQQj" +
    "MCGCCWEuZm9vLmNvbYIJYi5mb28uY29tggljLmZvby5jb20wDQYJKoZIhvcNAQEF" +
    "BQADggEBAH59Ewi4dxchcQwgJgA3KkTu6CAb/5S3BCwjv0ERdnnoshrxqu2lrF3e" +
    "2oW16kGpPdQiIw0OdD/XB3o2It01PjDzdBBBgCas2JtpoQi7/QH0qrvgFqgbzPLV" +
    "5Ehv1ObyxYKdOMDO7hqYr3PMkYyu4MhsjKp6LRDuFGHqYGzfdUzIjpfPd+jZtiN8" +
    "EBH+ZmG/PueGFd+vaQu3CIGIkG9fLrfpckUD87x/n6pa+cuWvuAd814fWJpdvLl1" +
    "iGkLfFU0E2G5pzlk9AHyWiBwYbuUrwLVW7sT7awpnzQBf0NCNETcuRmML7YnunwI" +
    "3pJosuWr0LZy4fQbu3CquXgY9GNpto8=";

  /**
   * Certificate with CN=wc.foo.com
   *                  subjAltName=DNS:*.foo.com.
   */
  private static final String WC_FOO_COM_ALTNAME_CERT =
    "MIID6jCCAtKgAwIBAgIJAJrNbvmrBDUOMA0GCSqGSIb3DQEBBQUAMEQxEzARBgNV" +
    "BAMTCndjLmZvby5jb20xGDAWBgoJkiaJk/IsZAEZFghsZGFwdGl2ZTETMBEGCgmS" +
    "JomT8ixkARkWA29yZzAeFw0xMjAxMTgxNjI2MjJaFw0yMjAxMTUxNjI2MjJaMEQx" +
    "EzARBgNVBAMTCndjLmZvby5jb20xGDAWBgoJkiaJk/IsZAEZFghsZGFwdGl2ZTET" +
    "MBEGCgmSJomT8ixkARkWA29yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC" +
    "ggEBAMZHEFW8ZmocVZth1uk5loF/gfrxvvOOKpulder6ZN9U56EhR7IvTrbgmx2Y" +
    "cvGgw0Lsr2EBozzzDFzKEFEtF+jhqvUMmynNwVSjPaQUPvK1YZLbPC9X5Vaehek8" +
    "2LD1C4rpt0D9RgsiD9+TdPCyG6ghP1UEngvdNF05hiAfpYkF6tDYvm8y73UH2cGE" +
    "2pHhEbxUWrCDBb0ihcFcLxdNlLmN3RtWgSh4D49ZUQ1DebLmKU/hVSqj2H+M91vx" +
    "0VbdM2hpUeM6JNQ4ikf7lxSaLQ6QfY3OFNpaYJWha7MWm+8Ho8UcfG63akN7x4aB" +
    "zjE10S9kOLNt9jokRv9+TBn4ib0CAwEAAaOB3jCB2zAMBgNVHRMBAf8EAjAAMAsG" +
    "A1UdDwQEAwIE8DATBgNVHSUEDDAKBggrBgEFBQcDATAdBgNVHQ4EFgQU9SxxReyk" +
    "Pxks/dvrLJtWFeGnkLYwdAYDVR0jBG0wa4AU9SxxReykPxks/dvrLJtWFeGnkLah" +
    "SKRGMEQxEzARBgNVBAMTCndjLmZvby5jb20xGDAWBgoJkiaJk/IsZAEZFghsZGFw" +
    "dGl2ZTETMBEGCgmSJomT8ixkARkWA29yZ4IJAJrNbvmrBDUOMBQGA1UdEQQNMAuC" +
    "CSouZm9vLmNvbTANBgkqhkiG9w0BAQUFAAOCAQEAcv8obBTxn7odtbjhc/Du36Zt" +
    "T+HjeO4B8Claf1XgmX8lki2SDO2qOdwA0eaYcOJyKhbdIpspQrp7W8vzvSmN6NPg" +
    "8XfAZ/xxDil8SfXwVjhHtAU4xYGeYRPY/1WCm8gKlWriV1ECRPn+sxs6DiG+HF7t" +
    "fEwFBqg1m6FLGycm6H6NMSLL+1sr9MXqjSVetKIlzvGKi4ZdGMRjobGXSx12aCt9" +
    "BfnIFAf8523sCADmpMs1th/blpzAfHkPXjtLa/6EC8Xj6EZfUaE8UGofgSpyS7wq" +
    "2ICWGB2oi1ekDMQmP15GtyNm41B2s11KCdDhSCAJu0dyIqWztO3bAGVxR1YTtQ==";

  /** Certificate with CN=127.0.0.1. */
  private static final String LOCALHOST_CERT =
    "MIIDrzCCApegAwIBAgIJAO+cKkPsfU8rMA0GCSqGSIb3DQEBBQUAMEMxEjAQBgNV" +
    "BAMTCTEyNy4wLjAuMTEYMBYGCgmSJomT8ixkARkWCGxkYXB0aXZlMRMwEQYKCZIm" +
    "iZPyLGQBGRYDb3JnMB4XDTEyMDExNzIyMzM0MFoXDTIyMDExNDIyMzM0MFowQzES" +
    "MBAGA1UEAxMJMTI3LjAuMC4xMRgwFgYKCZImiZPyLGQBGRYIbGRhcHRpdmUxEzAR" +
    "BgoJkiaJk/IsZAEZFgNvcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB" +
    "AQDGRxBVvGZqHFWbYdbpOZaBf4H68b7zjiqbpXXq+mTfVOehIUeyL0624JsdmHLx" +
    "oMNC7K9hAaM88wxcyhBRLRfo4ar1DJspzcFUoz2kFD7ytWGS2zwvV+VWnoXpPNiw" +
    "9QuK6bdA/UYLIg/fk3TwshuoIT9VBJ4L3TRdOYYgH6WJBerQ2L5vMu91B9nBhNqR" +
    "4RG8VFqwgwW9IoXBXC8XTZS5jd0bVoEoeA+PWVENQ3my5ilP4VUqo9h/jPdb8dFW" +
    "3TNoaVHjOiTUOIpH+5cUmi0OkH2NzhTaWmCVoWuzFpvvB6PFHHxut2pDe8eGgc4x" +
    "NdEvZDizbfY6JEb/fkwZ+Im9AgMBAAGjgaUwgaIwHQYDVR0OBBYEFPUscUXspD8Z" +
    "LP3b6yybVhXhp5C2MHMGA1UdIwRsMGqAFPUscUXspD8ZLP3b6yybVhXhp5C2oUek" +
    "RTBDMRIwEAYDVQQDEwkxMjcuMC4wLjExGDAWBgoJkiaJk/IsZAEZFghsZGFwdGl2" +
    "ZTETMBEGCgmSJomT8ixkARkWA29yZ4IJAO+cKkPsfU8rMAwGA1UdEwQFMAMBAf8w" +
    "DQYJKoZIhvcNAQEFBQADggEBAEy+LQguZ0kDdRone/HDnNQfCtWplHU8rE/8oFZo" +
    "ZVroGGo55zu5Iv66AljeLkTBp7FqIhH9JbwB8CF57g0Uuok560ttoWV/RPisW86p" +
    "z7eURpPClyel5+uz/PUt8crdNhXqG5iRvO7NlJONVZfLf3KlilXcoSE13msv8X80" +
    "pDXqOv61kZ4CKB1eAWMT5PXLsks47g42OtHKdOrGv+KGyiMUXmO/9Jxa44maXP6x" +
    "s8nJ1c5f2zZaZEANTkvO6UFbYynAHisBn9xD++5OcjVJMgX1qOaoxurO2kov5oyw" +
    "bLLuQaV6NVa+DPs6X6P1+iAmPQNj+Izqveq+8C1vyYdu9VU=";

  /**
   * Certificate with CN=localhost
   *                  subjAltName=IP:127.0.0.1.
   */
  private static final String LOCALHOST_ALTNAME_CERT =
    "MIID4jCCAsqgAwIBAgIJAK/f77u+7Kw2MA0GCSqGSIb3DQEBBQUAMEMxEjAQBgNV" +
    "BAMTCWxvY2FsaG9zdDEYMBYGCgmSJomT8ixkARkWCGxkYXB0aXZlMRMwEQYKCZIm" +
    "iZPyLGQBGRYDb3JnMB4XDTEyMDExODE2MDY1NFoXDTIyMDExNTE2MDY1NFowQzES" +
    "MBAGA1UEAxMJbG9jYWxob3N0MRgwFgYKCZImiZPyLGQBGRYIbGRhcHRpdmUxEzAR" +
    "BgoJkiaJk/IsZAEZFgNvcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB" +
    "AQDGRxBVvGZqHFWbYdbpOZaBf4H68b7zjiqbpXXq+mTfVOehIUeyL0624JsdmHLx" +
    "oMNC7K9hAaM88wxcyhBRLRfo4ar1DJspzcFUoz2kFD7ytWGS2zwvV+VWnoXpPNiw" +
    "9QuK6bdA/UYLIg/fk3TwshuoIT9VBJ4L3TRdOYYgH6WJBerQ2L5vMu91B9nBhNqR" +
    "4RG8VFqwgwW9IoXBXC8XTZS5jd0bVoEoeA+PWVENQ3my5ilP4VUqo9h/jPdb8dFW" +
    "3TNoaVHjOiTUOIpH+5cUmi0OkH2NzhTaWmCVoWuzFpvvB6PFHHxut2pDe8eGgc4x" +
    "NdEvZDizbfY6JEb/fkwZ+Im9AgMBAAGjgdgwgdUwDAYDVR0TAQH/BAIwADALBgNV" +
    "HQ8EBAMCBPAwEwYDVR0lBAwwCgYIKwYBBQUHAwEwHQYDVR0OBBYEFPUscUXspD8Z" +
    "LP3b6yybVhXhp5C2MHMGA1UdIwRsMGqAFPUscUXspD8ZLP3b6yybVhXhp5C2oUek" +
    "RTBDMRIwEAYDVQQDEwlsb2NhbGhvc3QxGDAWBgoJkiaJk/IsZAEZFghsZGFwdGl2" +
    "ZTETMBEGCgmSJomT8ixkARkWA29yZ4IJAK/f77u+7Kw2MA8GA1UdEQQIMAaHBH8A" +
    "AAEwDQYJKoZIhvcNAQEFBQADggEBAGa/YCXT/zUV48INqggR0ielSIXz1ztFKG4R" +
    "sWDoh76MPwyDqONXA3azXKe5BkeXDQ+6cN+VTgHYCpaHnaWdAWgLVqs4prr5MIzk" +
    "pxIRiVbayzRi0apUq5MyV/XGMECYOf3dPCT2P9Ph4jGJkLHKg66cKoxEPreoCToy" +
    "GT/1gh18bJ0xAo1CMlc4rH5C1pOx+hOIurFIxjUg44TGnBxMYUmeH0S1B1rmkuFo" +
    "h65ugoRzPU690x6DkscPxSQKexEjEZG+z0QnsQgaig6SY3bX2kKMa48QywLp0/Vo" +
    "HddtVv0q6rQqonRHRuCyD+FuXUg0w7BVVRH9txYAsE5eciIc7z0=";


  /**
   * Certificate test data.
   *
   * @return  cert test data
   *
   * @throws  Exception  if test data cannot be generated
   */
  @DataProvider(name = "certificates")
  public Object[][] createCerts()
    throws Exception
  {
    final CertificateFactory cf = CertificateFactory.getInstance("X.509");
    final X509Certificate aFooComCert =
      (X509Certificate) cf.generateCertificate(
        new ByteArrayInputStream(LdapUtil.base64Decode(A_FOO_COM_CERT)));
    final X509Certificate wcFooComCert =
      (X509Certificate) cf.generateCertificate(
        new ByteArrayInputStream(LdapUtil.base64Decode(WC_FOO_COM_CERT)));
    final X509Certificate wcFooBarComCert =
      (X509Certificate) cf.generateCertificate(
        new ByteArrayInputStream(LdapUtil.base64Decode(WC_FOO_BAR_COM_CERT)));
    final X509Certificate aFooComAltNameCert =
      (X509Certificate) cf.generateCertificate(
        new ByteArrayInputStream(
          LdapUtil.base64Decode(A_FOO_COM_ALTNAME_CERT)));
    final X509Certificate wcFooComAltNameCert =
      (X509Certificate) cf.generateCertificate(
        new ByteArrayInputStream(
          LdapUtil.base64Decode(WC_FOO_COM_ALTNAME_CERT)));
    final X509Certificate localhostCert =
      (X509Certificate) cf.generateCertificate(
        new ByteArrayInputStream(LdapUtil.base64Decode(LOCALHOST_CERT)));
    final X509Certificate localhostAltNameCert =
      (X509Certificate) cf.generateCertificate(
        new ByteArrayInputStream(
          LdapUtil.base64Decode(LOCALHOST_ALTNAME_CERT)));

    return new Object[][] {
      /* a.foo.com == CN=a.foo.com */
      new Object[] {
        "a.foo.com", aFooComCert, true,
      },
      /* b.foo.com != CN=a.foo.com */
      new Object[] {
        "b.foo.com", aFooComCert, false,
      },
      /* a.foo.com == CN=*.foo.com */
      new Object[] {
        "a.foo.com", wcFooComCert, true,
      },
      /* b.foo.com == CN=*.foo.com */
      new Object[] {
        "b.foo.com", wcFooComCert, true,
      },
      /* a.b.foo.com != CN=*.foo.com */
      new Object[] {
        "a.b.foo.com", wcFooComCert, false,
      },
      /* a.foo.com != CN=*.foo.bar.com */
      new Object[] {
        "a.foo.com", wcFooBarComCert, false,
      },
      /* a.b.foo.bar.com != CN=*.foo.bar.com */
      new Object[] {
        "a.b.foo.bar.com", wcFooBarComCert, false,
      },
      /* a.foo.bar.com == CN=*.foo.bar.com */
      new Object[] {
        "a.foo.bar.com", wcFooBarComCert, true,
      },
      /* a.foo.com == subjAltName: DNS=a.foo.com */
      new Object[] {
        "a.foo.com", aFooComAltNameCert, true,
      },
      /* b.foo.com == subjAltName: DNS=b.foo.com */
      new Object[] {
        "b.foo.com", aFooComAltNameCert, true,
      },
      /* a.foo.com == subjAltName: DNS=*.foo.com */
      new Object[] {
        "a.foo.com", wcFooComAltNameCert, true,
      },
      /* b.foo.com == subjAltName: DNS=*.foo.com */
      new Object[] {
        "b.foo.com", wcFooComAltNameCert, true,
      },
      /* a.b.foo.com != subjAltName: DNS=*.foo.com */
      new Object[] {
        "a.b.foo.com", wcFooComAltNameCert, false,
      },
      /* 10.0.0.1 != CN=127.0.0.1 */
      new Object[] {
        "10.0.0.1", localhostCert, false,
      },
      /* 127.0.0.1 != CN=127.0.0.1, IPs can only match subjAltName */
      new Object[] {
        "127.0.0.1", localhostCert, false,
      },
      /* 127.0.0.1 == subjAltName: IP=127.0.0.1 */
      new Object[] {
        "127.0.0.1", localhostAltNameCert, true,
      },
    };
  }


  /**
   * @param  hostname  to match against the cert
   * @param  cert  to extract hostname from
   * @param  pass  whether the verify should succeed
   *
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"ssl"}, dataProvider = "certificates")
  public void verify(
    final String hostname, final X509Certificate cert, final boolean pass)
    throws Exception
  {
    final boolean defaultResult = DEFAULT_VERIFIER.verify(hostname, cert);
    final boolean sunResult = SUN_VERIFIER.verify(hostname, cert);
    Assert.assertEquals(defaultResult, sunResult);
    Assert.assertEquals(defaultResult, pass);
  }
}
