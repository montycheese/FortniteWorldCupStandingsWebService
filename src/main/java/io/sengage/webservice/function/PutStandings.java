package io.sengage.webservice.function;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.sengage.webservice.dagger.DaggerExtensionComponent;
import io.sengage.webservice.dagger.ExtensionComponent;
import io.sengage.webservice.model.PutStandingsRequest;
import io.sengage.webservice.model.ServerlessInput;
import io.sengage.webservice.model.ServerlessOutput;
import io.sengage.webservice.model.Standing;
import io.sengage.webservice.persistence.StandingDataProvider;

import javax.inject.Inject;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;

public class PutStandings extends BaseLambda<ServerlessInput, ServerlessOutput> {
	
	private LambdaLogger logger;
	
	private final Map<String, Integer> weekRegionNameToLastPosition;

	@Inject
	StandingDataProvider standingDataProvider;
	
	@Inject
	Gson gson;
	
	public PutStandings() {
		ExtensionComponent component = DaggerExtensionComponent.create();
		component.injectPutStandingsComponent(this);
		weekRegionNameToLastPosition = new HashMap<>();
	}
	
	@Override
	public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		logger = context.getLogger();
		logger.log("PutStandings() input:" + serverlessInput.getBody());
		
		PutStandingsRequest standings = gson.fromJson(serverlessInput.getBody(), PutStandingsRequest.class);

		for (Standing standing : standings.getStandings()) {
			String weekRegionName = getWeekRegionName(standing);
			standing.setWeekRegion(String.format("%d-%s", standing.getWeek(), standing.getRegion().name()));
			standing.setWeekRegionName(weekRegionName);
			
			System.out.println("Operating on standing: " + standing.toString());
			
			// fetch last position if they are already on the board
			Optional<Standing> lastPosition = standingDataProvider.getStanding(weekRegionName);
			
			if (lastPosition.isPresent()) {
				System.out.println("Found prior standing for user at rank: " + lastPosition.get().getRank());
				
				weekRegionNameToLastPosition.put(standing.getWeekRegionName(), lastPosition.get().getRank());
				int delta = lastPosition.get().getRank() - standing.getRank();
				standing.setDelta(delta);
				System.out.println("Calculated delta: " + delta);
				standingDataProvider.deleteStanding(lastPosition.get());
			} else if (weekRegionNameToLastPosition.containsKey(standing.getWeekRegionName())) {
				System.out.println("Standing's last position is cached");
				int delta = weekRegionNameToLastPosition.get(standing.getWeekRegionName()) - standing.getRank();
				System.out.println("Calculated delta: " + delta);
				standing.setDelta(delta);
			} else {
				System.out.println("Standing has not had a prior rank");
				standing.setDelta(Integer.MIN_VALUE);
			}
			
			// fetch person who held the new rank last
			Optional<Standing> priorStandingWithRank = standingDataProvider.getStanding(standing.getWeekRegion(), standing.getRank());
			
			if (priorStandingWithRank.isPresent()) {
				System.out.println("This rank was previously held by: " + priorStandingWithRank.get().getWeekRegionName());
				// cache their last position and delete their entry from DB
				weekRegionNameToLastPosition.put(priorStandingWithRank.get().getWeekRegionName(), priorStandingWithRank.get().getRank());
				standingDataProvider.deleteStanding(priorStandingWithRank.get());
			}
			
			standingDataProvider.setStandings(Arrays.asList(standing));
		}

		return ServerlessOutput.builder()
        		.headers(getOutputHeaders())
        		.statusCode(HttpStatus.SC_OK)
        		.body(gson.toJson(standings))
        		.build();
	}
	
	private String getWeekRegionName(Standing standing) {
		String name = standing.getName1();
		if (!standing.isSolos()) {
			name += "-" + standing.getName2();
		}
		return String.format("%d-%s-%s",
				standing.getWeek(),
				standing.getRegion().name(),
				name
			);
	}
}
