package com.aebiz.baseframework.redis;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;

/**
 * Created by wizzer on 2017/3/28.
 */
public class JedisClusterNodeSet extends HashSet<HostAndPort> {
    private static final Log log= Logs.get();
    private static final long serialVersionUID = 6028293027128774439L;

    private String nodes;
    private int port = 6937;
    private String host = "127.0.0.1";

    public void init() {
        if (!Strings.isBlank(nodes)) {
            String[] tmp = Strings.splitIgnoreBlank(nodes);
            log.debug("redisCluster nodes size::"+tmp.length);
            for (String node : tmp) {
                HostAndPort hp;
                if (node.contains(":")) {
                    String[] tmp2 = node.split("[\\:]");
                    hp = new HostAndPort(tmp2[0], Integer.parseInt(tmp2[1]));
                } else {
                    hp = new HostAndPort(node, port);
                }
                this.add(hp);
            }
        } else {
            this.add(new HostAndPort(host, port));
        }
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
