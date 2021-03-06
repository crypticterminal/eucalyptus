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

package com.eucalyptus.walrus.bootstrap;

import org.apache.log4j.Logger;

import com.eucalyptus.bootstrap.Bootstrap;
import com.eucalyptus.bootstrap.Bootstrapper;
import com.eucalyptus.bootstrap.DependsLocal;
import com.eucalyptus.bootstrap.Provides;
import com.eucalyptus.bootstrap.RunDuring;
import com.eucalyptus.walrus.WalrusBackend;
import com.eucalyptus.walrus.WalrusControl;

@Provides(WalrusBackend.class)
@RunDuring(Bootstrap.Stage.RemoteServicesInit)
@DependsLocal(WalrusBackend.class)
public class WalrusBootstrapper extends Bootstrapper {
  private static Logger LOG = Logger.getLogger(WalrusBootstrapper.class);
  private static WalrusBootstrapper singleton;

  public static Bootstrapper getInstance() {
    synchronized (WalrusBootstrapper.class) {
      if (singleton == null) {
        singleton = new WalrusBootstrapper();
        LOG.info("Creating WalrusBackend Bootstrapper instance.");
      } else {
        LOG.info("Returning WalrusBackend Bootstrapper instance.");
      }
    }
    return singleton;
  }

  @Override
  public boolean load() throws Exception {
    WalrusControl.checkPreconditions();
    return true;
  }

  @Override
  public boolean start() throws Exception {
    WalrusControl.configure();
    return true;
  }

  /**
   * @see com.eucalyptus.bootstrap.Bootstrapper#enable()
   */
  @Override
  public boolean enable() throws Exception {
    WalrusControl.enable();
    return true;
  }

  /**
   * @see com.eucalyptus.bootstrap.Bootstrapper#stop()
   */
  @Override
  public boolean stop() throws Exception {
    WalrusControl.stop();
    return true;
  }

  /**
   * @see com.eucalyptus.bootstrap.Bootstrapper#destroy()
   */
  @Override
  public void destroy() throws Exception {}

  /**
   * @see com.eucalyptus.bootstrap.Bootstrapper#disable()
   */
  @Override
  public boolean disable() throws Exception {
    WalrusControl.disable();
    return true;
  }

  /**
   * @see com.eucalyptus.bootstrap.Bootstrapper#check()
   */
  @Override
  public boolean check() throws Exception {
    // check local storage
    WalrusControl.check();
    return true;
  }
}
