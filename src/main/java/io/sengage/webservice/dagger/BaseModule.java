package io.sengage.webservice.dagger;

import java.time.Instant;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.sengage.webservice.function.GetStandings;
import io.sengage.webservice.function.PutStandings;
import io.sengage.webservice.function.SendFeedback;
import io.sengage.webservice.persistence.DDBStandingDataProvider;
import io.sengage.webservice.persistence.DDBTournamentStandingDataProvider;
import io.sengage.webservice.persistence.StandingDataProvider;
import io.sengage.webservice.persistence.TournamentStandingDataProvider;
import io.sengage.webservice.router.LambdaRouter;
import io.sengage.webservice.router.Resource;
import io.sengage.webservice.utils.gson.InstantTypeConverter;
import dagger.Module;
import dagger.Provides;

@Module
public class BaseModule {


	@Provides
	@Singleton
	static LambdaRouter provideLambdaRouter() {
		return new LambdaRouter()
		    .registerActivity(Resource.builder()
		    		.className(GetStandings.class.getName())
		    		.httpMethod("GET")
		    		.pattern(Pattern.compile("^/standings$"))
		    		.build())
		    .registerActivity(Resource.builder()
		    		.className(PutStandings.class.getName())
		    		.httpMethod("PUT")
		    		.pattern(Pattern.compile("^/standings$"))
		    		.build())
    		.registerActivity(Resource.builder()
    				.className(SendFeedback.class.getName())
    				.httpMethod("POST")
    				.pattern(Pattern.compile("^/feedback$"))
    				.build());
	}
	
	@Provides
	@Singleton
	static Gson provideGson() {
		return new GsonBuilder()
		.registerTypeAdapter(Instant.class, new InstantTypeConverter())
		.serializeNulls()
		.create();
	}
	
	@Provides
	@Singleton
	static StandingDataProvider provideStandingDataProvider(DynamoDBMapper mapper) {
		return new DDBStandingDataProvider(mapper);
	}
	
	@Provides
	@Singleton
	static TournamentStandingDataProvider provideTournamentStandingDataProvider(DynamoDBMapper mapper) {
		return new DDBTournamentStandingDataProvider(mapper);
	}
	
	@Provides
	@Singleton
	static DynamoDBMapper provideDynamoDBMapper(AmazonDynamoDB ddb) {
		return new DynamoDBMapper(ddb);
	}
	
	@Provides
	@Singleton
	static AmazonDynamoDB provideDynamoDB() {
		return AmazonDynamoDBClientBuilder.defaultClient();
	}
	
	

}
