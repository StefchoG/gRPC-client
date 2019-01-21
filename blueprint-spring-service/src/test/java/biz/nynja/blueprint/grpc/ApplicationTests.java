/*package biz.nynja.blueprint.grpc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests extends GrpcServerTestBase {

	@Test
	public void testRegister() throws ExecutionException, InterruptedException {

		String testEmail = "newEmail@test.com";
		final AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub = AccountServiceGrpc
				.newBlockingStub(Optional.ofNullable(channel).orElse(inProcChannel));

		final RegisterRequest request = RegisterRequest.newBuilder().setId(8).setEmail(testEmail).setPassword("abc")
				.build();

		final RegisterResponse reply = accountServiceBlockingStub.register(request);

		assertNotNull("Reply should not be null", reply);
		assertTrue(String.format("Reply should contain email '%s'", testEmail),
				reply.getAccount().getEmail().equals(testEmail));
	}

	@Test
	public void testRegisterExistEmail() throws ExecutionException, InterruptedException {

		String testEmail = "email@test.com";
		final AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub = AccountServiceGrpc
				.newBlockingStub(Optional.ofNullable(channel).orElse(inProcChannel));

		final RegisterRequest request = RegisterRequest.newBuilder().setId(7).setEmail(testEmail).setPassword("abc")
				.build();

		final RegisterResponse reply = accountServiceBlockingStub.register(request);

		assertNotNull("Reply should not be null", reply);
		assertTrue(String.format("Reply should contain cause '%s'", testEmail),
				reply.getError().getCause().equals(RegisterError.Cause.EMAIL_ALREADY_USED));
	}

}
*/