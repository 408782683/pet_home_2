package com.rain.controller;

import com.alibaba.fastjson2.JSONObject;
import com.rain.entity.Appointment;
import com.rain.entity.InsuranceClaim;
import com.rain.service.LifeServiceService;
import com.rain.service.OrderServiceEx;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 生活服务入口 Servlet：预约、保险、理赔、订单列表
 */
@WebServlet(urlPatterns = {"/api/appointment/*", "/api/insurance/*", "/api/order/*"})
public class LifeServiceServlet extends BaseServlet {

    private final LifeServiceService service = new LifeServiceService();
    private final OrderServiceEx orderService = new OrderServiceEx();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String path = req.getPathInfo();

        try {
            // 预约模块
            if (uri.startsWith("/api/appointment") && "/findByPage".equals(path)) {
                appointmentByPage(req, resp);
            } else if (uri.startsWith("/api/appointment") && "/status".equals(path)) {
                appointmentStatus(req, resp);
            } else if (uri.startsWith("/api/appointment") && path.startsWith("/cancel/")) {
                appointmentCancel(resp, path);
            } else if (uri.startsWith("/api/appointment") && "/add".equals(path)) {
                appointmentAdd(req, resp);

            // 保险模块
            } else if (uri.startsWith("/api/insurance") && "/findByPage".equals(path)) {
                insuranceByPage(req, resp);
            } else if (uri.startsWith("/api/insurance") && "/detail".equals(path)) {
                insuranceDetail(req, resp);
            } else if (uri.startsWith("/api/insurance") && "/orderListPage".equals(path)) {
                insuranceOrderList(req, resp);
            } else if (uri.startsWith("/api/insurance") && "/claimListPage".equals(path)) {
                claimList(req, resp);
            } else if (uri.startsWith("/api/insurance") && "/claim/submit".equals(path)) {
                claimSubmit(req, resp);

            // 商品订单模块
            } else if (uri.startsWith("/api/order") && "/listByPage".equals(path)) {
                orderList(req, resp);
            } else {
                writeJson(resp, error("接口不存在"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, error("系统繁忙，请稍后重试"));
        }
    }

    /** 分页查询预约 */
    private void appointmentByPage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());

        JSONObject data = new JSONObject();
        data.put(
                "appointmentList",
                service.findAppointmentByPage(
                        params.getIntValue("userId"),
                        params.getString("hospitalName"),
                        params.getString("status"),
                        params.getIntValue("page"),
                        params.getIntValue("size")
                )
        );
        data.put(
                "total",
                service.countAppointment(
                        params.getIntValue("userId"),
                        params.getString("hospitalName"),
                        params.getString("status")
                )
        );

        writeJson(resp, success(data));
    }

    /** 更新预约状态 */
    private void appointmentStatus(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        service.updateAppointmentStatus(params.getIntValue("id"), params.getString("status"));
        writeJson(resp, success("更新成功"));
    }

    /** 取消预约 */
    private void appointmentCancel(HttpServletResponse resp, String path) throws Exception {
        Integer id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        service.cancelAppointment(id);
        writeJson(resp, success("取消成功"));
    }

    /** 新增预约 */
    private void appointmentAdd(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        Appointment appointment = params.toJavaObject(Appointment.class);

        if (appointment.getStatus() == null || appointment.getStatus().isEmpty()) {
            appointment.setStatus("待赴约");
        }

        service.addAppointment(appointment);
        writeJson(resp, success("预约成功"));
    }

    /** 分页查询保险 */
    private void insuranceByPage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());

        Double minPrice = params.containsKey("minPrice") ? params.getDouble("minPrice") : null;
        Double maxPrice = params.containsKey("maxPrice") ? params.getDouble("maxPrice") : null;

        JSONObject data = new JSONObject();
        data.put(
                "insuranceList",
                service.findInsuranceByPage(
                        params.getString("name"),
                        minPrice,
                        maxPrice,
                        params.getIntValue("page"),
                        params.getIntValue("size")
                )
        );
        data.put(
                "total",
                service.countInsurance(
                        params.getString("name"),
                        minPrice,
                        maxPrice
                )
        );

        writeJson(resp, success(data));
    }

    /** 保险详情 */
    private void insuranceDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject data = new JSONObject();
        data.put("insurance", service.findInsuranceById(Integer.parseInt(req.getParameter("id"))));
        writeJson(resp, success(data));
    }

    /** 分页查询保单 */
    private void insuranceOrderList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());

        JSONObject data = new JSONObject();
        data.put(
                "policyList",
                service.findInsuranceOrderByPage(
                        params.getIntValue("userId"),
                        params.getString("status"),
                        params.getIntValue("page"),
                        params.getIntValue("size")
                )
        );
        data.put(
                "total",
                service.countInsuranceOrder(
                        params.getIntValue("userId"),
                        params.getString("status")
                )
        );

        writeJson(resp, success(data));
    }

    /** 分页查询理赔 */
    private void claimList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());

        JSONObject data = new JSONObject();
        data.put(
                "claimList",
                service.findClaimByPage(
                        params.getIntValue("userId"),
                        params.getString("status"),
                        params.getIntValue("page"),
                        params.getIntValue("size")
                )
        );
        data.put(
                "total",
                service.countClaim(
                        params.getIntValue("userId"),
                        params.getString("status")
                )
        );

        writeJson(resp, success(data));
    }

    /** 提交理赔 */
    private void claimSubmit(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        InsuranceClaim claim = params.toJavaObject(InsuranceClaim.class);

        // 兼容前端可能传 insuranceOrderId 的情况，自动回填到 orderId
        if ((claim.getOrderId() == null || claim.getOrderId() == 0) && params.containsKey("insuranceOrderId")) {
            claim.setOrderId(params.getIntValue("insuranceOrderId"));
        }

        String claimNo = "CLM"
                + new SimpleDateFormat("yyyyMMdd").format(new Date())
                + (new Random().nextInt(900000) + 100000);

        claim.setClaimNo(claimNo);

        if (claim.getStatus() == null || claim.getStatus().isEmpty()) {
            claim.setStatus("待审核");
        }
        if (claim.getClaimAmount() == null) {
            claim.setClaimAmount(new BigDecimal("0"));
        }

        service.submitClaim(claim);
        writeJson(resp, success("提交成功"));
    }

    /** 分页查询商城订单 */
    private void orderList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());

        writeJson(
                resp,
                success(
                        orderService.findOrderListResult(
                                params.getIntValue("userId"),
                                params.getIntValue("page"),
                                params.getIntValue("size"),
                                params.getString("status")
                        )
                )
        );
    }
}
