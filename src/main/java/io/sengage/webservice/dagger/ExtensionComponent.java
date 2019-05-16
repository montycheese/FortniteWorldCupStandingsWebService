package io.sengage.webservice.dagger;

import io.sengage.webservice.function.GetStandings;
import io.sengage.webservice.function.PutStandings;
import io.sengage.webservice.function.SendFeedback;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {BaseModule.class, ExtensionModule.class})
public interface ExtensionComponent {
	void injectGetStandingsComponent(GetStandings getStandings);
	void injectPutStandingsComponent(PutStandings putStandings);
	void injectSendFeedbackComponent(SendFeedback sendFeedback);
}
