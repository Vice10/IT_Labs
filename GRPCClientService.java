package com.it.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

@Service
public class GRPCClientService {
    public String ping() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);
        PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .build());
        channel.shutdown();
        return helloResponse.getPong();
    }
    public String getBase(String baseName){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);
        BaseGP baseResponse = stub.getBase(BaseGP.newBuilder()
            .setBaseName(baseName)
            .build());
        channel.shutdown();
        return baseResponse.getBaseContents();
    }
    public String postTable(String baseName, String tableName, TableScheme scheme){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);
        BaseGP baseResponse = stub.postTable(TableGPD.newBuilder()
                .setBaseName(baseName)
                .setTableName(tableName)
                .setScheme(scheme)
                .build());
        channel.shutdown();
        return baseResponse.getBaseContents();
    }
    public String postRow(String baseName, String tableName, TableRow row){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);
        TableGPD tableResponse = stub.postRow(RowGP.newBuilder()
                .setBaseName(baseName)
                .setTableName(tableName)
                .setEntry(row)
                .build());
        channel.shutdown();
        return tableResponse.getTableContents();
    }
    public String rmdp(String baseName, String tableName){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);
        TableGPD tableResponse = stub.rmdp(TableGPD.newBuilder()
                .setBaseName(baseName)
                .setTableName(tableName)
                .build());
        channel.shutdown();
        return tableResponse.getTableContents();
    }
}