package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.OneTimePassword;
import com.getsimplex.steptimer.utils.JedisData;

import java.util.Optional;

public class OneTimePasswordService {

    public static void saveOneTimePassword(OneTimePassword oneTimePassword) throws Exception{
        JedisData.loadToJedis(oneTimePassword, String.valueOf(oneTimePassword.getOneTimePassword()), oneTimePassword.getExpirationDate().getTime());
    }

    public Optional<OneTimePassword> getOneTimePassword(Integer oneTimePassword, Long expiresBefore) throws Exception{
        return JedisData.getEntityById(OneTimePassword.class, String.valueOf(oneTimePassword));
    }

}
