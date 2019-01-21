package org.tues.stefchog.polyglot.command;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;

import org.tues.stefchog.polyglot.io.Output;
import org.tues.stefchog.polyglot.protobuf.ServiceResolver;

/** Utility to list the services, methods and message definitions for the known GRPC end-points */
public class ServiceList {

  /** Lists the GRPC services - filtered by service name (contains) or method name (contains) */
  public static void listServices(
      Output output,
      FileDescriptorSet fileDescriptorSet,
      String protoDiscoveryRoot,
      Optional<String> serviceFilter,
      Optional<String> methodFilter,
      Optional<Boolean> withMessage) {

    ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);

    // Add white-space before the rendered output
    output.newLine();

    for (ServiceDescriptor descriptor : serviceResolver.listServices()) {
      boolean matchingDescriptor =
          !serviceFilter.isPresent()
          || descriptor.getFullName().toLowerCase().contains(serviceFilter.get().toLowerCase());

      if (matchingDescriptor) {
        listMethods(output, protoDiscoveryRoot, descriptor, methodFilter, withMessage);
      }
    }
  }

  /** Lists the methods on the service (the methodFilter will be applied if non-empty)  */
  private static void listMethods(
      Output output,
      String protoDiscoveryRoot,
      ServiceDescriptor descriptor,
      Optional<String> methodFilter,
      Optional<Boolean> withMessage) {

    boolean printedService = false;

    // Due to the way the protos are discovered, the leaf directly of the  protoDiscoveryRoot
    // is the same as the root directory as the proto file
    File protoDiscoveryDir = new File(protoDiscoveryRoot).getParentFile();

    for (MethodDescriptor method : descriptor.getMethods()) {
      if (!methodFilter.isPresent() || method.getName().contains(methodFilter.get())) {

        // Only print the service name once - and only if a method is going to be printed
        if (!printedService) {
          File pFile = new File(protoDiscoveryDir, descriptor.getFile().getName());
          output.writeLine(descriptor.getFullName() + " -> " + pFile.getAbsolutePath());
          printedService = true;
        }

        output.writeLine("  " + descriptor.getFullName() + "/" + method.getName());

        // If requested, add the message definition
        if (withMessage.isPresent() && withMessage.get()) {
          output.writeLine(renderDescriptor(method.getInputType(), "  "));
          output.newLine();
        }
      }
    }

    if (printedService) {
      output.newLine();
    }
  }

  /** Creates a human-readable string to help the user build a message to send to an end-point */
  private static String renderDescriptor(Descriptor descriptor, String indent) {
    if (descriptor.getFields().size() == 0) {
      return indent + "<empty>";
    }

    List<String> fieldsAsStrings = descriptor.getFields().stream()
        .map(field -> renderDescriptor(field, indent + "  "))
        .collect(Collectors.toList());

    return Joiner.on(System.lineSeparator()).join(fieldsAsStrings);
  }

  /** Create a readable string from the field to help the user build a message  */
  private static String renderDescriptor(FieldDescriptor descriptor, String indent) {
    String isOpt = descriptor.isOptional() ? "<optional>" : "<required>";
    String isRep = descriptor.isRepeated() ? "<repeated>" : "<single>";
    String fieldPrefix = indent + descriptor.getJsonName() + "[" + isOpt + " " + isRep + "]";

    if (descriptor.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
      return fieldPrefix + " {" + System.lineSeparator()
          + renderDescriptor(descriptor.getMessageType(), indent + "  ")
          + System.lineSeparator() + indent + "}";

    } else if (descriptor.getJavaType() == FieldDescriptor.JavaType.ENUM) {
      return fieldPrefix + ": " + descriptor.getEnumType().getValues();

    } else {
      return fieldPrefix + ": " + descriptor.getJavaType();
    }
  }
}
