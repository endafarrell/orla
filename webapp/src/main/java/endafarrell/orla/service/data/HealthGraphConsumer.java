package endafarrell.orla.service.data;

import endafarrell.healthgraph4j.HealthGraph;
import endafarrell.orla.OrlaException;
import endafarrell.orla.service.processor.ProcessResults;

public interface HealthGraphConsumer {
    String getHealthGraphAuthorisation() throws OrlaException;
    void authenticate(String authenticationCode) throws OrlaException;

    ProcessResults readHealthgraphFitnessActivities();
    HealthGraph getHealthGraphClient();


}
