package io.sengage.webservice.function;

import io.sengage.webservice.dagger.ActivityComponent;
import io.sengage.webservice.dagger.DaggerActivityComponent;
import io.sengage.webservice.model.ServerlessInput;
import io.sengage.webservice.model.ServerlessOutput;
import io.sengage.webservice.router.LambdaRouter;

import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.apache.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.auth0.jwt.exceptions.JWTVerificationException;

public final class RequestHandler extends BaseLambda<ServerlessInput, ServerlessOutput> {
	
	@Inject 
	LambdaRouter router;
	
	public RequestHandler() {
		ActivityComponent component = DaggerActivityComponent.create();
		component.injectRequestHandler(this);
	}
	
	private LambdaLogger logger;
	@Override
	public ServerlessOutput handleRequest(ServerlessInput serverlessInput, Context context) {
		logger = context.getLogger();
		logger.log("query params: " + serverlessInput.getQueryStringParameters());
		
		ServerlessOutput output = new ServerlessOutput();;
		
		try {
			BaseLambda<ServerlessInput, ServerlessOutput> activity = 
					router.getMatchingActivity(serverlessInput.getPath(), serverlessInput.getHttpMethod()).get();
			logger.log("RequestHandler#handleRequest(): Routing request at path " + serverlessInput.getPath() +
					" with method: " + serverlessInput.getHttpMethod() + " to activity: " + activity.getClass().getName());
			output = activity.handleRequest(serverlessInput,  context);
		} catch(NoSuchElementException e) {
			logger.log("Could not find matching route for: " + serverlessInput.getPath());
			output.setStatusCode(HttpStatus.SC_NOT_FOUND);
        	logException(logger, e);
            output.setBody(e.getMessage());
		} catch(JWTVerificationException e) {
		 	logger.log("RequestHandler#handleRequest(): JWT token verification error: " + e.getMessage());
			output.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
        	logException(logger, e);
            output.setBody("Caller is not authorized to perform that action");
		} catch (IllegalArgumentException e) {
			logger.log("RequestHandler#handleRequest(): unexpected exception thrown during execution " + e.getMessage());
        	output.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        	logException(logger, e);
            output.setBody(e.getMessage());
		} catch (Exception e) {
        	logger.log("RequestHandler#handleRequest(): unexpected exception thrown during execution " + e.getMessage());
        	output.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        	logException(logger, e);
            output.setBody("Encountered unexpected error while processing request");
		}
		
        output.setHeaders(getOutputHeaders());
		return output;
	}
}
