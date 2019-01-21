package test.client;

import com.intracol.blueprint.grpc.AccountServiceGrpc;
import com.intracol.blueprint.grpc.RegisterRequest;
import com.intracol.blueprint.grpc.RegisterResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TestClient {

	public static void main(String[] args) {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext(true).build();

		AccountServiceGrpc.AccountServiceBlockingStub accountServiceBlockingStub = AccountServiceGrpc
				.newBlockingStub(managedChannel);

		// Email/Pass: test@email.com / abc
		RegisterRequest request = RegisterRequest.newBuilder().setEmail("test@email.com").setPassword("abc")
				.build();
		System.out.println("Client sending: " + request);

		RegisterResponse response = accountServiceBlockingStub.register(request);
		System.out.println("Client received: " + response);
	}
}
