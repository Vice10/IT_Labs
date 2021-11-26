package com.it.grpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class PingPongServiceImpl extends PingPongServiceGrpc.PingPongServiceImplBase {
    @Autowired
    private final IWebApiRepo repository;
    private final BaseDAL baseDAL;
    public PingPongServiceImpl(IWebApiRepo repository, BaseDAL baseDAL) {
        this.repository = repository;
        this.baseDAL = baseDAL;
    }
    @Override
    public void ping(
            PingRequest request, StreamObserver<PongResponse> responseObserver) {
        String ping = new StringBuilder()
                .append("pong")
                .toString();
        PongResponse response = PongResponse.newBuilder()
                .setPong(ping)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    @Override
    public void getBase(BaseGP baseGP, StreamObserver<BaseGP> baseGPresponseObserver){
        String baseName = baseGP.getBaseName();
        String baseContents = "Doesn't exist";
        try {
            baseContents = baseDAL.getBaseDAL(baseName);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        BaseGP baseResp = BaseGP.newBuilder()
                .setBaseName(baseName)
                .setBaseContents(baseContents)
                .build();
        baseGPresponseObserver.onNext(baseResp);
        baseGPresponseObserver.onCompleted();
    }
    @Override
    public void postTable(TableGPD tableGPD, StreamObserver<BaseGP> baseGPresponseObserver){
        String baseName = tableGPD.getBaseName();
        String tableName = tableGPD.getTableName();
        TableScheme scheme = tableGPD.getScheme();
        String baseContents = "{}";
        try {
            baseContents = baseDAL.addTableDAL(baseName, tableName, scheme);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        BaseGP baseResp = BaseGP.newBuilder()
                .setBaseName(baseName)
                .setBaseContents(baseContents)
                .build();
        baseGPresponseObserver.onNext(baseResp);
        baseGPresponseObserver.onCompleted();
    }
    @Override
    public void postRow(RowGP rowGP, StreamObserver<TableGPD> tableGPDStreamObserver){
        String baseName = rowGP.getBaseName();
        String tableName = rowGP.getTableName();
        TableRow tableRow = rowGP.getEntry();
        String tableContents = "{}";
        try {
            tableContents = baseDAL.addRowDAL(baseName, tableName, tableRow);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        TableGPD tableGPD = TableGPD.newBuilder()
                .setTableContents(tableContents)
                .build();
        tableGPDStreamObserver.onNext(tableGPD);
        tableGPDStreamObserver.onCompleted();
    }
    @Override
    public void rmdp(TableGPD tableGPD, StreamObserver<TableGPD> tableGPDStreamObserver){
        String baseName = tableGPD.getBaseName();
        String tableName = tableGPD.getTableName();
        String tableContents = "{}";
        try {
            tableContents = baseDAL.rmdpDAL(baseName, tableName);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        TableGPD responseTableGPD = TableGPD.newBuilder()
                .setTableContents(tableContents)
                .build();
        tableGPDStreamObserver.onNext(responseTableGPD);
        tableGPDStreamObserver.onCompleted();
    }
}