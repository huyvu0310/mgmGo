package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.dto.ApiResponse;
import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.FavoriteService;
import com.mgmtp.internship.experiences.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteRestController extends BaseRestController {
    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @GetMapping("/activity/{activityId}")
    public Object updateFavorite(@PathVariable("activityId") long activityId, @RequestParam("isFavorite") boolean isFavorite) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please login to perform this operation.");
        }
        if (favoriteService.updateFavorite(activityId, user.getId(), isFavorite)) {
            if (!isFavorite) {
                return ApiResponse.success("Remove favorite activity success!");
            }
            return ApiResponse.success("Add favorite activity success!");
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Something went wrong! Please try again.");
    }
}
