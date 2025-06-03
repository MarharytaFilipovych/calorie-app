package com.margosha.kse.calories.business.service;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInRequestDto;
import com.margosha.kse.calories.business.mapper.RecordMapper;
import com.margosha.kse.calories.business.mapper.UserMapper;
import com.margosha.kse.calories.data.entity.Product;
import com.margosha.kse.calories.data.entity.ProductRecord;
import com.margosha.kse.calories.data.entity.User;
import com.margosha.kse.calories.data.entity.Record;
import com.margosha.kse.calories.data.enums.Gender;
import com.margosha.kse.calories.data.enums.MealType;
import com.margosha.kse.calories.data.repository.ProductRepository;
import com.margosha.kse.calories.data.repository.RecordRepository;
import com.margosha.kse.calories.data.repository.UserRepository;
import com.margosha.kse.calories.business.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RecordRepository recordRepository;
    private final ProductRepository productRepository;
    private final RecordMapper recordMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper, RecordRepository recordRepository, ProductRepository productRepository, RecordMapper recordMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.recordRepository = recordRepository;
        this.productRepository = productRepository;
        this.recordMapper = recordMapper;
    }

    public Page<UserDto> getAllUsers(int limit, int offset){
        return userRepository.findAll(PageRequest.of(offset - 1, limit)).map(userMapper::toDto);
    }

    public UUID createUser(UserDto dto){
        if(userRepository.findByEmail(dto.getEmail()))throw new IllegalArgumentException("User with email " + dto.getEmail() + " already exits!");
        User user = userRepository.save(userMapper.toEntity(dto));
        return user.getId();
    }

    public UserDto getUserById(UUID id){
        return userRepository.findById(id).map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public void updateUser(UserDto dto, UUID id){
        if (!userRepository.existsById(id)) throw new EntityNotFoundException(id.toString());
        User updatedUser = userMapper.toEntity(dto);
        updatedUser.setId(id);
        userRepository.save(updatedUser);
    }

    public void deleteUser(UUID id){
        userRepository.deleteById(id);
    }

    public int getDailyTarget(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));
        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        boolean male = user.getGender().equals(Gender.MALE);
        double dailyCalories = getDailyCalories(user, age, male);
        if(male && dailyCalories < 1500) return 1500;
        else if(dailyCalories < 1200) return 1200;
        return (int)dailyCalories;
    }

    public Page<RecordResponseDto> getRecords(UUID id, int limit, int offset, LocalDate date){
        if(!userRepository.existsById(id))throw new EntityNotFoundException(id.toString());
        Pageable pageable = PageRequest.of(offset - 1, limit);
        Page<UUID> uuidPage;
        if(date != null){
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            uuidPage = recordRepository.findIdsByUserIdAndDateTime(id, start, end, pageable);
        }
        else uuidPage = recordRepository.findIdsByUserId(id, pageable);
        if(!uuidPage.hasContent())return Page.empty();
        List<Record> records = recordRepository.findAllByIdsWithProducts(uuidPage.getContent());
        List<RecordResponseDto> recordResponseDtos = records.stream().map(recordMapper::toDto).toList();
        return new PageImpl<>(recordResponseDtos, pageable, uuidPage.getTotalPages());
    }

    @Transactional
    public void updateRecord(UUID userId, UUID id, RecordRequestDto dto){
        if(!recordRepository.existsByIdAndUser_Id(id, userId))throw new EntityNotFoundException(id.toString());
        Record record = recordRepository.findByIdWithProducts(id);
        record.setMealType(MealType.valueOf(dto.getMealType().name()));

        Set<ProductRecord> productRecords = new HashSet<>();
        dto.getProductRecords().forEach(pr -> {
            ProductRecord productRecord = createProductRecord(record, pr);
            productRecords.add(productRecord);
        });

        record.getProductRecords().clear();
        record.setProductRecords(productRecords);
        recordRepository.save(record);
    }

    public void deleteRecord(UUID userId, UUID id){
        recordRepository.deleteByIdAndUser_Id(id, userId);
    }

    public RecordResponseDto getConsumption(UUID userId, UUID id){
        if(!recordRepository.existsByIdAndUser_Id(id, userId))throw new EntityNotFoundException(id.toString());
        Record record = recordRepository.findByIdWithProducts(id);
        return  recordMapper.toDto(record);
    }

    @Transactional
    public UUID createRecord(UUID userId, RecordRequestDto dto){
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(userId.toString()));
        Record record = recordMapper.toEntity(dto);
        record.setUser(user);
        Set<ProductRecord> productRecords = new HashSet<>();
        dto.getProductRecords().forEach(pr -> {
            ProductRecord productRecord = createProductRecord(record, pr);
            productRecords.add(productRecord);
        });
        record.setProductRecords(productRecords);
        Record savedRecord = recordRepository.save(record);
        return savedRecord.getId();
    }

    private ProductRecord createProductRecord(Record record, ProductRecordInRequestDto pr){
        Product product = productRepository.findById(pr.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("The passed product with id " + pr.getProductId().toString() + " was not found!"));
        ProductRecord productRecord = new ProductRecord();
        productRecord.setProduct(product);
        productRecord.setQuantity(pr.getQuantity());
        productRecord.setRecord(record);
        return productRecord;
    }

    private double getDailyCalories(User user, int age, boolean male) {
        double dailyCalories =(10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * age);
        dailyCalories = male ? dailyCalories + 5 : dailyCalories - 161;
        switch(user.getActivityLevel()){
            case SEDENTARY -> dailyCalories *= 1.2;
            case LOW -> dailyCalories *= 1.375;
            case MODERATE -> dailyCalories *= 1.55;
            case HIGH -> dailyCalories *= 1.725;
            case VERY_HIGH -> dailyCalories *= 1.9;
        }
        switch (user.getGoal()){
            case LOSE -> dailyCalories -= 500;
            case GAIN -> dailyCalories += 500;
        }
        return dailyCalories;
    }
}
