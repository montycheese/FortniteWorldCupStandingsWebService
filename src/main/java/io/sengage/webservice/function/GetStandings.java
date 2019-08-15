package io.sengage.webservice.function;

import java.util.Map;

import javax.inject.Inject;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;

import io.sengage.webservice.auth.JwtUtils;
import io.sengage.webservice.auth.TwitchJWTField;
import io.sengage.webservice.dagger.DaggerExtensionComponent;
import io.sengage.webservice.dagger.ExtensionComponent;
import io.sengage.webservice.model.ListStandingsResult;
import io.sengage.webservice.model.ListTournamentStandingsResult;
import io.sengage.webservice.model.Region;
import io.sengage.webservice.model.ServerlessInput;
import io.sengage.webservice.model.ServerlessOutput;
import io.sengage.webservice.model.Tournament;
import io.sengage.webservice.persistence.StandingDataProvider;
import io.sengage.webservice.persistence.TournamentStandingDataProvider;

public class GetStandings extends BaseLambda<ServerlessInput, ServerlessOutput> {

	private static final String WEEK_QUERY_PARAM_KEY = "week";
	private static final String REGION_QUERY_PARAM_KEY = "region";
	private static final String TOURNAMENT_QUERY_PARAM_KEY = "tournament";
	
	private LambdaLogger logger;

	@Inject
	StandingDataProvider standingDataProvider;
	
	@Inject
	TournamentStandingDataProvider tournamentStandingDataProvider;
	
	@Inject
	JwtUtils jwtUtils;
	
	@Inject
	Gson gson;
	
	public GetStandings() {
		ExtensionComponent component = DaggerExtensionComponent.create();
		component.injectGetStandingsComponent(this);
	}
	
	@Override
	public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		logger = context.getLogger();
		Map<String, String> queryParams = serverlessInput.getQueryStringParameters();
		
		String token = parseAuthTokenFromHeaders(serverlessInput.getHeaders());
		DecodedJWT jwt = jwtUtils.decode(token);
		

		logger.log(String.format("Sent by user [%s] watching channel [%s]",
				jwt.getClaim(TwitchJWTField.USER_ID.getValue()).asString(),
				jwt.getClaim(TwitchJWTField.CHANNEL_ID.getValue()).asString()));
		
		String tournamentString = queryParams.get(TOURNAMENT_QUERY_PARAM_KEY);
		Tournament tournament;
		
		if (tournamentString == null) {
			tournament = Tournament.WORLD_CUP_QUALIFIERS; // Backwards compatiable
		} else {
			tournament = Tournament.from(tournamentString);
		}
		
		switch(tournament) {
		case WORLD_CUP_QUALIFIERS:
			return handleFortniteWorldCupQualifiers(queryParams);
		case CHAMPION_SERIES_QUALIFIERS:
			return handleChampionSeriesQualifiers(queryParams);
		default:
			throw new IllegalArgumentException("Unsupported tournament: " + tournament);
		}
	}
	
	private ServerlessOutput handleChampionSeriesQualifiers(Map<String, String> queryParams) {
		int week = Integer.parseInt(queryParams.get(WEEK_QUERY_PARAM_KEY));
		String region = queryParams.get(REGION_QUERY_PARAM_KEY);
		
		// use weeek 0 for testing
		if (week < 0 || week > 5) {
			throw new IllegalArgumentException("Week must be between 1-5");
		}
		
		ListTournamentStandingsResult result = tournamentStandingDataProvider
				.getStandings(Tournament.CHAMPION_SERIES_QUALIFIERS, week, Region.from(region));
		
		if (week == 0) {
			week = 1;
		}
		result.setTitle(String.format("%s WEEK %d %s", Tournament.CHAMPION_SERIES_QUALIFIERS.getFriendlyName().toUpperCase(),
				week, result.getSquadType().name().toUpperCase()));
		
		return ServerlessOutput.builder()
        		.headers(getOutputHeaders())
        		.statusCode(HttpStatus.SC_OK)
        		.body(gson.toJson(result, ListTournamentStandingsResult.class))
        		.build();
	}
	
	private ServerlessOutput handleFortniteWorldCupQualifiers(Map<String, String> queryParams) {
		int week = Integer.parseInt(queryParams.get(WEEK_QUERY_PARAM_KEY));
		String region = queryParams.get(REGION_QUERY_PARAM_KEY);
		
		if (week < 1 || week > 10) {
			throw new IllegalArgumentException("Week must be between 1-10");
		}
		
		ListStandingsResult result = standingDataProvider.getStandings(week, Region.valueOf(region));
		
		return ServerlessOutput.builder()
        		.headers(getOutputHeaders())
        		.statusCode(HttpStatus.SC_OK)
        		.body(gson.toJson(result, ListStandingsResult.class))
        		.build();
	}
	
	
}
