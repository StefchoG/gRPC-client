package org.tues.stefchog.polyglot.command;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.DescriptorProtos;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tues.stefchog.polyglot.ConfigProto;
import org.tues.stefchog.polyglot.grpc.ServerReflectionClient;
import org.tues.stefchog.polyglot.protobuf.ProtoMethodName;
import org.tues.stefchog.polyglot.protobuf.ProtocInvoker;

/**
 *
 * @author stoyan.hristov
 */
public class FileDescriptorBuilder {
    
  private static final Logger logger = LoggerFactory.getLogger(FileDescriptorBuilder.class);
    private ConfigProto.ProtoConfiguration protoConfig;
    
    public FileDescriptorBuilder(ConfigProto.ProtoConfiguration protoConfig){
            this.protoConfig = protoConfig;
    }

    public DescriptorProtos.FileDescriptorSet build() {
        try {
            logger.info("Using proto descriptors obtained from protoc");

            return ProtocInvoker.forConfig(protoConfig).invoke(); //results in fileDescriptorSet

        } catch (Throwable t) {
            throw new RuntimeException("Unable to resolve service by invoking protoc", t);
        }
    }

    public DescriptorProtos.FileDescriptorSet build(Channel channel, ProtoMethodName grpcMethodName) throws RuntimeException {
        // Fetch the appropriate file descriptors for the service.
        final DescriptorProtos.FileDescriptorSet fileDescriptorSet;
        Optional<DescriptorProtos.FileDescriptorSet> reflectionDescriptors = Optional.empty();
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
  private static Optional<DescriptorProtos.FileDescriptorSet> resolveServiceByReflection(
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

}
