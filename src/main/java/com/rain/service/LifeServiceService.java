package com.rain.service;

import com.rain.entity.Appointment;
import com.rain.entity.Insurance;
import com.rain.entity.InsuranceClaim;
import com.rain.entity.InsuranceOrder;
import com.rain.entity.InsuranceOrder;
import com.rain.mapper.LifeServiceMapper;

import java.sql.SQLException;
import java.util.List;

/**
 * 生活服务业务层
 */
public class LifeServiceService {

    private final LifeServiceMapper mapper = new LifeServiceMapper();

    // ============ 预约 ============
    public List<Appointment> findAppointmentByPage(
            Integer userId,
            String hospitalName,
            String status,
            Integer page,
            Integer size
    ) throws SQLException {
        return mapper.findAppointmentByPage(userId, hospitalName, status, page, size);
    }

    public Integer countAppointment(Integer userId, String hospitalName, String status) throws SQLException {
        return mapper.countAppointment(userId, hospitalName, status);
    }

    public void updateAppointmentStatus(Integer id, String status) throws SQLException {
        mapper.updateAppointmentStatus(id, status);
    }

    public void cancelAppointment(Integer id) throws SQLException {
        mapper.cancelAppointment(id);
    }

    public void addAppointment(Appointment appointment) throws SQLException {
        mapper.addAppointment(appointment);
    }

    // ============ 保险 ============
    public List<Insurance> findInsuranceByPage(
            String insuranceName,
            Double minPrice,
            Double maxPrice,
            Integer page,
            Integer size
    ) throws SQLException {
        return mapper.findInsuranceByPage(insuranceName, minPrice, maxPrice, page, size);
    }

    public Integer countInsurance(String insuranceName, Double minPrice, Double maxPrice) throws SQLException {
        return mapper.countInsurance(insuranceName, minPrice, maxPrice);
    }

    public Insurance findInsuranceById(Integer id) throws SQLException {
        return mapper.findInsuranceById(id);
    }

    // ============ 保单 ============
    public List<InsuranceOrder> findInsuranceOrderByPage(
            Integer userId,
            String status,
            Integer page,
            Integer size
    ) throws SQLException {
        return mapper.findInsuranceOrderByPage(userId, status, page, size);
    }

    public Integer countInsuranceOrder(Integer userId, String status) throws SQLException {
        return mapper.countInsuranceOrder(userId, status);
    }

    // ============ 理赔 ============
    public List<InsuranceClaim> findClaimByPage(
            Integer userId,
            String status,
            Integer page,
            Integer size
    ) throws SQLException {
        return mapper.findClaimByPage(userId, status, page, size);
    }

    public Integer countClaim(Integer userId, String status) throws SQLException {
        return mapper.countClaim(userId, status);
    }

    public void submitClaim(InsuranceClaim claim) throws SQLException {
        mapper.submitClaim(claim);
    }

    public void submitInsuranceOrder(InsuranceOrder order) throws SQLException {
        mapper.submitInsuranceOrder(order);
    }
}
