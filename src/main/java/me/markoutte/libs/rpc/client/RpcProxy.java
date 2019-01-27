package me.markoutte.libs.rpc.client;

import me.markoutte.libs.rpc.RpcObject;
import me.markoutte.libs.rpc.RpcResponse;
import me.markoutte.libs.rpc.utils.Logging;
import org.openide.util.Lookup;
import me.markoutte.libs.rpc.RpcRequest;
import me.markoutte.libs.rpc.exceptions.RpcException;
import me.markoutte.libs.rpc.server.ServiceRegister;
import me.markoutte.libs.rpc.utils.ClassLoaderObjectInputStream;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Прокси-объект, который осуществляет передачу аргументов для удалённого вызова процедуры.
 * 
 * Pelevin Maksim
 *
 * @since 2018/02/05
 */
class RpcProxy<T> implements InvocationHandler {

    private final Class<T> service;
    private final int port;
    private final String host;
    private static final ClassLoader cl = Lookup.getDefault().lookup(ClassLoader.class);

    public RpcProxy(Class<T> service, String host, int port) {
        this.service = service;
        this.host = host;
        this.port = port;
    }

    @SuppressWarnings("unchecked")
    public T newProxy() {
        return (T) Proxy.newProxyInstance(Lookup.getDefault().lookup(ClassLoader.class), new Class[] {service}, this);
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String clazz = method.getDeclaringClass().getCanonicalName();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();

        RpcObject<?> object = ServiceRegister.getInstance().find(clazz);
        if (object == null) {
            object = ServiceRegister.getInstance().add(service, null, host, port);
        }
        
        if (object.getImpl() != null) {
            Logging.info(getClass(), "Found local implementation, calling for it");
            return object.getClazz().getMethod(methodName).invoke(object.getImpl(), parameterTypes);
        }

        RpcRequest request = new RpcRequest(clazz, methodName, parameterTypes, args);
        long start = System.currentTimeMillis();
        try (Socket socket = new Socket(host, port);
            ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream())) {
            stream.writeObject(request);
            stream.flush();

            try (ObjectInputStream response = new ClassLoaderObjectInputStream(cl, socket.getInputStream())) {
                RpcResponse rpcResponse = (RpcResponse) response.readObject();
                if (rpcResponse.getException() != null) {
                    throw rpcResponse.getException();
                } else return rpcResponse.getResult();
            }

        } catch (ConnectException ce) {
            throw new RpcException("Cannot connect to another service by port " + port);
        } finally {
            long end = System.currentTimeMillis();
            Logging.info(getClass(), "Method call {0} in {1} ms", request, end - start);
        }
    }
}
