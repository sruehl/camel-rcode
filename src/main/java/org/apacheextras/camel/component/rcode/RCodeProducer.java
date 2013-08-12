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

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

import javax.security.auth.login.LoginException;
import java.net.ConnectException;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.camel.CamelExchangeException;

/** @author cemmersb */
public class RCodeProducer extends DefaultProducer {

  private RCodeEndpoint endpoint;

  private RCodeOperation operation;

  /**
   * Creates an RCodeProducer with and endpoint and operation.
   * @param rCodeEndpoint RCodeEndpoint
   * @param operation RCodeOperation
   */
  public RCodeProducer(RCodeEndpoint rCodeEndpoint, RCodeOperation operation) {
    super(rCodeEndpoint);
    this.endpoint = rCodeEndpoint;
    this.operation = operation;
  }

  @Override
  public void process(Exchange exchange) throws LoginException, ConnectException,
                                                REngineException, REXPMismatchException,
                                                CamelExchangeException {
    final Message in = exchange.getIn();
    final Map<String, Object> headers = in.getHeaders();
    final RCodeOperation configuredOperation = operation;

    if (!endpoint.isConnected()) {
      endpoint.reconnect();
    }
    
    if (headers.containsKey(RCodeConstants.RSERVE_OPERATION)) {
      final String op = headers.get(RCodeConstants.RSERVE_OPERATION).toString().toUpperCase();
      operation = RCodeOperation.valueOf(op);
    }
    
    executeOperation(in);
    
    // Reset the operation to the original value in case of header 
    // controlled operation changes
    if (headers.containsKey(RCodeConstants.RSERVE_OPERATION)) {
      operation = configuredOperation;
    }
    
    exchange.getOut().getHeaders().putAll(in.getHeaders());
    exchange.getOut().setAttachments(in.getAttachments());
  }

  private void executeOperation(Message in)
          throws REngineException, REXPMismatchException, CamelExchangeException {
    final Exchange exchange = in.getExchange();

    switch (operation) {
      case ASSIGN_CONTENT: {
        final Entry<String, String> assignment = in.getMandatoryBody(Entry.class);
        endpoint.sendAssign(assignment.getKey(), assignment.getValue());
      }
      break;
      case ASSIGN_EXPRESSION: {
        final Entry<String, REXP> assignment = in.getMandatoryBody(Entry.class);
        endpoint.sendAssign(assignment.getKey(), assignment.getValue());
      }
      break;
      case EVAL: {
        final String command = in.getMandatoryBody(String.class);
        REXP rexp = endpoint.sendEval(command);
        exchange.getOut().setBody(rexp);
      }
      break;
      case VOID_EVAL: {
        final String command = in.getMandatoryBody(String.class);
        endpoint.sendVoidEval(command);
      }
      break;
      case PARSE_AND_EVAL: {
        final String command = in.getMandatoryBody(String.class);
        REXP rexp = endpoint.sendParseAndEval(command);
        exchange.getOut().setBody(rexp);
      }
      break;
    }
  }
}