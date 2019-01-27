package me.markoutte.libs.rpc;

import java.io.Serializable;

/**
 * Pelevin Maksim
 *
 * @since 2018/02/01
 */
public class RpcResponse implements Serializable {
    
    private final Object result;
    private final Throwable exception;

    private RpcResponse(Object result, Throwable exception) {
        this.result = result;
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public Throwable getException() {
        return exception;
    }

    public static RpcResponse newResult(Object result) {
        return new RpcResponse(result, null);
    }
    
    public static RpcResponse newException(Throwable e) {
        return new RpcResponse(null, e);
    }
}
