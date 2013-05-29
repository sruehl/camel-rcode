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
 *
 * @author cemmersb
 */
public enum RCodeOperation {
  
  ASSIGN_CONTENT ("ASC"),
  ASSIGN_EXPRESSION ("ASE"),
  EVAL_COMMAND ("EVC"),
  VOID_EVAL_COMMAND("VEC"),
  GET_VALUE("GVA"),
  PARSE_AND_EVAL("PAE");
  
  private String method;
  
  RCodeOperation(String method) {
    this.method = method;
  }
  
  public String getMethod() {
    return method;
  }
  
}
