package com.margosha.kse.calories.presentation.grpc.interceptors;

import com.margosha.kse.calories.presentation.annotations.OptionalGrpcRequest;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@GrpcGlobalServerInterceptor
public class NullRequestInterceptor implements ServerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(NullRequestInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(next.startCall(call, headers)) {
            @Override
            public void onMessage(ReqT message) {
                try {
                    Method method = findGrpcMethod(call);
                    if (method != null && method.isAnnotationPresent(OptionalGrpcRequest.class)) {
                        super.onMessage(message);
                        return;
                    }
                    if (message == null) {
                        log.error("Null request received for method: {}", call.getMethodDescriptor().getFullMethodName());
                        call.close(
                                Status.INVALID_ARGUMENT
                                        .withDescription("Request message cannot be null"),
                                new Metadata()
                        );
                        return;
                    }
                    super.onMessage(message);
                } catch (Exception e) {
                    call.close(Status.fromThrowable(e), new Metadata());
                }
            }

            private Method findGrpcMethod(ServerCall<ReqT, RespT> call) {
                try {
                    String serviceName = call.getMethodDescriptor().getServiceName();
                    String methodName = call.getMethodDescriptor().getBareMethodName();
                    Class<?> serviceClass = Class.forName(serviceName);
                    return serviceClass.getMethod(methodName, Object.class, StreamObserver.class);
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }
}