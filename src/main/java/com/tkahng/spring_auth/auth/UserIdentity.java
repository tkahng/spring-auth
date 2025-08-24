package com.tkahng.spring_auth.auth;

import com.tkahng.spring_auth.identity.Identity;
import com.tkahng.spring_auth.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserIdentity {
    private User user;
    private Identity identity;
    private List<Identity> identities = new ArrayList<>();
}
