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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.rosuda.REngine.REXPDouble;

/**
 *
 * @author cemmersb
 */
public class RCodeProducerParseEvalTest extends RCodeProducerTest {
  
  final String user = "test";
  final String password = "test123";
  
  @Test
  public void sendParseAndEvalMatrixTest() throws Exception {
    
    final String command = "seq <- seq(1:9);\n"
        + "mat <- matrix(seq, 3);\n"
        + "mat3 <- mat * 3;";
    
    final double[] expected = {3.0, 6.0, 9.0, 12.0, 15.0, 18.0, 21.0, 24.0, 27.0};
    
    final REXPDouble rexpd = new REXPDouble(expected);
    
    when(rConnection.isConnected()).thenReturn(Boolean.TRUE);
    when(rConnection.parseAndEval(command)).thenReturn(rexpd);
    when(rConnection.needLogin()).thenReturn(Boolean.TRUE);
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) {
        return null;
      }
    }).when(rConnection).login(user, password);
    
    // Initialize a mock endpoint that receives at least one message
    final MockEndpoint mockEndpoint = getMockEndpoint("mock:rcode");
    mockEndpoint.whenAnyExchangeReceived(new Processor() {
      @Override
      public void process(Exchange exchange) throws Exception {
        try {
        assertTrue(expected == ((REXPDouble)exchange.getIn().getBody()).asDoubles());
        } catch(Exception ex) {
          fail("Did not receive the expected result " + ex.getMessage());
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
        // Send commands to the RCode endpoint, operation is 'parse_and_eval'
        from("direct:rcode")
            .to("rcode:localhost:6311/parse_and_eval?user=" + user + "&password=" + password + "&bufferSize=4194304")
            .to("mock:rcode");
      }
    };
  }
}
