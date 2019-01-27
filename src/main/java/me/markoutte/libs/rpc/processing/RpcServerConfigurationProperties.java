package me.markoutte.libs.rpc.processing;

import me.markoutte.libs.rpc.exceptions.RpcRuntimeException;
import me.markoutte.libs.rpc.server.ServiceRegister;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

import java.util.*;

/**
 * Конфигурация RPC сервера, которая хранит локальную информацию о всех сервисах и их расположениях.
 * 
 * Если у приложения определена локальная серверная конфигурация,
 * то будут задействованы поля host и port, которые по умолчанию
 * хранят localhost и -1 соответственно.
 * 
 * Pelevin Maksim
 *
 * @since 2018/06/19
 */
public class RpcServerConfigurationProperties {

    /* package */ static final String FOLDER = "Rpc/Registration";
    /**
     * Свойство обязательно должно быть заполнено для серверного приёма вызова процедур.
     * 
     * По нему определяется, какая конфигурация из всех собранных в различных модулях,
     * принадлежит конкретному приложения. Если приложение не поднимает собственного
     * серверного соединения, то может быть {@code null}.
     * 
     * Для заполнения использовать опции приложения {@code -J-Drpc.configuration=default,alevrit}.
     * 
     */
    private static final String configuration = System.getProperty("rpc.configuration");
    
    private final Collection<Configuration> enabledConfigurations = new ArrayList<>();
    private final Map<Class<?>, Configuration> serviceMap;

    public static RpcServerConfigurationProperties getDefault() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * @return все серверные конфигурации
     */
    public Collection<Configuration> getConfigurations() {
        return enabledConfigurations;
    }
    // иницилазиация всех конфигураций
    private RpcServerConfigurationProperties() {
        FileObject config = FileUtil.getConfigFile(FOLDER);
        
        Map<Class<?>, Configuration> serivceMap = new HashMap<>();
        String host = "localhost";
        int port = -1;

        Set<String> enabledConfigurations = new HashSet<>();
        if (configuration != null) {
            enabledConfigurations.addAll(Arrays.asList(configuration.split(",")));
        }
        
        if (config != null && config.getChildren().length > 0) {

            ClassLoader cl = Lookup.getDefault().lookup(ClassLoader.class);

            for (FileObject object : config.getChildren()) {
                String configuration = (String) object.getAttribute("configuration");
                Configuration conf = new Configuration(
                        configuration,
                        getHost(object, configuration),
                        getPort(object, configuration)
                );

                for (FileObject fo : object.getChildren()) {
                    String name = fo.getName();
                    try {
                        Class<?> service = cl.loadClass(name);
                        serivceMap.put(service, conf);
                        ServiceRegister.getInstance().add(service, host, port);
                    } catch (ClassNotFoundException | RpcRuntimeException e) {
                        e.printStackTrace();
                    }
                }
                
                if (enabledConfigurations.contains(conf.getConfiguration())) {
                    this.enabledConfigurations.add(conf);
                }
            }
        }

        this.serviceMap = Collections.unmodifiableMap(serivceMap);
    }

    private String getHost(FileObject fo, String configuration) {
        String anotherValue = String.format("rpc.configuration.%s.host", configuration);
        return System.getProperty(anotherValue, (String) fo.getAttribute("host"));
    }
    
    private int getPort(FileObject fo, String configuration) {
        String anotherValue = String.format("rpc.configuration.%s.port", configuration);
        return Integer.getInteger(anotherValue, (int) fo.getAttribute("port"));
    }

    /**
     * Осуществяет поиск серверной конфигурации для конкретного сервиса.
     * @param clazz Класс удалённого или локального сервиса
     * @return Серверная конфигурация
     * 
     * @see Configuration
     */
    public Configuration lookup(Class<?> clazz) {
        return serviceMap.get(clazz);
    }
    
    private static class InstanceHolder {
        public static final RpcServerConfigurationProperties INSTANCE;
        static {
            RpcServerConfigurationProperties instance = null;
            try {
                instance = new RpcServerConfigurationProperties();
            } catch (Throwable e ) {                
                e.printStackTrace();
            }
            INSTANCE = instance;
        }
    }

    /**
     * Серверная конфигурация
     */
    public static class Configuration {
        private final String configuration;
        private final String host;
        private final int port;

        public Configuration(String configuration, String host, int port) {
            this.configuration = configuration;
            this.host = host;
            this.port = port;
        }

        public String getConfiguration() {
            return configuration;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }
}
