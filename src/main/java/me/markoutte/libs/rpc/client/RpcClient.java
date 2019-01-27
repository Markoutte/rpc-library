package me.markoutte.libs.rpc.client;

import me.markoutte.libs.rpc.exceptions.RpcRuntimeException;
import me.markoutte.libs.rpc.processing.RpcServerConfigurationProperties;

import java.lang.reflect.Method;

/**
 * Клиент для работы RPC.
 * 
 * Используется клиентами для вызова удалённых методов других программных комплексов.
 * Стандартный код выглядит следующим образом (после подключения необходимых либ,
 * т.е. библиотеки rpc-library и ту, где декларированы удалённые сервисы).
 * 
 * <code>
 *     ...
 *     RemoteControlService service = RpcClient.getInstance().lookup(RemoteControlService.class);
 *     int totalMemory = service.getMemoryUsage();
 *     ...
 * </code>
 * 
 * Pelevin Maksim
 *
 * @since 2018/02/05
 */
public class RpcClient {

    /**
     * Возвращает прокси-объект сервиса для удалённого вызова.
     * 
     * Если вызов осуществляется не к удалённому сервису, а к собственному,
     * то сокетное взаимодействие не будет востребовано, а прокси-объект
     * вызовет необходимый метод через стандартую джавовую рефлекскию.
     * 
     * @param clazz Класс сервиса (стандартный интерфейс)
     * @param <T> Тип класса сервиса
     * @return Прокси-объект
     * 
     * @see RpcProxy#invoke(Object, Method, Object[]) 
     */
    public <T> T lookup(Class<T> clazz) {
        RpcServerConfigurationProperties.Configuration result = RpcServerConfigurationProperties.getDefault().lookup(clazz);
        if (result == null) {
            throw new RpcRuntimeException("Cannot found configuration for class " + clazz.getCanonicalName());
        }
        return new RpcProxy<>(clazz, result.getHost(), result.getPort()).newProxy();
    }

    /**
     * В отличии от {@link #lookup(Class)} можно указать произвольный порт для нужной конфигурации
     * 
     * @param clazz Класс сервиса (стандартный интерфейс)
     * @param port Порт, на котором прослушивается метод
     * @param <T> Тип класса сервиса
     * @return Прокси-объект
     */
    public <T> T lookup(Class<T> clazz, String host, int port) {
        return new RpcProxy<>(clazz, host, port).newProxy();
    }
    
    public static RpcClient getInstance() {
        return RpcClientHolder.INSTANCE;
    }
    
    private static class RpcClientHolder {
        private static final RpcClient INSTANCE = new RpcClient();
    }

    private RpcClient() {
    }
}
