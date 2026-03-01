package com.rain.service;

import com.rain.entity.*;
import com.rain.mapper.LifeServiceMapper;

import java.sql.SQLException;
import java.util.List;

public class LifeServiceService {
    private final LifeServiceMapper mapper = new LifeServiceMapper();
    public List<Appointment> findAppointmentByPage(Integer userId, String hospitalName, String status, Integer page, Integer size) throws SQLException { return mapper.findAppointmentByPage(userId, hospitalName, status, page, size); }
    public Integer countAppointment(Integer userId, String hospitalName, String status) throws SQLException { return mapper.countAppointment(userId, hospitalName, status); }
    public void updateAppointmentStatus(Integer id, String status) throws SQLException { mapper.updateAppointmentStatus(id, status); }
    public void cancelAppointment(Integer id) throws SQLException { mapper.cancelAppointment(id); }
    public void addAppointment(Appointment a) throws SQLException { mapper.addAppointment(a); }

    public List<Insurance> findInsuranceByPage(String insuranceName, String type, Integer page, Integer size) throws SQLException { return mapper.findInsuranceByPage(insuranceName, type, page, size); }
    public Integer countInsurance(String insuranceName, String type) throws SQLException { return mapper.countInsurance(insuranceName, type); }
    public Insurance findInsuranceById(Integer id) throws SQLException { return mapper.findInsuranceById(id); }

    public List<InsuranceOrder> findInsuranceOrderByPage(Integer userId, Integer page, Integer size) throws SQLException { return mapper.findInsuranceOrderByPage(userId, page, size); }
    public Integer countInsuranceOrder(Integer userId) throws SQLException { return mapper.countInsuranceOrder(userId); }

    public List<InsuranceClaim> findClaimByPage(Integer userId, Integer page, Integer size) throws SQLException { return mapper.findClaimByPage(userId, page, size); }
    public Integer countClaim(Integer userId) throws SQLException { return mapper.countClaim(userId); }
    public void submitClaim(InsuranceClaim c) throws SQLException { mapper.submitClaim(c); }
}
