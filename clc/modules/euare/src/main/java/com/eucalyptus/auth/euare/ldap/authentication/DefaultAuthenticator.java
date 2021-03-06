/*************************************************************************
 * Copyright 2008 Regents of the University of California
 * Copyright 2009-2012 Ent. Services Development Corporation LP
 *
 * Redistribution and use of this software in source and binary forms,
 * with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer
 *   in the documentation and/or other materials provided with the
 *   distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. USERS OF THIS SOFTWARE ACKNOWLEDGE
 * THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE LICENSED MATERIAL,
 * COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS SOFTWARE,
 * AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
 * IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA,
 * SANTA BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY,
 * WHICH IN THE REGENTS' DISCRETION MAY INCLUDE, WITHOUT LIMITATION,
 * REPLACEMENT OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO
 * IDENTIFIED, OR WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT
 * NEEDED TO COMPLY WITH ANY SUCH LICENSES OR RIGHTS.
 ************************************************************************/

package com.eucalyptus.auth.euare.ldap.authentication;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.apache.log4j.Logger;
import com.eucalyptus.auth.LdapException;
import com.google.common.base.Strings;

/**
 * The default authenticator.
 * 
 * Supports:
 *   simple
 *   SASL/DIGEST-MD5
 *
 * SSL is also supported.
 */
public class DefaultAuthenticator implements LdapAuthenticator {
  
  private static Logger LOG = Logger.getLogger( DefaultAuthenticator.class ); 
  
  public DefaultAuthenticator( ) {
  }
  
  @Override
  public LdapContext authenticate( String serverUrl, String method, boolean useSsl, boolean ignoreSslCert, String login, String password, Object... extraArgs ) throws LdapException {
    if ( Strings.isNullOrEmpty( login ) || Strings.isNullOrEmpty( password ) ) {
      throw new LdapException( "LDAP login failed: empty login name or password" );
    }    
    Properties env = new Properties( );
    env.put( Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY );
    env.put( Context.REFERRAL, "follow" );
    env.put( Context.PROVIDER_URL, serverUrl );
    env.put( Context.SECURITY_AUTHENTICATION, method );
    env.put( Context.SECURITY_PRINCIPAL, login );
    env.put( Context.SECURITY_CREDENTIALS, password );
    if ( useSsl ) {
      env.put( Context.SECURITY_PROTOCOL, SSL_PROTOCOL );
      if ( ignoreSslCert ) {
        env.put( SOCKET_FACTORY, EasySSLSocketFactory.class.getCanonicalName( ) );
      }
    }
    LdapContext ldapContext = null;
    try {
      ldapContext = new InitialLdapContext( env, null );
    } catch ( NamingException e ) {
      LOG.error( e, e );
      throw new LdapException( "LDAP login failure", e );
    }
    return ldapContext;
  }
  
}
