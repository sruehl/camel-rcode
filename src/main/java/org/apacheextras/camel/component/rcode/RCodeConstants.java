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

/**
 * The RCodeConstants contain all constant variables used within the component.
 * 
 * @author cemmersb
 */
public final class RCodeConstants {
  
  // Overwriting the default consutructur
  private RCodeConstants() {
    // hide the utility class from public access
  }
  
  /**
   * Denotes the operations key value in the header of an exchange message.
   */
  public static final String RSERVE_OPERATION = "RSRV_OP";
  
}
