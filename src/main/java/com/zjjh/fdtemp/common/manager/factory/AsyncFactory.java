package com.zjjh.fdtemp.common.manager.factory;

import com.zjjh.fdtemp.beans.entity.SysLogininfor;
import com.zjjh.fdtemp.beans.entity.SysOperLog;
import com.zjjh.fdtemp.common.utils.*;
import com.zjjh.fdtemp.common.utils.http.UserAgentUtils;
import com.zjjh.fdtemp.common.utils.spring.SpringUtils;
import com.zjjh.fdtemp.constants.Constants;
import com.zjjh.fdtemp.service.SysOperLogService;
import com.zjjh.fdtemp.service.impl.SysLogininforServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class AsyncFactory {
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    public static TimerTask recordOper(final SysOperLog operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
                SpringUtils.getBean(SysOperLogService.class).insertOperlog(operLog);
            }
        };
    }

    public static TimerTask recordLogininfor(final String username, final String status, final String message, final Object... args) {
        final String userAgent = ServletUtils.getRequest().getHeader("User-Agent");
        final String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        return new TimerTask() {
            @Override
            public void run() {
                String address = AddressUtils.getRealAddressByIP(ip);
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(address);
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                sys_user_logger.info(s.toString(), args);
                String os = UserAgentUtils.getOperatingSystem(userAgent);
                String browser = UserAgentUtils.getBrowser(userAgent);
                SysLogininfor logininfor = new SysLogininfor();
                logininfor.setLoginName(username);
                logininfor.setIpaddr(ip);
                logininfor.setLoginLocation(address);
                logininfor.setBrowser(browser);
                logininfor.setOs(os);
                logininfor.setMsg(message);
                if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
                    logininfor.setStatus(Constants.SUCCESS);
                } else if (Constants.LOGIN_FAIL.equals(status)) {
                    logininfor.setStatus(Constants.FAIL);
                }
                SpringUtils.getBean(SysLogininforServiceImpl.class).insertLogininfor(logininfor);
            }
        };
    }
}
