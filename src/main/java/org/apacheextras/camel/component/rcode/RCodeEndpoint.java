/*
 * Copyright 2013 Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apacheextras.camel.component.rcode;

import java.net.ConnectException;
import javax.security.auth.login.LoginException;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 *
 * @author cemmersb
 */
public class RCodeEndpoint extends DefaultEndpoint {

  private RConnection rConnection;
  private RCodeConfiguration rCodeConfiguration;

  public RCodeEndpoint() {
  }

  public RCodeEndpoint(String endpointUri, RCodeComponent component) {
    super(endpointUri, component);
  }

  public RCodeEndpoint(String endpointUri, RCodeComponent component, RCodeConfiguration configuration) {
    super(endpointUri, component);
    this.rCodeConfiguration = configuration;
  }

  @Override
  public Producer createProducer() throws Exception {
    return new RCodeProducer(this);
  }

  @Override
  public Consumer createConsumer(Processor processor) throws Exception {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isSingleton() {
    // RConnection is not thread-safe to be shared
    return false;
  }

  @Override
  protected void doStart() throws Exception {
    super.doStart();
    connect();
  }

  @Override
  protected void doStop() throws Exception {
    rConnection.close();
    super.doStop();
  }

  public boolean isConnected() {
    return rConnection.isConnected();
  }

  public void reconnect() throws Exception {
    connect();
  }

  private void connect() throws RserveException, LoginException, ConnectException {
    if (null == rConnection) {
      try {
        rConnection = new RConnection(rCodeConfiguration.getHost(), rCodeConfiguration.getPort());
      } catch (RserveException ex) {
        throw new ConnectException("Could not connect to Rserve: '" + ex.getMessage() + "'");
      }
    }
    if (rConnection.needLogin()) {
      try {
        rConnection.login(rCodeConfiguration.getUser(), rCodeConfiguration.getPassword());
      } catch (RserveException ex) {
        throw new LoginException("Could not login to Rserve: '" + ex.getMessage() + "'");
      }
    }
    rConnection.setStringEncoding("utf8");
  }

  public REXP sendEval(String command) throws RserveException {
    return rConnection.eval(command);
  }

  public void sendVoidEval(String command) throws RserveException {
    rConnection.voidEval(command);
  }

  public void sendAssign(String symbol, String content) throws RserveException {
    rConnection.assign(symbol, content);
  }

  public void sendAssign(String symbol, REXP rexp) throws RserveException {
    rConnection.assign(symbol, rexp);
  }

  public REXP sendGet(String symbol, REXP environment) throws REngineException {
    return rConnection.get(symbol, environment, true);
  }

  public REXP sendParseAndEval(String command) throws REngineException, REXPMismatchException {
    return rConnection.parseAndEval(command);
  }

  /**
   * @return the rCodeConfiguration
   */
  public RCodeConfiguration getConfiguration() {
    return rCodeConfiguration;
  }

  /**
   * @param rCodeConfiguration the rCodeConfiguration to set
   */
  public void setConfiguration(RCodeConfiguration configuration) {
    this.rCodeConfiguration = configuration;
  }

  public String getHost() {
    return rCodeConfiguration.getHost();
  }

  public void setHost(String host) {
    rCodeConfiguration.setHost(host);
  }

  public int getPort() {
    return rCodeConfiguration.getPort();
  }

  public void setPort(int port) {
    rCodeConfiguration.setPort(port);
  }

  public String getUser() {
    return rCodeConfiguration.getUser();
  }

  public void setUser(String user) {
    rCodeConfiguration.setUser(user);
  }

  public String getPassword() {
    return rCodeConfiguration.getPassword();
  }

  public void setPassword(String password) {
    rCodeConfiguration.setPassword(password);
  }

  public long getBufferSize() {
    return getConfiguration().getBufferSize();
  }

  public void setBufferSize(long bufferSize) {
    getConfiguration().setBufferSize(bufferSize);
  }
}