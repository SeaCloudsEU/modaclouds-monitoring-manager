/**
 * Copyright 2014 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.monitoring.test;

import it.polimi.modaclouds.qos_models.monitoring_ontology.MO;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.jena.fuseki.server.FusekiConfig;
import org.apache.jena.fuseki.server.SPARQLServer;
import org.apache.jena.fuseki.server.ServerConfig;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import polimi.deib.rsp_services_csparql.server.rsp_services_csparql_server;

import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.rdf.model.Model;

@RunWith(Suite.class)
@SuiteClasses({ KbTest.class})
public class AllTests {

	private static SPARQLServer fusekiServer;

	@BeforeClass
	public static void init() {
		try {
			MO.setKnowledgeBaseURL(new URL("http://localhost:3030"));
			DatasetAccessor da = DatasetAccessorFactory.createHTTP(MO
					.getKnowledgeBaseDataURL());
			File datasetDir = new File(
					"target/generated-test-resources/dataset");
			datasetDir.mkdirs();
			ServerConfig config = FusekiConfig.configure(AllTests.class
					.getResource("/moda_fuseki_configuration.ttl").getFile());
			fusekiServer = new SPARQLServer(config);
			fusekiServer.start();
			Model ontology = RDFDataMgr.loadModel(AllTests.class.getResource(
					"/monitoring_ontology.ttl").getFile());
			da.putModel(ontology);

			rsp_services_csparql_server.main(null);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());

		}
	}

	@AfterClass
	public static void teardown() {
		fusekiServer.stop();
		try {

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {
			try {
				FileUtils.deleteDirectory(new File(
						"target/generated-test-resources/dataset"));
			} catch (IOException e) {
				Assert.fail(e.getMessage());
			}
		}
	}

}