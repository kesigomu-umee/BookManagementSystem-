package example.training.bookmanagement.presentation.authentication;

import example.training.bookmanagement.domain.model.useraggregate.Role;
import example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway.jpamodel.UserJpaModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;

public class SimpleLoginUser extends org.springframework.security.core.userdetails.User {
    //ROLE_プレフィックスを付けるというSpringSecurityの仕様
    private static final List<GrantedAuthority> USER_ROLES = AuthorityUtils.createAuthorityList("ROLE_" + Role.GeneralUser.name());
    private static final List<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList("ROLE_" + Role.Administrator.name(), "ROLE_" + Role.GeneralUser.name());
    public String userId;
//    //publicなuser変数が無いと動かない
//    public String user;

//    public SimpleLoginUser(UserJpaModel userJpaModel) {
//        super(userJpaModel.getName(), userJpaModel.getPassword(), determineRoles(userJpaModel.getRole()));
//        userId = userJpaModel.getId();
//    }

    public SimpleLoginUser(String id, String name, String password, String role) {
        super(name, password, determineRoles(role));
        userId = id;
    }

    private static List<GrantedAuthority> determineRoles(String role) {
        return role.equals(Role.Administrator.name()) ? ADMIN_ROLES : USER_ROLES;
    }

    public static SimpleLoginUser createByUserJpaModel(UserJpaModel userJpaModel) {
        return new SimpleLoginUser(userJpaModel.getId(), userJpaModel.getName(), userJpaModel.getPassword(), userJpaModel.getRole());
    }

    public String getUserId() {
        return userId;
    }
}
