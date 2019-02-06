package com.intracol.blueprint.grpc.services;

import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.intracol.blueprint.grpc.AccountService;
import com.intracol.blueprint.grpc.AccountServiceGrpc.AccountServiceImplBase;
import com.intracol.blueprint.grpc.RegisterError;
import com.intracol.blueprint.grpc.RegisterError.Cause;
import com.intracol.blueprint.grpc.RegisterRequest;
import com.intracol.blueprint.grpc.RegisterResponse;

import io.grpc.stub.StreamObserver;

@GRpcService
public class AccountServiceImpl extends AccountServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {

        LOGGER.info("Request to gRPC service received {}", request);

        if (!request.getEmail().equals("test@email.com") && !request.getPassword().equals("abc")) {
            responseObserver.onNext(RegisterResponse.newBuilder()
                    .setError(RegisterError.newBuilder().setCause(Cause.WRONG_EMAIL_OR_PASSWORD)).build());
            responseObserver.onCompleted();
        } else {
            if (request.getRole().equals(Role.ADMIN) || request.getRole().equals(Role.UNDEFINED)) {
                responseObserver.onNext(RegisterResponse.newBuilder()
                        .setError(RegisterError.newBuilder().setCause(Cause.WRONG_EMAIL_OR_PASSWORD)).build());
                responseObserver.onCompleted();
            }

            RegisterResponse response = RegisterResponse.newBuilder().setStatus("You logged successfully").build();

            LOGGER.info("Response from gRPC service {}", response);
            LOGGER.info("Response from gRPC service {}", response);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        if (request.getAccount().getId().equals("123")) {
            Account account = Account.newBuilder().setId("123").setUsername("username1234").setRole(Role.USER).build();
            UserResponse response = UserResponse.newBuilder().setAccount(account).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        responseObserver.onNext(UserResponse.newBuilder().setError(RequestError.newBuilder()
                .setCause(RequestError.Cause.WRONG_PARAMETERS).setMessgae("Account with specified id is not found"))
                .build());
        responseObserver.onCompleted();

    }
}
