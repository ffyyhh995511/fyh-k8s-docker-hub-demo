package com.fyh.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Instant;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ip", resolveLocalIp());
        body.put("timestamp", Instant.now().toEpochMilli());
        body.put("message", "hello world");
        return body;
    }

    private String resolveLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            return "unknown";
        }
    }
}
