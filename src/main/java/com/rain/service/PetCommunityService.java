package com.rain.service;

import com.rain.entity.*;
import com.rain.mapper.PetCommunityMapper;

import java.sql.SQLException;
import java.util.List;

public class PetCommunityService {
    private final PetCommunityMapper mapper = new PetCommunityMapper();

    public List<AdoptionPost> findAdoptionByPage(Integer currentUserId, String breed, String petType, String petGender, String publisher, Integer page, Integer size) throws SQLException { return mapper.findAdoptionByPage(currentUserId, breed, petType, petGender, publisher, page, size); }
    public Integer countAdoption(Integer currentUserId, String breed, String petType, String petGender, String publisher) throws SQLException { return mapper.countAdoption(currentUserId, breed, petType, petGender, publisher); }

    public List<BreedingPost> findBreedingByPage(Integer currentUserId, String breed, String petType, String petGender, String publisher, Integer page, Integer size) throws SQLException { return mapper.findBreedingByPage(currentUserId, breed, petType, petGender, publisher, page, size); }
    public Integer countBreeding(Integer currentUserId, String breed, String petType, String petGender, String publisher) throws SQLException { return mapper.countBreeding(currentUserId, breed, petType, petGender, publisher); }
    public BreedingPost findBreedingById(Integer id) throws SQLException { return mapper.findBreedingById(id); }
    public void addBreeding(BreedingPost p) throws SQLException { mapper.addBreeding(p); }
    public void updateBreeding(BreedingPost p) throws SQLException { mapper.updateBreeding(p); }

    public List<FosterPost> findFosterByPage(Integer currentUserId, String breed, String petType, String petGender, String publisher, Integer page, Integer size) throws SQLException { return mapper.findFosterByPage(currentUserId, breed, petType, petGender, publisher, page, size); }
    public Integer countFoster(Integer currentUserId, String breed, String petType, String petGender, String publisher) throws SQLException { return mapper.countFoster(currentUserId, breed, petType, petGender, publisher); }
    public FosterPost findFosterById(Integer id) throws SQLException { return mapper.findFosterById(id); }
    public void addFoster(FosterPost p) throws SQLException { mapper.addFoster(p); }
    public void updateFoster(FosterPost p) throws SQLException { mapper.updateFoster(p); }

    public List<Hospital> findHospitalByPage(String name, String type, String level, Integer page, Integer size) throws SQLException { return mapper.findHospitalByPage(name, type, level, page, size); }
    public Integer countHospital(String name, String type, String level) throws SQLException { return mapper.countHospital(name, type, level); }
    public Hospital findHospitalById(Integer id) throws SQLException { return mapper.findHospitalById(id); }
}
