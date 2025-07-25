package com.pard.weact.User.service;

import com.pard.weact.User.dto.req.CreateUser;
import com.pard.weact.User.dto.res.ReadAllUser;
import com.pard.weact.User.entity.User;
import com.pard.weact.User.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;

    public void createUser(CreateUser req){

        // 아이디가 중복이라면 회원가입 못하게 막아둠.
        if(userRepo.existsByUserId(req.getUserId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 아이디입니다.");
        }

        User user = User.builder()
                .userName(req.getUserName())
                .gender(req.getGender())
                .userId(req.getUserId())
                .pw(req.getPw())
                .build();
        userRepo.save(user);
    }

    public List<ReadAllUser> readAll(){
        List<User> users = userRepo.findAll();
        List<ReadAllUser> readAllUsers = users.stream().map(user ->
                ReadAllUser.builder()
                        .id(user.getId())
                        .userName(user.getUserName())
                        .gender(user.getGender())
                        .userId(user.getUserId())
                        .build()).toList();
        return readAllUsers;
    }
    @Transactional
    public void updateById(Long Id,CreateUser req){
        Optional<User> optionalUser = userRepo.findById(Id);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.update(req);
            userRepo.save(user);// 이게 없으면 db에 저장 안됨!!!
        }
        else{
            throw new IllegalArgumentException("해당 아이디를 찾을 수 없습니다. ID: " + Id);
        }
    }

    @Transactional
    public void deleteById(Long Id){
        Optional<User> optionalUser = userRepo.findById(Id);
        if(optionalUser.isPresent()) {
            userRepo.deleteById(Id);
        }
        else{
            throw new IllegalArgumentException("해당 아이디를 찾을 수 없습니다. ID: " + Id);
        }
    }
}
