package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.dto.ImageDTO;
import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.ImageService;
import com.mgmtp.internship.experiences.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ImageRestControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageRestControllerTest.class);

    private static final long USER_ID = 1L;
    private static final MockMultipartFile FILE = new MockMultipartFile("photo", "test.jpg", "multipart/form-data", new byte[]{1});
    private static final Long OLD_IMAGE_ID = 2L;
    private static final String USER_URL = "/api/image/user";
    private static final UserProfileDTO USER_PROFILE_DTO = Mockito.spy(new UserProfileDTO(OLD_IMAGE_ID, "display", 0));
    private static final LdapUserDetails LDAP_USER_DETAILS = mock(LdapUserDetails.class);
    private static final CustomLdapUserDetails USER_DETAILS = Mockito.spy(new CustomLdapUserDetails(USER_ID, USER_PROFILE_DTO, LDAP_USER_DETAILS));

    private MockMvc mockMvc;

    @Mock
    private ImageService imageService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ImageRestController imageRestController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(imageRestController).build();
    }

    @Test
    public void addUserImageShouldThrowExceptionIfSizeTooLarge() {
        try {
            Mockito.when(imageService.validateProfilePicture(Mockito.any())).thenReturn(false);

            mockMvc.perform(MockMvcRequestBuilders.multipart(USER_URL).file(FILE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("{\"status\":\"FAILED\",\"message\":\"The image is too large.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void addUserImageShouldThrowExceptionIfNotAnImage() {
        try {
            Mockito.when(imageService.validateProfilePicture(Mockito.any())).thenThrow(new IOException());

            mockMvc.perform(MockMvcRequestBuilders.multipart(USER_URL).file(FILE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("{\"status\":\"FAILED\",\"message\":\"Can not process the image.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void addUserImageShouldNotChangeIdIfUpdateFail() {
        try {
            Mockito.when(imageService.validateProfilePicture(Mockito.any())).thenReturn(true);
            Mockito.when(userService.getCurrentUser()).thenReturn(USER_DETAILS);
            Mockito.when(imageService.updateUserImage(USER_DETAILS.getId(), OLD_IMAGE_ID, FILE.getBytes())).thenThrow(new RuntimeException());

            mockMvc.perform(MockMvcRequestBuilders.multipart(USER_URL).file(FILE))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("{\"status\":\"FAILED\",\"message\":\"Server error.\"}"));

            Mockito.verify(USER_PROFILE_DTO, Mockito.never()).setImageId(Mockito.anyLong());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void addUserImageShouldChangeIdIfUpdateSucceed() {

        Long imageId = 1L;
        try {

            Mockito.when(imageService.validateProfilePicture(Mockito.any())).thenReturn(true);
            Mockito.when(userService.getCurrentUser()).thenReturn(USER_DETAILS);
            Mockito.when(imageService.updateUserImage(USER_DETAILS.getId(), OLD_IMAGE_ID, FILE.getBytes())).thenReturn(imageId);

            mockMvc.perform(MockMvcRequestBuilders.multipart(USER_URL).file(FILE))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"id\":1}"));

            Mockito.verify(USER_PROFILE_DTO).setImageId(imageId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnImageById() {
        long imageId = 1;
        ImageDTO expectedImageDTO = new ImageDTO(imageId, new byte[]{(byte) 0xd4, 0x4f});
        Mockito.when(imageService.findImageById(imageId)).thenReturn(expectedImageDTO);

        byte[] actualImage = imageRestController.getImageById(imageId).getBody();

        Assert.assertEquals(actualImage, expectedImageDTO.getImage());
    }

    @Test(expected = ApiException.class)
    public void shouldThrowNotFoundExceptionIfReturnNullImage() {
        long imageId = 1;
        Mockito.when(imageService.findImageById(imageId)).thenReturn(null);
        imageRestController.getImageById(imageId).getBody();
    }

    @Test(expected = ApiException.class)
    public void shouldThrowApiExceptionIfAddImageFailed() throws IOException {
        long activityId = 1;
        MockMultipartFile photo = new MockMultipartFile("data", "data.jpg", "image/jpg", new byte[]{0x4f, 0x3f});
        Mockito.when(imageService.checkMaximumImagesOfActivity(activityId)).thenReturn(-1L);
        imageRestController.addImage(activityId, photo);
    }
}
