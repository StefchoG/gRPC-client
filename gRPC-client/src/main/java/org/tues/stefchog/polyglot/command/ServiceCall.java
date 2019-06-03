package org.tues.stefchog.polyglot.command;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat.TypeRegistry;
import org.tues.stefchog.polyglot.ConfigProto;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.tues.stefchog.polyglot.grpc.ChannelFactory;
import org.tues.stefchog.polyglot.grpc.CompositeStreamObserver;
import org.tues.stefchog.polyglot.grpc.DynamicGrpcClient;
import org.tues.stefchog.polyglot.grpc.ServerReflectionClient;
import org.tues.stefchog.polyglot.io.LoggingStatsWriter;
import org.tues.stefchog.polyglot.io.MessageReader;
import org.tues.stefchog.polyglot.io.MessageWriter;
import org.tues.stefchog.polyglot.io.Output;
import org.tues.stefchog.polyglot.oauth2.OauthCredentialsFactory;
import org.tues.stefchog.polyglot.protobuf.ProtoMethodName;
import org.tues.stefchog.polyglot.protobuf.ProtocInvoker;
import org.tues.stefchog.polyglot.protobuf.ServiceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tues.stefchog.polyglot.ConfigProto.CallConfiguration;
import org.tues.stefchog.polyglot.ConfigProto.ProtoConfiguration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/** Makes a call to an endpoint, rendering the result */
public class ServiceCall {
  private static final Logger logger = LoggerFactory.getLogger(ServiceCall.class);

  
    public static List<DynamicMessage> blockingCallEndpoint(
      ProtoConfiguration protoConfig,
      String endpoint,
      String fullMethod,
      CallConfiguration callConfig,
      ImmutableList<DynamicMessage> requestMessages) {
     
      return  blockingCallEndpoint(
                protoConfig,
                Optional.of(endpoint),
                Optional.of(fullMethod),
                Optional.empty(),
                Optional.empty(),
                ImmutableList.copyOf(new ArrayList()),
                callConfig,
                requestMessages);
    
    }
  
     /** Calls the endpoint specified in the arguments */
  public static List<DynamicMessage> blockingCallEndpoint(
      ProtoConfiguration protoConfig,
      Optional<String> endpoint,
      Optional<String> fullMethod,
      Optional<Path> protoDiscoveryRoot,
      Optional<Path> configSetPath,
      ImmutableList<Path> additionalProtocIncludes,
      CallConfiguration callConfig,
      ImmutableList<DynamicMessage> requestMessages) {
    Preconditions.checkState(endpoint.isPresent(), "--endpoint argument required");
    Preconditions.checkState(fullMethod.isPresent(), "--full_method argument required");
    validatePath(protoDiscoveryRoot);
    validatePath(configSetPath);
    validatePaths(additionalProtocIncludes);

    HostAndPort hostAndPort = HostAndPort.fromString(endpoint.get());
    ProtoMethodName grpcMethodName =
        ProtoMethodName.parseFullGrpcMethodName(fullMethod.get());
    ChannelFactory channelFactory = ChannelFactory.create(callConfig);

    logger.info("Creating channel to: " + hostAndPort.toString());
    Channel channel;
    if (callConfig.hasOauthConfig()) {
      channel = channelFactory.createChannelWithCredentials(
          hostAndPort, new OauthCredentialsFactory(callConfig.getOauthConfig()).getCredentials());
    } else {
      channel = channelFactory.createChannel(hostAndPort);
    }

    FileDescriptorSet fileDescriptorSet 
            = constructFileDescriptor(protoConfig, channel, grpcMethodName);

    // Set up the dynamic client and make the call.
    ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
    MethodDescriptor methodDescriptor = serviceResolver.resolveServiceMethod(grpcMethodName);

    logger.info("Creating dynamic grpc client");
    DynamicGrpcClient dynamicClient = DynamicGrpcClient.create(methodDescriptor, channel);

    logger.info(String.format(
        "Making rpc with %d request(s) to endpoint [%s]", requestMessages.size(), hostAndPort));
    try {
      return dynamicClient.blockingCall(requestMessages, callOptions(callConfig));
    } catch (Throwable t) {
      throw new RuntimeException("Caught exception while waiting for rpc", t);
    }
  }
  
  /** Calls the endpoint specified in the arguments */
  public static void callEndpoint(
      Output output,
      ProtoConfiguration protoConfig,
      Optional<String> endpoint,
      Optional<String> fullMethod,
      Optional<Path> protoDiscoveryRoot,
      Optional<Path> configSetPath,
      ImmutableList<Path> additionalProtocIncludes,
      CallConfiguration callConfig,
      ImmutableList<DynamicMessage> requestMessages) {
    Preconditions.checkState(endpoint.isPresent(), "--endpoint argument required");
    Preconditions.checkState(fullMethod.isPresent(), "--full_method argument required");
    validatePath(protoDiscoveryRoot);
    validatePath(configSetPath);
    validatePaths(additionalProtocIncludes);

    HostAndPort hostAndPort = HostAndPort.fromString(endpoint.get());
    ProtoMethodName grpcMethodName =
        ProtoMethodName.parseFullGrpcMethodName(fullMethod.get());
    ChannelFactory channelFactory = ChannelFactory.create(callConfig);

    logger.info("Creating channel to: " + hostAndPort.toString());
    Channel channel;
    if (callConfig.hasOauthConfig()) {
      channel = channelFactory.createChannelWithCredentials(
          hostAndPort, new OauthCredentialsFactory(callConfig.getOauthConfig()).getCredentials());
    } else {
      channel = channelFactory.createChannel(hostAndPort);
    }

    FileDescriptorSet fileDescriptorSet 
            = constructFileDescriptor(protoConfig, channel, grpcMethodName);

    // Set up the dynamic client and make the call.
    ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);
    MethodDescriptor methodDescriptor = serviceResolver.resolveServiceMethod(grpcMethodName);

    logger.info("Creating dynamic grpc client");
    DynamicGrpcClient dynamicClient = DynamicGrpcClient.create(methodDescriptor, channel);

    // This collects all known types into a registry for resolution of potential "Any" types.
    TypeRegistry registry = TypeRegistry.newBuilder()
        .add(serviceResolver.listMessageTypes())
        .build();

    StreamObserver<DynamicMessage> streamObserver = CompositeStreamObserver.of(
        new LoggingStatsWriter(), MessageWriter.create(output, registry));
    logger.info(String.format(
        "Making rpc with %d request(s) to endpoint [%s]", requestMessages.size(), hostAndPort));
    try {
      dynamicClient.call(requestMessages, streamObserver, callOptions(callConfig)).get();
    } catch (Throwable t) {
      throw new RuntimeException("Caught exception while waiting for rpc", t);
    }
  }

    private static FileDescriptorSet constructFileDescriptor(ProtoConfiguration protoConfig, Channel channel, ProtoMethodName grpcMethodName) throws RuntimeException {
        // Fetch the appropriate file descriptors for the service.
        final FileDescriptorSet fileDescriptorSet;
        Optional<FileDescriptorSet> reflectionDescriptors = Optional.empty();
        if (protoConfig.getUseReflection()) {
            reflectionDescriptors =
                    resolveServiceByReflection(channel, grpcMethodName.getFullServiceName());
        }   if (reflectionDescriptors.isPresent()) {
            logger.info("Using proto descriptors fetched by reflection");
            fileDescriptorSet = reflectionDescriptors.get();
        } else {
            try {
                fileDescriptorSet = ProtocInvoker.forConfig(protoConfig).invoke();
                logger.info("Using proto descriptors obtained from protoc");
            } catch (Throwable t) {
                throw new RuntimeException("Unable to resolve service by invoking protoc", t);
            }
        }   
        return fileDescriptorSet;
    }

  /**
   * Returns a {@link FileDescriptorSet} describing the supplied service if the remote server
   * advertises it by reflection. Returns an empty optional if the remote server doesn't support
   * reflection. Throws a NOT_FOUND exception if we determine that the remote server does not
   * support the requested service (but *does* support the reflection service).
   */
  private static Optional<FileDescriptorSet> resolveServiceByReflection(
      Channel channel, String serviceName) {
    ServerReflectionClient serverReflectionClient = ServerReflectionClient.create(channel);
    ImmutableList<String> services;
    try {
      services = serverReflectionClient.listServices().get();
    } catch (Throwable t) {
      // Listing services failed, try and provide an explanation.
      Throwable root = Throwables.getRootCause(t);
      if (root instanceof StatusRuntimeException) {
        if (((StatusRuntimeException) root).getStatus().getCode() == Status.Code.UNIMPLEMENTED) {
          logger.warn("Could not list services because the remote host does not support " +
              "reflection. To disable resolving services by reflection, either pass the flag " +
              "--use_reflection=false or disable reflection in your config file.");
        }
      }

      // In any case, return an empty optional to indicate that this failed.
      return Optional.empty();
    }

    if (!services.contains(serviceName)) {
      throw Status.NOT_FOUND
          .withDescription(String.format(
              "Remote server does not have service %s. Services: %s", serviceName, services))
          .asRuntimeException();
    }

    try {
      return Optional.of(serverReflectionClient.lookupService(serviceName).get());
    } catch (Throwable t) {
      logger.warn("Unable to lookup service by reflection: " + serviceName, t);
      return Optional.empty();
    }
  }

  private static CallOptions callOptions(CallConfiguration callConfig) {
    CallOptions result = CallOptions.DEFAULT;
    if (callConfig.getDeadlineMs() > 0) {
      result = result.withDeadlineAfter(callConfig.getDeadlineMs(), TimeUnit.MILLISECONDS);
    }
    return result;
  }

  private static void validatePath(Optional<Path> maybePath) {
    if (maybePath.isPresent()) {
      Preconditions.checkArgument(Files.exists(maybePath.get()));
    }
  }

  private static void validatePaths(Iterable<Path> paths) {
    for (Path path : paths) {
      Preconditions.checkArgument(Files.exists(path));
    }
  }
}