package io.sengage.webservice.function;

import java.time.Instant;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.http.HttpStatus;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;

import io.sengage.webservice.dagger.DaggerExtensionComponent;
import io.sengage.webservice.dagger.ExtensionComponent;
import io.sengage.webservice.model.Feedback;
import io.sengage.webservice.model.ServerlessInput;
import io.sengage.webservice.model.ServerlessOutput;

public class SendFeedback extends BaseLambda<ServerlessInput, ServerlessOutput> {

private LambdaLogger logger;
	
	@Inject
	Gson gson;
	
	@Inject
	DynamoDBMapper dynamoDBMapper;
	
	public SendFeedback() {
		ExtensionComponent component = DaggerExtensionComponent.create();
		component.injectSendFeedbackComponent(this);
	}

	@Override
	public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		logger = context.getLogger();
		logger.log("SendFeeback() input:" + serverlessInput.getBody());
		
		String feedbackBody = serverlessInput.getBody();
		
		if (feedbackBody == null || feedbackBody.isEmpty()) {
			throw new IllegalArgumentException("Can not send empty feedback");
		}
		
		Feedback feedback = Feedback.builder()
				.id(UUID.randomUUID().toString())
				.createdAt(Instant.now())
				.body(feedbackBody)
				.build();
		
		dynamoDBMapper.save(feedback);
		
		ServerlessOutput output = new ServerlessOutput();
		output.setStatusCode(HttpStatus.SC_OK);
		output.setHeaders(getOutputHeaders());
		return output;
	}
}
