package org.apacheextras.camel.component.rcode;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * Creates {@link RConnection}s.
 *
 * @author sruehl
 * @version $LastChangedVersion$
 */
public class RConnectionFactory {

  protected RConnectionFactory() {
  }

  public static RConnectionFactory getInstance() {
    return SingletonHolder.INSTANCE;
  }

  /**
   * Creates a {@link RConnection}.
   *
   * @param rCodeConfiguration
   *         the configuration.
   * @return the new created connection.
   * @throws RserveException
   * @see RConnection#RConnection(String, int)
   */
  public RConnection createConnection(RCodeConfiguration rCodeConfiguration) throws RserveException {
    return new RConnection(rCodeConfiguration.getHost(), rCodeConfiguration.getPort());
  }


  protected static class SingletonHolder {
    public static RConnectionFactory INSTANCE = new RConnectionFactory();
  }
}
