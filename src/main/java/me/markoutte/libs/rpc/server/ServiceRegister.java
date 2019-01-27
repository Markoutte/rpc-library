package me.markoutte.libs.rpc.server;

import me.markoutte.libs.rpc.RpcObject;
import org.openide.util.Lookup;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис регистрации всех классов с их описаниями.
 * 
 * Pelevin Maksim
 *
 * @since 2018/02/01
 */
public final class ServiceRegister {

    private Map<String, RpcObject<?>> register = new HashMap<>();
    
    public static ServiceRegister getInstance() {
        return ServiceRegisterHolder.INSTANCE;
    }
    
    public <T> RpcObject<T> add(Class<T> service, String host, int port) {
        return add(service, Lookup.getDefault().lookup(service), host, port);
    }

    public <T> RpcObject<T> add(Class<T> service, T impl, String host, int port) {
        RpcObject<T> object = new RpcObject<>(service, impl, host, port);
        register.put(service.getCanonicalName(), object);
        return object;
    }
    
    private ServiceRegister() {
    }

    public <T> T get(String clazz) {
        return this.<T>find(clazz).getImpl();
    }

    @SuppressWarnings("unchecked")
    public <T> RpcObject<T> find(String clazz) {
        return (RpcObject<T>) register.get(clazz);
    }

    private static final class ServiceRegisterHolder {
        private static final ServiceRegister INSTANCE = new ServiceRegister();
    }
}
