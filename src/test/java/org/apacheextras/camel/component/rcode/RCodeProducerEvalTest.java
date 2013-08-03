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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;


import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPString;

/**
 *
 * @author cemmersb
 */
public class RCodeProducerEvalTest extends CamelTestSupport {

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
  public void sendEvalCmdVersionTest() throws Exception {
    
    // R command to retrieve the version string from the server
    final String command = "R.version.string";
    // Prefix of the REXP version result
    final String expected = "R version 2.15.2 (2012-10-26)";
    // Create a REXP that contains the expected version string
    final REXP rexp = new REXPString(expected);
    when(rConnection.eval(command)).thenReturn(rexp);
    when(rConnection.isConnected()).thenReturn(Boolean.TRUE);
    
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
        }
      }
    });

    // Send out the RCode version command
    template.sendBody("direct:rcode", command);

    // Check if at least one result could be retrieved
    mockEndpoint.assertIsSatisfied();
  }
  
  @Test
  public void sendEvalCmdPythagorasTest() throws Exception{
    final String command = "c <- sqrt(2^2 + 2^2);";
    final double expected = 2.8284271247461903;
    final REXP rexp = new REXPDouble(expected);
    
    when(rConnection.isConnected()).thenReturn(Boolean.TRUE);
    when(rConnection.eval(command)).thenReturn(rexp);
    
    final MockEndpoint mockEndpoint = getMockEndpoint("mock:rcode");
    mockEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchng) throws Exception {
        try {
        assertTrue(expected == ((REXPDouble)exchng.getIn().getBody()).asDouble());
        } catch(Exception ex) {
          fail("Did not receive the expected result!");
        }
      }
    });
    template.sendBody("direct:rcode", command);
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