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
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Message;
import org.apache.camel.NoSuchHeaderException;
import org.apache.camel.impl.DefaultProducer;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import javax.security.auth.login.LoginException;
import java.net.ConnectException;
import java.util.Map;
import java.util.Map.Entry;

/** @author cemmersb */
public class RCodeProducer extends DefaultProducer {

  private RCodeEndpoint endpoint;

  private RCodeOperation operation;

  public RCodeProducer(RCodeEndpoint rCodeEndpoint, RCodeOperation operation) {
    super(rCodeEndpoint);
    this.endpoint = rCodeEndpoint;
    this.operation = operation;
  }

  @Override
  public void process(Exchange exchange) throws RserveException, LoginException, ConnectException,
                                                InvalidPayloadException, REngineException, REXPMismatchException {
    final Message in = exchange.getIn();
    final Map<String, Object> headers = in.getHeaders();

    if (!endpoint.isConnected()) {
      endpoint.reconnect();
    }
    
    if (headers.containsKey(RCodeConstants.RSERVE_OPERATION)) {
      final String op = headers.get(RCodeConstants.RSERVE_OPERATION).toString().toUpperCase();
      operation = RCodeOperation.valueOf(op);
    }
    executeOperation(in);
    
    exchange.getOut().getHeaders().putAll(in.getHeaders());
    exchange.getOut().setAttachments(in.getAttachments());
  }

  private Exchange executeOperation(Message in)
          throws REngineException, REXPMismatchException, InvalidPayloadException {
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
      case GET_VALUE: {
        final Entry<String, REXP> environmentValue = in.getMandatoryBody(Entry.class);
        REXP rexp = endpoint.sendGet(environmentValue.getKey(), environmentValue.getValue());
        exchange.getOut().setBody(rexp);
      }
      break;
      case PARSE_AND_EVAL: {
        final String command = in.getMandatoryBody(String.class);
        REXP rexp = endpoint.sendParseAndEval(command);
        exchange.getOut().setBody(rexp);
      }
      break;
    }
    return exchange;
  }
}