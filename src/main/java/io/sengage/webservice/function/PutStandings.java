package io.sengage.webservice.function;

import io.sengage.webservice.dagger.DaggerExtensionComponent;
import io.sengage.webservice.dagger.ExtensionComponent;
import io.sengage.webservice.model.PutStandingsRequest;
import io.sengage.webservice.model.ServerlessInput;
import io.sengage.webservice.model.ServerlessOutput;
import io.sengage.webservice.model.Standing;
import io.sengage.webservice.persistence.StandingDataProvider;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;

public class PutStandings extends BaseLambda<ServerlessInput, ServerlessOutput> {
	
	private LambdaLogger logger;

	@Inject
	StandingDataProvider standingDataProvider;
	
	@Inject
	Gson gson;
	
	public PutStandings() {
		ExtensionComponent component = DaggerExtensionComponent.create();
		component.injectPutStandingsComponent(this);
	}
	
	@Override
	public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		logger = context.getLogger();
		logger.log("PutStandings() input:" + serverlessInput.getBody());
		
		PutStandingsRequest standings = gson.fromJson(serverlessInput.getBody(), PutStandingsRequest.class);

		for (Standing standing : standings.getStandings()) {
			standing.setWeekRegion(String.format("%d-%s", standing.getWeek(), standing.getRegion().name()));
			standing.setId(UUID.randomUUID().toString());
		}
		standingDataProvider.setStandings(standings.getStandings());
		
		return ServerlessOutput.builder()
        		.headers(getOutputHeaders())
        		.statusCode(HttpStatus.SC_OK)
        		.body(gson.toJson(standings))
        		.build();
	}
}
