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

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.net.URI;
import java.util.Map;

/**
 * An <a href="https://github.com/cemmersb/camel-rcode">RCode Component</a>.
 * @author cemmersb
 */
public class RCodeComponent extends DefaultComponent {

  /**
   * Configuration element contains component configuration
   */
  private RCodeConfiguration configuration;

  /**
   * Creates an endpoint instance by checking the configuration parameters.
   *
   * @param uri String
   * @param remaining String
   * @param parameters Map<String, Object>
   * @return Endpoint
   */
  @Override
  protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    // Retrieve the RCodeConfiguration if it does not exist. 
    // Otherwise copy the existing configuration.
    RCodeConfiguration newConfiguration;
    if (null == configuration) {
      newConfiguration = new RCodeConfiguration(new URI(uri));
    } else {
      newConfiguration = configuration.copy();
    }
    // Take the suffix of the URI element and fetch the operation after the '/'
    String operation = remaining.substring(remaining.indexOf('/') + 1);
    // Set the RCodeOperation value
    RCodeOperation rCodeOperation = RCodeOperation.valueOf(operation.toUpperCase());
    // Create the RCodeEndpoint based on uri, component, configuration and operation
    RCodeEndpoint endpoint = new RCodeEndpoint(uri, this, newConfiguration, rCodeOperation);
    // Sett additional configuration parameters as properties
    setProperties(endpoint.getConfiguration(), parameters);
    // retirn the endpoint
    return endpoint;
  }

  /**
   * Returns the RCodeConfiguration. It will create a new instance if the 
   * configuration object is null.
   * @return RCodeConfiguration
   */
  public RCodeConfiguration getConfiguration() {
    if (null == configuration) {
      configuration = new RCodeConfiguration();
    }
    return configuration;
  }
  
  /**
   * Sets the RCodeConfiguration.
   * @param configuration RCodeConfiguration
   */
  public void setConfiguration(RCodeConfiguration configuration) {
    this.configuration = configuration;
  }
  
  /**
   * Returns the host configuration.
   * @return String
   */
  public String getHost() {
    return getConfiguration().getHost();
  }
  
  /**
   * Sets the host configuration.
   * @param host String
   */
  public void setHost(String host) {
    getConfiguration().setHost(host);
  }
  
  /**
   * Returns the port configuration.
   * @return int
   */
  public int getPort() {
    return getConfiguration().getPort();
  }
  
  /**
   * Sets the port configuration.
   * @param port int
   */
  public void setPort(int port) {
    getConfiguration().setPort(port);
  }
  
  /**
   * Returns the user configuration.
   * @return String
   */
  public String getUser() {
    return getConfiguration().getUser();
  }
  
  /**
   * Sets the user configuration.
   * @param user String
   */
  public void setUser(String user) {
    getConfiguration().setUser(user);
  }
  
  /**
   * Returns the password configuration.
   * @return String
   */
  public String getPassword() {
    return getConfiguration().getPassword();
  }
  
  /**
   * Sets the password configuration.
   * @param password String
   */
  public void setPassword(String password) {
    getConfiguration().setPassword(password);
  }
  
  /**
   * Returns the bufferSize configuration.
   * @return long
   */
  public long getBufferSize() {
    return getConfiguration().getBufferSize();
  }
  
  /**
   * Sets the bufferSize configuration.
   * @param bufferSize long
   */
  public void setBufferSize(long bufferSize) {
    getConfiguration().setBufferSize(bufferSize);
  }
}