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

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import javax.security.auth.login.LoginException;
import java.net.ConnectException;
import java.util.logging.Level;
import org.apache.camel.RuntimeCamelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cemmersb
 */
public class RCodeEndpoint extends DefaultEndpoint {

  // Logger to provide a certain level of information
  private static final Logger LOGGER = LoggerFactory.getLogger(RCodeEndpoint.class);
  // RConnection utilizes the RServe package of 'R'
  private RConnection rConnection;
  // Configuration contains all default values that can be overwritten by 
  // the endpoint configuration.
  private RCodeConfiguration rCodeConfiguration;
  // Contains all supported operations as enumeration
  private RCodeOperation operation;

  /**
   * Creates an empty endpoint instance based on the default configuration.
   */
  public RCodeEndpoint() {
  }

  /**
   * Creates an endpoint based on the uri and the given component.
   *
   * @param endpointUri String
   * @param component RCodeComponent
   */
  public RCodeEndpoint(String endpointUri, RCodeComponent component) {
    super(endpointUri, component);
  }

  /**
   * Creates an endpoint based on the uri, component, configuration and
   * operation.
   *
   * @param endpointUri String
   * @param component RCodeComponent
   * @param configuration RCodeConfiguration
   * @param operation RCodeOperation
   */
  public RCodeEndpoint(String endpointUri, RCodeComponent component, RCodeConfiguration configuration, RCodeOperation operation) {
    super(endpointUri, component);
    this.rCodeConfiguration = configuration;
    this.operation = operation;
  }

  @Override
  public Producer createProducer() throws Exception {
    return new RCodeProducer(this, operation);
  }

  @Override
  public Consumer createConsumer(Processor processor) throws Exception {
    // TODO: Need to verify if the createConsumer is actually required.
    // Might be an option for long running async calculations.
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isSingleton() {
    // RConnection is not thread-safe to be shared
    return false;
  }

  @Override
  protected void doStart() throws RserveException, Exception {
    super.doStart();
    // Connects after the endpoint has started
    connect();
  }

  @Override
  protected void doStop() throws Exception {
    // Closes the RConnection when shuttding down
    rConnection.close();
    super.doStop();
  }
  
  /**
   * Provides the information if the endpoint is connected.
   * @return boolean
   */
  public boolean isConnected() {
    return rConnection.isConnected();
  }
  
  /**
   * Reconnects the 'R' connection.
   */
  public void reconnect() {
    connect();
  }
  
  /**
   * Connects the RConnection to the underlying RServe instance.
   */
  private void connect() {
    // Create the RConnection instance via the Factory pattern
    if (null == rConnection) {
      try {
        rConnection = RConnectionFactory.getInstance().createConnection(rCodeConfiguration);
      } catch (RserveException ex) {
        LOGGER.error("Could not create a connection due to: {}", ex.getMessage());
        throw new RuntimeCamelException(ex.getMessage());
      }
    }
    // Login to the RConnection
    if (rConnection.needLogin()) {
      try {
        rConnection.login(rCodeConfiguration.getUser(), rCodeConfiguration.getPassword());
      } catch (RserveException ex) {
        LOGGER.error("Unable to login due to: {}", ex.getMessage());
        throw new RuntimeCamelException(ex.getMessage());
      }
    }
    // Set the encoding to UTF-8
    try {
      rConnection.setStringEncoding("utf8");
    } catch (RserveException ex) {
      LOGGER.error("Unable to set the encoding due to: {}", ex.getMessage());
      throw new RuntimeCamelException(ex.getMessage());
    }
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
   * @param configuration the rCodeConfiguration to set
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