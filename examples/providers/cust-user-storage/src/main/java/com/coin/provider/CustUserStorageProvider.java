package com.coin.provider;

import com.coin.User;
import com.coin.UserAdapter;
import com.coin.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jboss.logging.Logger;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @ClassName CustUserStorageProvider
 * @Description: TODO
 * @Author kh
 * @Date 2020-09-03 17:56
 * @Version V1.0
 **/
public class CustUserStorageProvider implements UserStorageProvider, UserLookupProvider, UserQueryProvider, CredentialInputValidator {

    private static final Logger logger = Logger.getLogger(CustUserStorageProvider.class);

    private KeycloakSession session;
    private ComponentModel model;
    private SqlSessionFactory sqlSessionFactory;

    public CustUserStorageProvider(KeycloakSession session, ComponentModel model, SqlSessionFactory sqlSessionFactory) {
        this.session = session;
        this.model = model;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        logger.info("getUserById");
        StorageId storageId = new StorageId(id);
        String username = storageId.getExternalId();
        logger.info("id{0}" + id);
//        if(null != id) {
//            logger.info(id);
//            SqlSession sqlSession = sqlSessionFactory.openSession();
//            List<User> users = sqlSession.selectList("selectUserById");
//            logger.info(users);
//            sqlSession.close();
//            if(null != users && users.size() > 0) {
//                UserAdapter userAdapter = new UserAdapter(session, realm, model, users.get(0));
//                userAdapter.setUsername(users.get(0).getUsername());
//                return userAdapter;
//            }
//        }

        return getUserByUsername(username, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        if(null != username) {
            SqlSession sqlSession = null;
            try {
                logger.info(username);
                sqlSession = sqlSessionFactory.openSession();
                // List<User> users = sqlSession.selectList("selectUserByUsername", username);
                UserMapper mapper = sqlSession.getMapper(UserMapper.class);
                List<User> users = mapper.selectUserByUsername(username);

                logger.info(users);
                if(null != users && users.size() > 0) {
                    UserAdapter userAdapter = new UserAdapter(session, realm, model, users.get(0));
                    userAdapter.setUsername(users.get(0).getUsername());

                    return userAdapter;
                }
            } finally {
                if(sqlSession != null) {
                    sqlSession.close();
                }
            }

        }
        return null;
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return null;
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return 0;
    }

    @Override
    public int getUsersCount(RealmModel realm, Set<String> groupIds) {
        return 0;
    }

    @Override
    public int getUsersCount(String search, RealmModel realm) {
        return 0;
    }

    @Override
    public int getUsersCount(String search, RealmModel realm, Set<String> groupIds) {
        return 0;
    }

    @Override
    public int getUsersCount(Map<String, String> params, RealmModel realm) {
        return 0;
    }

    @Override
    public int getUsersCount(Map<String, String> params, RealmModel realm, Set<String> groupIds) {
        return 0;
    }

    @Override
    public int getUsersCount(RealmModel realm, boolean includeServiceAccount) {
        return 0;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        return null;
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {
        return null;
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return null;
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
        return null;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return null;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
        SqlSession sqlSession = null;
        List<UserModel> userAdapters = new ArrayList<>();
        try {
            sqlSession = sqlSessionFactory.openSession();
            // List<User> users = sqlSession.selectList("selectUserByUsername", username);
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = mapper.searchForUser(firstResult, maxResults);

            if(null != users && users.size() > 0) {
                for(User user : users) {
                    UserAdapter userAdapter = new UserAdapter(this.session, realm, this.model, user);
                    userAdapter.setUsername(user.getUsername());
                    userAdapters.add(userAdapter);
                }
                return userAdapters;
            }
        } finally {
            if(sqlSession != null) {
                sqlSession.close();
            }
        }
        return userAdapters;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return null;
    }

    @Override
    public List<UserModel> getRoleMembers(RealmModel realm, RoleModel role) {
        return null;
    }

    @Override
    public List<UserModel> getRoleMembers(RealmModel realm, RoleModel role, int firstResult, int maxResults) {
        return null;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
        return null;
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
        return null;
    }


    @Override
    public boolean supportsCredentialType(String credentialType) {
        return CredentialModel.PASSWORD.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        if(!supportsCredentialType(credentialInput.getType()) || !(credentialInput instanceof UserCredentialModel)) {
            return false;
        }
        String password = credentialInput.getChallengeResponse();
        String username = user.getUsername();
        UserModel queryUser = getUserByUsername(username, realm);
        if(null == queryUser) {
            return false;
        }
        UserAdapter userAdapter = (UserAdapter) queryUser;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance( "MD5" );
            digest.update(password.getBytes());
            byte[] byteArray = digest.digest();

            StringBuffer md5StrBuff = new StringBuffer();

            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                else
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }

            if(md5StrBuff.toString().equals(userAdapter.getUser().getPassword())) {
                return true;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        return false;
    }
}
