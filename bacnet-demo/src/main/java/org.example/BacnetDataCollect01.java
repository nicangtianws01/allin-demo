package org.example;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class BacnetDataCollect01 {

    /**
     *
     */
    public List<ResultObj> collect() {

        LocalDevice localDevice = null;

        List<ResultObj> resultObjs = new ArrayList<>();

        try {
            //创建网络对象
            IpNetwork ipNetwork = new IpNetworkBuilder()
                    //本机的ip
                    .withLocalBindAddress("10.130.1.71")
                    //掩码和长度，如果不知道本机的掩码和长度的话，可以使用代码的工具类IpNetworkUtils获取
                    .withSubnet("255.255.255.0", 24)
                    //默认的UDP端口
                    .withPort(47808)
                    .withReuseAddress(true)
                    .build();

            //创建虚拟的本地设备，deviceNumber随意
            localDevice = new LocalDevice(233, new DefaultTransport(ipNetwork));
            //初始化本地设备
            localDevice.initialize();

            //搜寻网段内远程设备
            localDevice.startRemoteDeviceDiscovery();

            RemoteDevice remoteDevice = localDevice.getRemoteDeviceBlocking(884807);

            //获取远程设备的标识符对象
            List<ObjectIdentifier> identifierList = RequestUtils.getObjectList(localDevice, remoteDevice).getValues();
//
//            List<ObjectIdentifier> identifierList = new ArrayList<>();
//
//            //Object所有标识符
//            for (ObjectIdentifier oi : objectList) {
//                identifierList.add(oi);
//            }

            PropertyReferences refs = new PropertyReferences();

            for (ObjectIdentifier identifier : identifierList) {
                refs.add(identifier, PropertyIdentifier.objectName);
                refs.add(identifier, PropertyIdentifier.presentValue);
            }

            PropertyValues values = RequestUtils.readProperties(localDevice, remoteDevice, refs, false, null);

            List<String> properties = Arrays.asList("Temperature.Water", "Temperature.Indoor");

            for (ObjectIdentifier identifier : identifierList) {
                String name = values.getString(identifier, PropertyIdentifier.objectName);

                // 过滤掉不需要的属性
                if (!properties.contains(name) || identifier.getInstanceNumber() == 0) {
                    continue;
                }

                String value = values.getString(identifier, PropertyIdentifier.presentValue);

                ResultObj resultObj = new ResultObj().setName(name).setValue(value);
                resultObjs.add(resultObj);
            }

            return resultObjs;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (localDevice != null) {
                localDevice.terminate();
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class ResultObj {
        private String name;
        private String value;
    }
}
