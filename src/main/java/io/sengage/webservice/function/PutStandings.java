package io.sengage.webservice.function;

import io.sengage.webservice.dagger.DaggerExtensionComponent;
import io.sengage.webservice.dagger.ExtensionComponent;
import io.sengage.webservice.model.ServerlessInput;
import io.sengage.webservice.model.ServerlessOutput;
import io.sengage.webservice.model.Standing;
import io.sengage.webservice.persistence.StandingDataProvider;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
		logger.log("PutExtensionData() input:" + serverlessInput);
		
		List<Standing> standings = gson.fromJson(serverlessInput.getBody(), new TypeToken<List<Standing>>(){}.getType());

		for (Standing standing : standings) {
			standing.setWeekRegion(String.format("%d-%s", standing.getWeek(), standing.getRegion().name()));
			standing.setId(UUID.randomUUID().toString());
		}
		
		standingDataProvider.setStandings(standings);
		
		return ServerlessOutput.builder()
        		.headers(getOutputHeaders())
        		.statusCode(HttpStatus.SC_OK)
        		.body(gson.toJson(standings))
        		.build();
	}
}
