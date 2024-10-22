package com.beyond.easycheck.admin.application.service;

import com.beyond.easycheck.user.application.service.UserReadUseCase.FindUserResult;
import com.beyond.easycheck.user.application.service.UserReadUseCase.UserFindQuery;

import java.util.List;

public interface AdminReadUseCase {

    List<FindUserResult> getAllUsers(UserFindQuery query);

    FindUserResult getUserDetails(UserFindQuery query);

}
