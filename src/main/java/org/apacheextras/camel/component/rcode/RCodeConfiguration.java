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

import org.apache.camel.RuntimeCamelException;

import java.net.URI;

/**
 * The RCodeConfiguration object contains all elements that can be configured
 * on an endpoint or component.
 * @author cemmersb
 */
public final class RCodeConfiguration implements Cloneable {

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
  public static final long DEFAULT_BUFFER_SIZE = 1024 * 1024 * 2;
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
  
  /**
   * Creates a new RCodeConfiguration object with default values.
   * The default values are:</br>
   * <ul>
   *   <li>host = 127.0.0.1</li>
   *   <li>port = 6311</li>
   *   <li>bufferSize = 2MB</li>
   * </ul>
   */
  public RCodeConfiguration() {
  }
  
  /**
   * Creates ab RCodeConfiguration based on an URI parameter.
   * @param uri URI
   */
  public RCodeConfiguration(URI uri) {
    // Configure the host based on the endpoint URI
    final String uriHost = uri.getHost();
    if (null != uriHost && !uriHost.trim().isEmpty()) {
      setHost(uriHost);
    }
    // Configure port based on endpoint URI
    final int uriPort = uri.getPort();
    if (-1 != uriPort) {
      setPort(uriPort);
    }
  }
  
  /**
   * Copies the existing RCodeConfiguration into a new object.
   * The new object is an actual clone of the original.
   * @return RCodeConfiguration
   */
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
    // Note: The boundaries that can be processed are 32K and 1GB
    if (bufferSize < 1024 * 32) { // lowest buffer 32KB
      this.bufferSize = 1024 * 32;
    } else if (bufferSize > 1024 * 1024 * 1024) { // largest buffer 1GB
      this.bufferSize = 1024 * 1024 * 1024;
    }
    this.bufferSize = bufferSize;
  }
}