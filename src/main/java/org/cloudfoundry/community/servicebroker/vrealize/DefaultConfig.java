package org.cloudfoundry.community.servicebroker.vrealize;

import org.cloudfoundry.community.servicebroker.model.BrokerApiVersion;
import org.cloudfoundry.community.servicebroker.model.Catalog;
import org.cloudfoundry.community.servicebroker.model.Plan;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.vrealize.domain.CatalogTranslator;
import org.cloudfoundry.community.servicebroker.vrealize.domain.PlanTranslator;
import org.cloudfoundry.community.servicebroker.vrealize.domain.ServiceDefinitionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

@Configuration
@EnableMongoRepositories
public class DefaultConfig {

	@Autowired
	String serviceUri;

	@Bean
	public BrokerApiVersion brokerApiVersion() {
		return new BrokerApiVersion("2.7");
	}

	@Bean
	Gson gson() {
		GsonBuilder builder = new GsonBuilder();
		CatalogTranslator catalogTranslator = new CatalogTranslator();
		ServiceDefinitionTranslator serviceDefinitionTranslator = new ServiceDefinitionTranslator();
		PlanTranslator planTranslator = new PlanTranslator();

		builder.registerTypeAdapter(Catalog.class, catalogTranslator);
		builder.registerTypeAdapter(ServiceDefinition.class,
				serviceDefinitionTranslator);
		builder.registerTypeAdapter(Plan.class, planTranslator);

		builder.setPrettyPrinting();
		Gson gson = builder.create();
		catalogTranslator.setGson(gson);
		serviceDefinitionTranslator.setGson(gson);

		return gson;
	}

	@Bean
	public VraRepository vraRepository() {
		return Feign.builder().encoder(new GsonEncoder())
				.decoder(new ResponseEntityDecoder(new GsonDecoder()))
				.target(VraRepository.class, serviceUri);
	}

}