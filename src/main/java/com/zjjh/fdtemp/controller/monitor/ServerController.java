package com.zjjh.fdtemp.controller.monitor;

import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.web.domain.Server;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 *
 * @author szx
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController extends BaseController {
    @PreAuthorize("hasAuthority('monitor:server:view')")
    @GetMapping()
    public Server server() throws Exception {
        Server server = new Server();
        server.copyTo();
        return server;
    }
}
