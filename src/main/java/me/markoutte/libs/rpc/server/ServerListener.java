package me.markoutte.libs.rpc.server;

import me.markoutte.libs.rpc.RpcResponse;
import me.markoutte.libs.rpc.utils.Logging;
import org.openide.modules.OnStart;
import org.openide.util.Lookup;
import me.markoutte.libs.rpc.RpcRequest;
import me.markoutte.libs.rpc.exceptions.RpcException;
import me.markoutte.libs.rpc.processing.RpcServerConfigurationProperties;
import me.markoutte.libs.rpc.utils.ClassLoaderObjectInputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.nio.channels.CompletionHandler;
import java.util.Collection;
import java.util.List;

/**
 * Класс отвечает за асинхронный ответ на вызов удалённых процедур.
 * 
 * Pelevin Maksim
 *
 * @since 2018/02/01
 */
@OnStart
public class ServerListener implements Runnable {

    private static final ClassLoader cl = Lookup.getDefault().lookup(ClassLoader.class);

    @Override
    public void run() {
        try {
            init(RpcServerConfigurationProperties.getDefault().getConfigurations());
        } catch (RpcException e) {
            Logging.error(getClass(), e.getMessage());
        } catch (IOException e) {
            Logging.error(getClass(), e, "Cannot start service");
        }
    }

    public void run(List<RpcServerConfigurationProperties.Configuration> configurations) {
        try {
            init(configurations);
        } catch (RpcException e) {
            Logging.error(getClass(), e.getMessage());
        } catch (IOException e) {
            Logging.error(getClass(), e, "Cannot start service");
        }
    }

    private void init(Collection<RpcServerConfigurationProperties.Configuration> configurations) throws RpcException, IOException {
        final ServerHandler handler = new ServerHandler();
        for (RpcServerConfigurationProperties.Configuration configuration : configurations) {
            String host = configuration.getHost();
            int port = configuration.getPort();
            if (host == null || port < 0) {
                throw new RpcException("No server configuration found");
            }

            InetSocketAddress iadrr = new InetSocketAddress(host, port);

            AsynchronousServerSocketChannel channel = AsynchronousServerSocketChannel.open();
            channel.bind(iadrr);
            channel.accept(channel, handler);
        }
    }
    
    private static class ServerHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

        @Override
        public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {
            attachment.accept(attachment, this);
            
            try (ObjectInputStream stream = new ClassLoaderObjectInputStream(cl, Channels.newInputStream(result))) {
                Object readObject = stream.readObject();
                if (readObject instanceof RpcRequest) {
                    RpcResponse response;

                    Logging.info(getClass(), "Create response for {0} on {1}", result.getRemoteAddress(), readObject);
                    
                    try {
                        response = RpcResponse.newResult(invoke((RpcRequest) readObject));
                    } catch (Throwable e) {
                        response = RpcResponse.newException(e);
                    }
                    
                    try (ObjectOutputStream out = new ObjectOutputStream(Channels.newOutputStream(result))) {
                        out.writeObject(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private Object invoke(RpcRequest request) throws Throwable {
            Object o = ServiceRegister.getInstance().get(request.getClazz());
            Method m = o.getClass().getDeclaredMethod(request.getMethod(), request.getParameterTypes());
            try {
                return m.invoke(o, request.getParameters());
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
            exc.printStackTrace();
        }
    }
}
