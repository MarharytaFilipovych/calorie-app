package com.margosha.kse.calories.presentation.grpc.interceptors;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

@Slf4j
@GrpcGlobalServerInterceptor
public class LogGrpcInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String serviceName = call.getMethodDescriptor().getServiceName();
        String methodName = call.getMethodDescriptor().getBareMethodName();

        log.info("🛰️ gRPC request intercepted — Service: {}, Method: {}, Headers: {}",
                serviceName, methodName, headers);

        ServerCall<ReqT, RespT> forwardingCall = new ForwardingServerCall.SimpleForwardingServerCall<>(call) {
            @Override
            public void sendMessage(RespT message) {
                log.info("📤 Sending message to client: {}", message);
                super.sendMessage(message);
            }
        };

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(next.startCall(forwardingCall, headers)) {
            @Override
            public void onMessage(ReqT message) {
                log.info("📥 Received message from client: {}", message);
                super.onMessage(message);
            }

        };
    }
}
