package com.coin.factory;

import com.coin.provider.CustUserStorageProvider;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * @ClassName CustUserStorageProviderFactory
 * @Description: TODO
 * @Author kh
 * @Date 2020-09-03 17:59
 * @Version V1.0
 **/
public class CustUserStorageProviderFactory implements UserStorageProviderFactory<CustUserStorageProvider> {

    private static final Logger logger = Logger.getLogger(CustUserStorageProvider.class);
    private SqlSessionFactory sessionFactory = null;
    private Reader reader;
    private String CONFIGURATION_FILE = "mybatis-config.xml";

    @Override
    public void init(Config.Scope config) {
        try {
            reader = Resources.getResourceAsReader(CONFIGURATION_FILE);
            sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (Exception ex) {
            throw new RuntimeException("Error configuring MyBatis: " + ex.getMessage(), ex);
        }
    }

    @Override
    public CustUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        CustUserStorageProvider custUserStorageProvider = new CustUserStorageProvider(session, model, sessionFactory);
        return custUserStorageProvider;
    }

    @Override
    public String getId() {
        return "cust-user-storage";
    }


    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create().property()
                .name("江津用户登录验证").label("江津").helpText("登录验证").type(ProviderConfigProperty.STRING_TYPE).defaultValue("").add()
                .build();
    }
}
