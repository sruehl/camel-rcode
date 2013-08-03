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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 *
 * @author cemmersb
 */
public class RCodeProducerVoidEvalTest extends CamelTestSupport {
  
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
  public void sendVoidEvalCmdMapTest() throws Exception {
    // Path where the output file should be generated
    final String path = RCodeProducerVoidEvalTest.class.getProtectionDomain()
        .getCodeSource().getLocation().getPath() + "/german_map.pdf";
    // RCode to generate a german map as pdf
    final String command = "library(maps);\n"
        + "library(mapdata);\n"
        + "pdf(\"" + path + "\");\n"
        + "map('worldHires', 'germany');\n"
        + "dev.off()";
    
    // Configure mock endpoint for assertions
    final MockEndpoint mockEndpoint = getMockEndpoint("mock:rcode");
    mockEndpoint.expectedBodiesReceived(null); // void_eval returns a body with null value
    
    when(rConnection.isConnected()).thenReturn(Boolean.TRUE);
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) {
        return null;
      }
    }).when(rConnection).voidEval(command);
    
    // Send the command to the RCode component
    template.sendBody("direct:rcode", command);

    // Assert if the return value does not contain any body
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
        from("direct:rcode")
            .to("rcode:localhost:6311/void_eval?user=test&password=test123&bufferSize=4194304")
            .to("mock:rcode");
      }
    };
  }
}