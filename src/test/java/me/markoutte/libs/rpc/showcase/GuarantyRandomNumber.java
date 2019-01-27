package me.markoutte.libs.rpc.showcase;

import org.openide.util.lookup.ServiceProvider;

// нужно, чтобы автоматически подхватывалось,
// но здесь отключено, т.к. тестируется локально
//@ServiceProvider(service = RandomNumber.class)
public class GuarantyRandomNumber implements RandomNumber {
    @Override
    public int random() {
        return 4;
    }
}
