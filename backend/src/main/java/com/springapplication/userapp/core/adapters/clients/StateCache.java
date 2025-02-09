package com.springapplication.userapp.core.adapters.clients;

import com.springapplication.userapp.providers.encryption.Encryptor;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.util.HashMap;

@Service
public class StateCache {
    private final HashMap<String, String> stateCache;
    private final Encryptor encryptor;

    public StateCache(Encryptor encryptor) {
        this.stateCache = new HashMap<>();
        this.encryptor = encryptor;
    }

    public String addElement(String state) throws IllegalBlockSizeException, BadPaddingException {
        try{
            String encrypt = encryptor.encrypt(state);
            stateCache.put(state, encrypt);
            return encrypt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkElement(String user){
        return stateCache.containsKey(user);
    }

    public String checkEncryptedElement(String encryptedState) {
        for(String key : stateCache.keySet()) {
            if(stateCache.get(key).equals(encryptedState)) return key;
        }
        return null;
    }

    public boolean removeElement(String user) {
        if(!stateCache.containsKey(user)) return true;
        return stateCache.remove(user, stateCache.get(user));
    }
}
