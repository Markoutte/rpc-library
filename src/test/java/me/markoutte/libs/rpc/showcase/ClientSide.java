package me.markoutte.libs.rpc.showcase;

import me.markoutte.libs.rpc.client.RpcClient;

public class ClientSide {

    public static void main(String[] args) {
        RandomNumber lookup = RpcClient.getInstance().lookup(RandomNumber.class);
        int random = lookup.random();
        System.out.println(random);
    }

}
