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
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.rosuda.REngine.REXPString;

/**
 *
 * @author cemmersb
 */
public class RCodeProducerTest extends CamelTestSupport {

  RConnection rConnection = mock(RConnection.class, RETURNS_DEEP_STUBS);

  @Override
  @Before
  public void setUp() throws Exception {
    // We supply a fake factory to mock the RConnection Instance.
    RConnectionFactory.SingletonHolder.INSTANCE = new RConnectionFactory() {
      @Override
      public RConnection createConnection(RCodeConfiguration rCodeConfiguration) throws RserveException {
        return rConnection;
      }
    };
    super.setUp();
  }

  @Test
  public void sendEvalCommandTest() throws Exception {
    
    // R command to retrieve the version string from the server
    final String command = "R.version.string";
    // Prefix of the REXP version result
    final String expected = "R version";
    // Create a REXP that contains the expected version string
    final REXP rexp = new REXPString(expected);
    when(rConnection.eval(command)).thenReturn(rexp);
    
    // Initialize a mock endpoint that receives at least one message
    final MockEndpoint mockEndpoint = getMockEndpoint("mock:rcode");
    mockEndpoint.expectedMinimumMessageCount(1);
    mockEndpoint.expectedMessageCount(1);
    mockEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        final REXP receivedRexp = exchange.getIn().getBody(REXP.class);
        // Evaluate of the String contains the "R version" expression
        try {
          assertTrue(receivedRexp.asString().contains(expected));
        } catch (Exception ex) {
          fail();
          throw new Exception(ex);
        }
      }
    });

    // Send out the RCode version command
    template.sendBodyAndHeader("direct:rcode", command,
        RCodeConstants.RSERVE_OPERATION, RCodeOperation.EVAL);

    // Check if at least one result could be retrieved
    mockEndpoint.assertIsSatisfied();
  }

  @Test
  public void sendAssignContentTest() throws Exception {

    final TreeMap<String, String> assignments = new TreeMap<String, String>();
    assignments.put("mat", "matrix(seq(1:6), 2)");

    final MockEndpoint mockEndpoint = getMockEndpoint("mock:rcode");
    mockEndpoint.expectedMinimumMessageCount(1);
    mockEndpoint.expectedMessageCount(1);
    mockEndpoint.whenExchangeReceived(1, new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        final Map<String, Object> headers = exchange.getIn().getHeaders();
        if (headers.containsKey(RCodeConstants.RSERVE_OPERATION)) {
          try {
            assertTrue(headers.containsValue(RCodeOperation.ASSIGN_CONTENT));
          } catch (Exception ex) {
            fail();
            throw new Exception(ex);
          }
        }
      }
    });

    for (Entry<String, String> assigment : assignments.entrySet()) {
      template.sendBodyAndHeader("direct:rcode", assigment,
          RCodeConstants.RSERVE_OPERATION, RCodeOperation.ASSIGN_CONTENT);
    }

    mockEndpoint.assertIsSatisfied();
  }
  
  @Test
  public void sendVoidEvalTest() throws InterruptedException {
    // R command to retrieve the version string from the server
    final String command = "R.version.string";

    // Initialize a mock endpoint that receives at least one message
    final MockEndpoint mockEndpoint = getMockEndpoint("mock:rcode");
    mockEndpoint.expectedMinimumMessageCount(1);
    mockEndpoint.expectedMessageCount(1);

    // Send out the RCode version command
    template.sendBodyAndHeader("direct:rcode", command,
        RCodeConstants.RSERVE_OPERATION, RCodeOperation.VOID_EVAL);

    // Check if at least one result could be retrieved
    mockEndpoint.assertIsSatisfied();
  }

  @Override
  protected RouteBuilder createRouteBuilder() throws Exception {
    return new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        // Handle exceptions by sending the exceptions to the mock endpoint
        onException(Exception.class)
            .handled(true)
            .to("mock:error");
        // Send commands to the RCode endpoint
        from(
            "direct:rcode")
            .to("rcode:localhost:6311/eval?user=test&password=test123&bufferSize=4194304")
            .to("mock:rcode");
      }
    };
  }
}