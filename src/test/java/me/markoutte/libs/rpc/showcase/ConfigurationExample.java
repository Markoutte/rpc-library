package me.markoutte.libs.rpc.showcase;

import me.markoutte.libs.rpc.server.RpcServerConfiguration;

@RpcServerConfiguration(configuration = "show-case", port = 8180, services = RandomNumber.class)
public class ConfigurationExample {
}
