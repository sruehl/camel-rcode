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
import java.net.URISyntaxException;
import java.util.Map;

/** @author cemmersb */
public class RCodeComponent extends DefaultComponent {

  private RCodeConfiguration configuration;

  @Override
  protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters)
          throws URISyntaxException, Exception {
    RCodeConfiguration newConfiguration;
    if (null == configuration) {
      newConfiguration = new RCodeConfiguration(new URI(uri));
    } else {
      newConfiguration = configuration.copy();
    }

    // We only look at the first path element
    String operation = remaining.substring(remaining.indexOf("/") + 1);

    RCodeOperation rCodeOperation = RCodeOperation.valueOf(operation.toUpperCase());

    RCodeEndpoint endpoint = new RCodeEndpoint(uri, this, newConfiguration, rCodeOperation);
    setProperties(endpoint.getConfiguration(), parameters);
    return endpoint;
  }

  public RCodeConfiguration getConfiguration() {
    if (null == configuration) {
      configuration = new RCodeConfiguration();
    }
    return configuration;
  }

  public void setConfiguration(RCodeConfiguration configuration) {
    this.configuration = configuration;
  }

  public String getHost() {
    return getConfiguration().getHost();
  }

  public void setHost(String host) {
    getConfiguration().setHost(host);
  }

  public int getPort() {
    return getConfiguration().getPort();
  }

  public void setPort(int port) {
    getConfiguration().setPort(port);
  }

  public String getUser() {
    return getConfiguration().getUser();
  }

  public void setUser(String user) {
    getConfiguration().setUser(user);
  }

  public String getPassword() {
    return getConfiguration().getPassword();
  }

  public void setPassword(String password) {
    getConfiguration().setPassword(password);
  }

  public long getBufferSize() {
    return getConfiguration().getBufferSize();
  }

  public void setBufferSize(long bufferSize) {
    getConfiguration().setBufferSize(bufferSize);
  }
}