package me.markoutte.libs.rpc;

/**
 * Объект хранит информация о классе, объект реализации (если он локальный), хосте и порте.
 * 
 * Pelevin Maksim
 *
 * @since 2018/06/19
 */
public final class RpcObject<T> {
    
    private final Class<T> clazz;
    private final T impl;
    private final String host;
    private final int port;

    public RpcObject(Class<T> clazz, T impl, String host, int port) {
        this.clazz = clazz;
        this.impl = impl;
        this.host = host;
        this.port = port;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public T getImpl() {
        return impl;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
