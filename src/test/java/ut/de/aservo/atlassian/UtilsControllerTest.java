package ut.de.aservo.atlassian;

import de.aservo.atlassian.bitbucket.confapi.rest.controller.UtilsController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UtilsControllerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UtilsController utilsController;

    @Before
    public void setup() {
        utilsController = new UtilsController();
    }

    @Test
    public void testPing() {
        final Response response = utilsController.ping();
        final Object responseEntity = response.getEntity();
        final String respStr = responseEntity.toString();
        assertThat(respStr, "pong".equals(respStr));
    }
}
