package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkUtils;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BacnetDataCollectTest {

    @Test
    public void test() throws JsonProcessingException {
        List<String> props = new ArrayList<>();
        props.add("Temperature.Indoor");
        props.add("Temperature.Water");
        props.add("Temperature.Outdoor");
        List<BacnetDataCollect.ResultObj> list =
                new BacnetDataCollect()
                        .collect("10.130.1.71", "255.255.255.0", 24, 884807, props);
        log.info("{}", new ObjectMapper().writeValueAsString(list));
    }

    @Test
    public void test1() {
        log.info("{}", ObjectType.analogInput.intValue());
        log.info("{}", ObjectType.binaryInput.intValue());
        log.info("{}", ObjectType.multiStateInput.intValue());
        log.info("{}", ObjectType.characterstringValue.intValue());
    }

    @Test
    public void test2() throws JsonProcessingException {
        List<String> props = new ArrayList<>();
        props.add("Temperature.Indoor");
        props.add("Temperature.Water");
        BacnetDataCollect.BacnetDevice bacnetDevice = new BacnetDataCollect.BacnetDevice()
                .setIp("10.130.1.71").setDeviceId(884807)
                .setMask("255.255.255.0").setMaskLen(24)
                .setProperties(props);

        List<BacnetDataCollect.ResultObj> list = new BacnetDataCollect().collect(bacnetDevice);
        log.info("{}", new ObjectMapper().writeValueAsString(list));
    }

    @Test
    public void test3() {
        InetAddress address = IpNetworkUtils.getInetAddress(new OctetString("F4-B5-20-60-36-45".replace("-", ":").getBytes(StandardCharsets.UTF_8)));
        log.info(address.getHostAddress());
    }
}
