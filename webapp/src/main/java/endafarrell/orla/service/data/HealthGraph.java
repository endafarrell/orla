package endafarrell.orla.service.data;

import endafarrell.orla.OrlaException;
import endafarrell.orla.service.processor.ProcessResults;

public interface HealthGraph {
    String getHealthGraphAuthorisation() throws OrlaException;
    void authenticate(String authenticationCode) throws OrlaException;

    ProcessResults readHealthgraphFitnessActivities();
}
