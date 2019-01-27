package me.markoutte.libs.rpc.showcase;

import me.markoutte.libs.rpc.processing.RpcServerConfigurationProperties;
import me.markoutte.libs.rpc.server.ServerListener;
import me.markoutte.libs.rpc.server.ServiceRegister;

import java.util.Arrays;

public class ServerSide {

    public static void main(String[] args) throws InterruptedException {
        // эти действия не нужны при запуске в платформе, только конфигурация
        RpcServerConfigurationProperties.Configuration localhost = new RpcServerConfigurationProperties.Configuration("show-case", "localhost", 8180);
        ServerListener listener = new ServerListener();
        listener.run(Arrays.asList(localhost));
        ServiceRegister.getInstance().add(RandomNumber.class, new GuarantyRandomNumber(), "localhost", 8180);
        Thread.sleep(60_000);
    }

}
