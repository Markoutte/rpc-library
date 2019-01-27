package me.markoutte.libs.rpc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

/**
 * Pelevin Maksim
 *
 * @since 2018/02/01
 */
public class RpcRequest implements Serializable {
    
    private final String uuid;
    private final String clazz;
    private final String method;
    private final Class<?>[] parameterTypes;
    private final Object[] parameters;

    public RpcRequest(String clazz, String method, Class<?>[] parameterTypes, Object[] parameters) {
        this.uuid = UUID.randomUUID().toString();
        this.clazz = clazz;
        this.method = method;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
    }

    public String getUuid() {
        return uuid;
    }

    public String getClazz() {
        return clazz;
    }

    public String getMethod() {
        return method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "uuid='" + uuid + '\'' +
                ", clazz='" + clazz + '\'' +
                ", method='" + method + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
