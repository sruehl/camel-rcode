# Introduction: 'camel-rcode'
The sources within this repository provide an [Apache Camel](http://camel.apache.org/) 
RCode component to integrate with the statistics environment [R](http://www.r-project.org/)
via [Rserve](http://www.rforge.net/Rserve/).
Rserve needs to be [installed](http://www.rforge.net/Rserve/doc.html#intro) manually to 
your R environment depending on your environment.</br>

One option is to install Rserve from CRAN by using:
> install.packages("Rserve")

Before we can start with the build process, we need to start Rserve in order to be able
connecting to the environment. The commands below demonstrate only an option to start
the environment. It is also possible to directly start Rserve from your shell without 
entering the R console.
> <p>R</p>
> <p>library("Rserve")</p>
> <p>Rserve()</p>

## Supported Operations
> <b>NOTE:</b> The current compent implementation does not provide the full set of 
> functionalities provided by RServe and supports only a limited set of opterations</br>

- sendEval(String command)
- sendVoidEval(String command)
- sendAssign(String symbol, String content)
- sendAssign(String symbol, REXP rexp)
- sendGet(String symbol, REXP environment)
- sendParseAndEval(String command)

## URI format
> rcode:host[:port][?options]

- <b>rcode</b> is the unique component key
- <b>host</b> defines a hostname as [Java URI](http://docs.oracle.com/javase/6/docs/api/java/net/URI.html)
- <b>port</b> defines the connection [port] (http://docs.oracle.com/javase/6/docs/api/java/net/URI.html)
- <b>options</b> configure additional parameters.

## Options
<table>
<tr>
  <th>Name</th>
  <th>Type</th>
  <th>Default</th>
  <th>Description</th>
</tr>
<tr>
  <td>user</td>
  <td>String</td>
  <td>null</td>
  <td>Username to authenticate secured Rserve instances</td>
</tr>
<tr>
  <td>password</td>
  <td>String</td>
  <td>null</td>
  <td>Password to authenticate secured Rserve instances</td>
</tr>
<tr>
  <td>bufferSize</td>
  <td>long</td>
  <td>2097152</td>
  <td>Lowest value 32 KB, highest value 1GB bound to RAM
</td>
</tr>
</table>
