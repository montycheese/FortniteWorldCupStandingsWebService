package io.sengage.webservice.function;

import java.util.Map;

import javax.inject.Inject;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;

import io.sengage.webservice.dagger.DaggerExtensionComponent;
import io.sengage.webservice.dagger.ExtensionComponent;
import io.sengage.webservice.model.ListStandingsResult;
import io.sengage.webservice.model.Region;
import io.sengage.webservice.model.ServerlessInput;
import io.sengage.webservice.model.ServerlessOutput;
import io.sengage.webservice.persistence.StandingDataProvider;

public class GetStandings extends BaseLambda<ServerlessInput, ServerlessOutput> {

	private static final String WEEK_QUERY_PARAM_KEY = "week";
	private static final String REGION_QUERY_PARAM_KEY = "region";
	
	private LambdaLogger logger;

	@Inject
	StandingDataProvider standingDataProvider;
	
	@Inject
	Gson gson;
	
	public GetStandings() {
		ExtensionComponent component = DaggerExtensionComponent.create();
		component.injectGetStandingsComponent(this);
	}
	
	@Override
	public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		logger = context.getLogger();
		logger.log("GetExtensionData() input:" + serverlessInput);
		
		Map<String, String> queryParams = serverlessInput.getQueryStringParameters();
		
		int week = Integer.parseInt(queryParams.get(WEEK_QUERY_PARAM_KEY));
		String region = queryParams.get(REGION_QUERY_PARAM_KEY);
		
		// TODO MAKE IT 1
		if (week < 0 || week > 8) {
			throw new IllegalArgumentException("Week must be between 1-8");
		}
		
		ListStandingsResult result = standingDataProvider.getStandings(week, Region.valueOf(region));
		
		return ServerlessOutput.builder()
        		.headers(getOutputHeaders())
        		.statusCode(HttpStatus.SC_OK)
        		.body(gson.toJson(result, ListStandingsResult.class))
        		.build();
	}
	
	
}
