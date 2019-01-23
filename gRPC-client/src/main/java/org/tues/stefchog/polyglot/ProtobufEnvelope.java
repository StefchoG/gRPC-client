/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sdt.gtl.polyglot;

/**
 *
 * @author stoyan.hristov
 */
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;

import java.util.HashMap;

/**
 * ProtobufEnvelope - allows creating a protobuf message without the .proto file dynamically.
 *
 * @author Florian Leibert
 */
public class ProtobufEnvelope {
  private HashMap<String, Object> values = new HashMap();
  private DescriptorProtos.DescriptorProto.Builder desBuilder;
  private int i = 1;

  public ProtobufEnvelope() {
    desBuilder = DescriptorProtos.DescriptorProto.newBuilder();
    i = 1;
  }

  public <T> void addField(String fieldName, T fieldValue, DescriptorProtos.FieldDescriptorProto.Type type) {
    DescriptorProtos.FieldDescriptorProto.Builder fd1Builder = DescriptorProtos.FieldDescriptorProto.newBuilder()
        .setName(fieldName).setNumber(i++).setType(type);
    desBuilder.addField(fd1Builder.build());
    values.put(fieldName, fieldValue);
  }

  public DynamicMessage constructMessage(String messageName)
      throws Descriptors.DescriptorValidationException {
    desBuilder.setName(messageName);
    DescriptorProtos.DescriptorProto dsc = desBuilder.build();

    DescriptorProtos.FileDescriptorProto fileDescP = DescriptorProtos.FileDescriptorProto.newBuilder()
        .addMessageType(dsc).build();

    Descriptors.FileDescriptor[] fileDescs = new Descriptors.FileDescriptor[0];
    Descriptors.FileDescriptor dynamicDescriptor = Descriptors.FileDescriptor.buildFrom(fileDescP, fileDescs);
    Descriptors.Descriptor msgDescriptor = dynamicDescriptor.findMessageTypeByName(messageName);
    DynamicMessage.Builder dmBuilder =
        DynamicMessage.newBuilder(msgDescriptor);
    for (String name : values.keySet()) {
      dmBuilder.setField(msgDescriptor.findFieldByName(name), values.get(name));
    }
    return dmBuilder.build();
  }

  public void clear() {
    desBuilder = DescriptorProtos.DescriptorProto.newBuilder();
    i = 1;
    values.clear();
  }
}
