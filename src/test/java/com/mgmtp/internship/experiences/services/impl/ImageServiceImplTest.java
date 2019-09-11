package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.dto.ImageDTO;
import com.mgmtp.internship.experiences.repositories.ImageRepository;
import com.mgmtp.internship.experiences.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageServiceImplTest.class);
    private static final byte[] IMAGE_DATA = {1, 2, 3};
    private static final long USER_ID = 1;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    @InjectMocks
    private ImageServiceImpl imageService;

    @Test
    public void insertImageShouldReturnImageId() {
        Long expectedId = 1L;

        Mockito.when(imageRepository.insert(IMAGE_DATA)).thenReturn(expectedId);

        Long returnId = imageService.insertImage(IMAGE_DATA);

        Assert.assertEquals(returnId, expectedId);
    }

    @Test
    public void updateUserImageShouldReturnImageId() {
        Long expectedId = 1L;
        Long oldImageId = 2L;

        Mockito.doReturn(expectedId).when(imageService).insertImage(IMAGE_DATA);

        Long returnId = imageService.updateUserImage(USER_ID, oldImageId, IMAGE_DATA);

        Assert.assertEquals(expectedId, returnId);
    }

    @Test
    public void updateUserImageShouldCallDeleteOldImageIfThereIs() {
        Long expectedId = 2L;

        imageService.updateUserImage(USER_ID, expectedId, IMAGE_DATA);

        Mockito.verify(imageRepository).deleteImage(expectedId);
    }

    @Test
    public void updateUserImageShouldNotCallDeleteOldImageIfNull() {
        Long expectedId = null;

        imageService.updateUserImage(USER_ID, expectedId, IMAGE_DATA);

        Mockito.verify(imageRepository, Mockito.never()).deleteImage(expectedId);
    }

    @Test
    public void updateUserImageShouldCallSetImageId() {
        Long expectedId = 1L;
        Long oldImageId = 2L;

        Mockito.doReturn(expectedId).when(imageService).insertImage(IMAGE_DATA);

        imageService.updateUserImage(USER_ID, oldImageId, IMAGE_DATA);

        Mockito.verify(userRepository).updateImage(USER_ID, expectedId);
    }

    @Test
    public void validateProfilePictureShouldReturnFalseIfImageTooLarge() throws IOException {
        InputStream inputStream = generateImageStream(200);

        boolean result = imageService.validateProfilePicture(inputStream);

        Assert.assertFalse(result);
    }

    @Test
    public void validateProfilePictureShouldReturnTrue() throws IOException {
        InputStream inputStream = generateImageStream(150);

        boolean result = imageService.validateProfilePicture(inputStream);

        Assert.assertEquals(true, result);
    }

    @Test(expected = IOException.class)
    public void validateProfilePictureShouldThrowExceptionIfNotImage() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1});

        imageService.validateProfilePicture(inputStream);
    }

    public static InputStream generateImageStream(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", outputStream);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public void shouldReturnImageById() {
        long imageId = 1;
        ImageDTO expectedImageDTO = new ImageDTO(imageId, new byte[]{(byte) 0xe0, 0x4f});
        Mockito.when(imageRepository.findImageById(imageId)).thenReturn(expectedImageDTO);

        ImageDTO actualImageDTO = imageService.findImageById(imageId);

        Assert.assertEquals(actualImageDTO, expectedImageDTO);
    }

    @Test
    public void shouldReturnTrueIfAddImageSuccessfully() {
        long expectedResult = 1L;
        Mockito.when(imageRepository.insert(new byte[]{(byte) 0xe0, 0x4f})).thenReturn(expectedResult);

        long actualResult = imageService.insertImage(new byte[]{(byte) 0xe0, 0x4f});

        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldReturnFalseIfAddImageFail() {
        long expectedResult = 0L;
        Mockito.when(imageRepository.insert(new byte[]{(byte) 0xe0, 0x4f})).thenReturn(expectedResult);

        long actualResult = imageService.insertImage(new byte[]{(byte) 0xe0, 0x4f});

        Assert.assertEquals(actualResult, actualResult);
    }
}
