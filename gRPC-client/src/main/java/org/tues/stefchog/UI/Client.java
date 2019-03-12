package org.tues.stefchog.UI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.common.collect.ImmutableList;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

import org.tues.stefchog.polyglot.ConfigProto;
import org.tues.stefchog.polyglot.ConfigProto.OutputConfiguration;
import org.tues.stefchog.polyglot.ConfigProto.OutputConfiguration.Destination;
import org.tues.stefchog.polyglot.ProtobufEnvelope;
import org.tues.stefchog.polyglot.command.FileDescriptorBuilder;
import org.tues.stefchog.polyglot.command.ServiceCall;
import org.tues.stefchog.polyglot.io.Output;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {
	
	List<String> names = new ArrayList();
	List<String>values = new ArrayList();
	
    public String execute(List<String> names , List<String>values) throws DescriptorValidationException {
    	
    	//
    	this.names = names;
    	this.values = values;
    	//
    	
        OutputConfiguration outputconf = OutputConfiguration.newBuilder().
                setDestination(Destination.STDOUT).build();
        Output output = Output.forConfiguration(outputconf);
        
        String protoRoot = "src//test//java//org//tues//stefchog//polyglot//proto";
        ConfigProto.ProtoConfiguration protoConfig
                = ConfigProto.ProtoConfiguration.newBuilder().
                        setProtoDiscoveryRoot(protoRoot).
                        setUseReflection(true).build();
        
        FileDescriptorBuilder fileDescriptorBuilder =  new FileDescriptorBuilder(protoConfig);
        FileDescriptorSet fileDescriptor = fileDescriptorBuilder.build();
        List<Path> list = new ArrayList();
        ImmutableList<Path> unmodifiableList = ImmutableList.copyOf(list);
        
        ConfigProto.CallConfiguration callConfig =  
                ConfigProto.CallConfiguration.newBuilder().
                        setUseTls(false).build();
        List<DynamicMessage> result;
        ImmutableList<DynamicMessage> requestMessageList = constructRequestMessage(fileDescriptor);
        result = ServiceCall.blockingCallEndpoint(
                protoConfig,
                ("localhost:6565"),
                ("blueprint.AccountService/register"),
                callConfig,
                requestMessageList);
        
        
        if (result != null && result.size() > 0) {
        	DynamicMessage resultMessage;
        	resultMessage = result.get(0);
        	return resultMessage.toString();
        }
        return "";
    }
    
     private ImmutableList<DynamicMessage> constructRequestMessage(FileDescriptorSet fileDescriptor) throws DescriptorValidationException {
        ProtobufEnvelope pe = new ProtobufEnvelope();
       
       // pe.<String>addField("email", "test@email.com", DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);  
       //pe.<String>addField("email", messageToSend, DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);
        
       for (int i = 0; i < names.size(); i++) {
    	   pe.<String>addField(names.get(i), values.get(i), DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);
       }

       // pe.<String>addField("password", "abc", DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);
        DynamicMessage m = null;
        m = pe.constructMessage("RegisterRequest", fileDescriptor);       
        List<DynamicMessage> messageList = new ArrayList();
        messageList.add(m);
        ImmutableList<DynamicMessage> requestMessageList = ImmutableList.copyOf(messageList);
        return requestMessageList;
    }
}