/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tues.stefchog.polyglot;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.sdt.gtl.polyglot.ProtobufEnvelope;
import org.tues.stefchog.polyglot.ConfigProto;
import org.tues.stefchog.polyglot.ConfigProto.OutputConfiguration;
import org.tues.stefchog.polyglot.ConfigProto.OutputConfiguration.Destination;
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

/**
 *
 * @author stoyan.hristov
 */
public class TestClient {
public static void main(String[] args) {
        TestClient test = new TestClient();
        test.execute();
    }
    
    public void execute() {
        OutputConfiguration outputconf = OutputConfiguration.newBuilder().
                setDestination(Destination.STDOUT).build();
        Output output = Output.forConfiguration(outputconf);
        
        String protoRoot = "src//test//java//org//tues//stefchog//polyglot//proto";
        ConfigProto.ProtoConfiguration protoConfig
                = ConfigProto.ProtoConfiguration.newBuilder().
                        setProtoDiscoveryRoot(protoRoot).
                        setUseReflection(true).build();
        List<Path> list = new ArrayList();
        ImmutableList<Path> unmodifiableList = ImmutableList.copyOf(list);
        
        ConfigProto.CallConfiguration callConfig =  
                ConfigProto.CallConfiguration.newBuilder().
                        setUseTls(false).build();
       
        ImmutableList<DynamicMessage> requestMessageList = constructRequestMessage();
        ServiceCall.callEndpoint(output,
                protoConfig,
                Optional.of("localhost:6565"),
                Optional.of("blueprint.AccountService/register"),
                Optional.of(Paths.get(protoRoot)),
                Optional.empty(),
                unmodifiableList,
                callConfig,
                requestMessageList);
    }
    
     private ImmutableList<DynamicMessage> constructRequestMessage() {
        ProtobufEnvelope pe = new ProtobufEnvelope();
        pe.<String>addField("email", "test@email.com", DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);
        pe.<String>addField("password", "abc", DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING);
        DynamicMessage m = null;
        try {
            m = pe.constructMessage("RegisterRequest");
        } catch (Descriptors.DescriptorValidationException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<DynamicMessage> messageList = new ArrayList();
        messageList.add(m);
        ImmutableList<DynamicMessage> requestMessageList = ImmutableList.copyOf(messageList);
        return requestMessageList;
    }

}
