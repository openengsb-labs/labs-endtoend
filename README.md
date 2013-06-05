Karaf End-To-End Testing Framework
======================================

This library provides a simple and direct access to execute integration tests against any Apache Karaf based distribution.

[![Build Status](https://travis-ci.org/openengsb-labs/labs-endtoend.png?branch=master)](https://travis-ci.org/openengsb-labs/labs-endtoend)

Quickstart
-------------

* Add the distribution as dependency to your pom.xml

* Add the depends-maven-plugin to your pom:

<pre>
&lt;plugin&gt;
  &lt;groupId&gt;org.apache.servicemix.tooling&lt;/groupId&gt;
  &lt;artifactId&gt;depends-maven-plugin&lt;/artifactId&gt;
	&lt;version&gt;1.2&lt;/version&gt;
	&lt;executions&gt;
    &lt;execution&gt;
		  &lt;id&gt;generate-depends-file&lt;/id&gt;
			&lt;goals&gt;
				&lt;goal&gt;generate-depends-file&lt;/goal&gt;
			&lt;/goals&gt;
		&lt;/execution&gt;
	&lt;/executions&gt;
&lt;/plugin&gt;
</pre>

* Make sure that in the resources folder a context configuration file for your system exists and is named according to the form endtoend.[[osName]].[[osArch]].properties

Currently possible values are:
osName: linux, mac, win
osArch: x86, x86_64, amd64

* Define at least the distribution.uri in the configuration file:
distribution.uri = mvn-vless:[[groupId]]:[[artifactId]]:[[type]]:[[classifier]]

Other possible parameters are:
karaf.appname (default: karaf)
karaf.port (default: 8101)
karaf.cmd (default: bin/karaf)
karaf.client.cmd (default: bin/client)
karaf.root (default: .)

* To run tests on your distribution use the framework like this:

<pre>
DistributionResolver dr = new DistributionResolver();
DistributionExtractor ds = new DistributionExtractor(new File(EXTRACTION_DIR));

testContextLoader = new TestContextLoader(dr, ds);
testContextLoader.loadContexts();

defaultContext = testContextLoader.getDefaultTestContext();
context.setup();

// Test distribution...

context.teardown();
</pre>

A good working example can be found in ExecuteListCommandTest in the itest module.

Documentation
--------------
The full documentation is located at the github [wiki](https://github.com/openengsb-labs/labs-endtoend/wiki)

Build
-------------
While all final releases are available via maven central you can also build latest snapshots locally by using "mvn install".

