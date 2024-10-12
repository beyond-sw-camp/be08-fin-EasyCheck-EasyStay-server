package com.beyond.easycheck.user.application.service.admin;

import com.beyond.easycheck.user.application.service.user.UserReadUseCase.FindUserResult;
import com.beyond.easycheck.user.application.service.user.UserReadUseCase.UserFindQuery;

import java.util.List;

public interface AdminReadUseCase {

    List<FindUserResult> getAllUsers(UserFindQuery query);

    FindUserResult getUserDetails(UserFindQuery query);

}
