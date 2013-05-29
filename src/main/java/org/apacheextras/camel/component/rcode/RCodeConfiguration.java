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

import java.net.URI;
import org.apache.camel.RuntimeCamelException;

/**
 *
 * @author cemmersb
 */
public class RCodeConfiguration implements Cloneable {

  /**
   * The default rServe host set to
   * <code>127.0.0.1</code>.
   */
  public static final String DEFAULT_RSERVE_HOST = "127.0.0.1";
  /**
   * The default rServe port set to
   * <code>6311</code>.
   */
  public static final int DEFAULT_RSERVE_PORT = 6311;
  /**
   * Default buffer size set to
   * <code>2MB</code> in bytes.
   */
  public static final long DEFAULT_BUFFER_SIZE = 2097152;
  // Initialize the default host
  private String host = DEFAULT_RSERVE_HOST;
  // Initialize the default port
  private int port = DEFAULT_RSERVE_PORT;
  // Field for user
  private String user;
  // Field for password
  private String password;
  // Field defining the buffer size
  private long bufferSize = DEFAULT_BUFFER_SIZE;

  public RCodeConfiguration() {
  }

  public RCodeConfiguration(URI uri) {
    configure(uri);
  }

  private void configure(URI uri) {
    // Configure the host based on the endpoint URI
    final String uriHost = uri.getHost();
    if (null != uriHost) {
      setHost(uriHost);
    }
    // Configure port based on endpoint URI
    final int uriPort = uri.getPort();
    if (-1 != uriPort) {
      setPort(uriPort);
    }
  }

  public RCodeConfiguration copy() {
    try {
      return (RCodeConfiguration) clone();
    } catch (CloneNotSupportedException ex) {
      throw new RuntimeCamelException(ex);
    }
  }

  /**
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * @param host the host to set
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * @return the port
   */
  public int getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * @return the user
   */
  public String getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(String user) {
    this.user = user;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the bufferSize
   */
  public long getBufferSize() {
    return bufferSize;
  }

  /**
   * @param bufferSize the bufferSize to set
   */
  public void setBufferSize(long bufferSize) {
    // Set the buffer size to it's limits in bytes
    if (bufferSize < 32768) { // lowest buffer 32KB
      bufferSize = 32768;
    } else if (bufferSize > 1073741824) { // largest buffer 1GB
      bufferSize = 1073741824;
    }
    this.bufferSize = bufferSize;
  }
}