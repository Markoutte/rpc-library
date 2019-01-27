package me.markoutte.libs.rpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, которая отвечает за сохранения серверной конфигурации
 * с описанием всех доступных удалённо сервисов.
 * 
 * Сами сервисы должны быть обычными интерфейсами, реализация которых
 * может быть в любом месте приложения, при условии, что для их
 * декларирования используется аннотация NetBeans RCP {@link org.openide.util.lookup.ServiceProvider}.
 * 
 * Тогда при запуске приложения эти сервисы будут найдены и загружены для последующего вызова.
 * 
 * Pelevin Maksim
 *
 * @since 2018/06/19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RpcServerConfiguration {

    /**
     * Название конфигурации, напр. default.
     * Должна быть уникальная для конкретной серверной конфигурации.
     */
    String configuration();

    /**
     * Хост приложения.
     */
    String host() default "localhost";

    /**
     * Порт приложения.
     */
    int port();

    /**
     * Список сервисов, доступных удалённо. Должны быть исключительно интерфейсами,
     * реализация которых доступна через {@link org.openide.util.Lookup#lookup(Class)}.
     */
    Class<?>[] services() default {};
}
