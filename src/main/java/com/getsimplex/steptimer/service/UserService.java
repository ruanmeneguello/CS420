package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.User;
import com.getsimplex.steptimer.utils.JedisData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by sean on 6/13/2017.
 */
public class UserService {

    public static User getUserByUserName(String userName) throws Exception{

        Optional<User> userOptional = JedisData.getEntityById(User.class, userName);
        if (!userOptional.isPresent()){
            throw new Exception ("User name not found");
        }
        User currentUser = userOptional.get();
        return currentUser;

    }

    public static User getUserByPhone(Long phoneNumber) throws Exception{

        List<User> users = JedisData.getEntitiesByScore(User.class, phoneNumber, phoneNumber);
        if (users.isEmpty()){
            throw new Exception ("Phone number "+phoneNumber+" not found");
        }
        if (users.size()>1){
            throw new Exception ("Multiple users found with same phone number: "+phoneNumber);
        }
        return users.get(0);

    }


}
