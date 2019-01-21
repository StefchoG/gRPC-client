/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tues.stefchog.polyglot;

import com.google.common.collect.ImmutableList;
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
        
        ConfigProto.ProtoConfiguration protoConfig
                = ConfigProto.ProtoConfiguration.newBuilder().
                        setProtoDiscoveryRoot("../proto/").
                        setUseReflection(true).build();
        List<Path> list = new ArrayList();
        ImmutableList<Path> unmodifiableList = ImmutableList.copyOf(list);
        
        ConfigProto.CallConfiguration callConfig =  
                ConfigProto.CallConfiguration.newBuilder().
                        setUseTls(false).build();
 
        ServiceCall.callEndpoint(output,
                protoConfig,
                Optional.of("localhost:4556"),
                Optional.of("blueprint.AccountService/register"),
                Optional.of(Paths.get("../proto/")),
                Optional.empty(),
                unmodifiableList,
                callConfig);
    }

}
