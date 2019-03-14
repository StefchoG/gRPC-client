/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tues.stefchog.polyglot;

/**
 *
 * @author stoyan.hristov
 */
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;

import java.util.HashMap;

/**
 * ProtobufEnvelope - allows creating a protobuf message without the .proto file
 * dynamically.
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

    public <T> void addField(String fieldName, T fieldValue, DescriptorProtos.FieldDescriptorProto.Type type, String typeName) {
        DescriptorProtos.FieldDescriptorProto.Builder fd1Builder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setName(fieldName).setNumber(i++).setType(type).setTypeName(typeName);
        desBuilder.addField(fd1Builder.build());
        values.put(fieldName, fieldValue);
    }

    public <T> void addField(String fieldName, T fieldValue, DescriptorProtos.FieldDescriptorProto.Type type) {
        DescriptorProtos.FieldDescriptorProto.Builder fd1Builder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setName(fieldName).setNumber(i++).setType(type);
        desBuilder.addField(fd1Builder.build());
        values.put(fieldName, fieldValue);
    }

    public DynamicMessage constructMessage(String messageName, FileDescriptorSet fileDescriptorSet)
            throws Descriptors.DescriptorValidationException {
        desBuilder.setName(messageName);
        DescriptorProtos.DescriptorProto dsc = desBuilder.build();

        DescriptorProtos.FileDescriptorProto fileDescP = DescriptorProtos.FileDescriptorProto.newBuilder()
                .addMessageType(dsc).build();

        FileDescriptor[] fileDescriptorsArray = buildFileDescriptors(fileDescriptorSet);

        FileDescriptor dynamicDescriptor = FileDescriptor.buildFrom(fileDescP, fileDescriptorsArray);
        Descriptor msgDescriptor = dynamicDescriptor.findMessageTypeByName(messageName);
        DynamicMessage.Builder dmBuilder
                = DynamicMessage.newBuilder(msgDescriptor); //need for dynamicMessage
        for (String name : values.keySet()) {
            Object value = values.get(name);
            FieldDescriptor.Type type = msgDescriptor.findFieldByName(name).getType(); //find 'name' in msgDescriptor
            if(type.equals(FieldDescriptor.Type.ENUM)) { //
                value = msgDescriptor.findFieldByName(name).getEnumType().findValueByName((String)values.get(name));
            } else  if(type.equals(FieldDescriptor.Type.MESSAGE)) {
                msgDescriptor.findFieldByName(name).getMessageType();
            }
            dmBuilder.setField(msgDescriptor.findFieldByName(name), value);
        }
        return dmBuilder.build();
    }

    private FileDescriptor[] buildFileDescriptors(FileDescriptorSet fileDescriptorSet) throws Descriptors.DescriptorValidationException {
        FileDescriptor[] emptyFileDescriptors = new FileDescriptor[0];
        FileDescriptor[] fileDescriptorsArray
                = new FileDescriptor[fileDescriptorSet.getFileList().size()];
        int counter = 0;
        for (FileDescriptorProto fdp : fileDescriptorSet.getFileList()) {
            
            FileDescriptor fd = FileDescriptor.buildFrom(fdp, emptyFileDescriptors);
            fileDescriptorsArray[counter++] = fd;
        }
        return fileDescriptorsArray;
    }

    public void clear() {
        desBuilder = DescriptorProtos.DescriptorProto.newBuilder();
        i = 1;
        values.clear();
    }
}
