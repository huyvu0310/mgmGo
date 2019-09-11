// Jquery objects
const $uploadImage = $('#upload-image');
const $image = $('#image');
const $imageError = $('#image-error');
const $imageOnDetailPage = $('#image-on-detail-page');
const $btnUpload = $('#btn-upload');
const $btnUpdate = $('#btn-update');
const $thumbnails = $('.thumbnails');

// Attributes
const srcAttr = 'src';
const imageIdAttr = 'image-id';
const orderImageAttr = 'order-image';

// default values
const attrDefault = {
    [srcAttr]: '/static/images/no-img.png',
    [imageIdAttr]: -1,
    [orderImageAttr]: -1
}

// className
const displayNoneClass = 'd-none';
const imageActiveClass = 'image-active';

$(document).ready(initPage);

function initPage() {
    $uploadImage.change(function () {
        readImage(this);
    });

    $thumbnails.on('click', '#btn-upload', btnUploadClickHandler);
    $thumbnails.on('click', '.icon-update', iconUpdateClickHandler);
    $btnUpdate.on('click', btnUpdateClickHandler);
    $thumbnails.on('click', '.thumbnail-image', thumbnailImageClickHandler);

    $('.dropzone-wrapper').on('dragover', function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).addClass('dragover');
    });

    $('.dropzone-wrapper').on('dragleave', function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).removeClass('dragover');
    });


    $("form#upload-image-form").submit(function (event) {
            event.preventDefault();
            let url = getRequestImageURL(),
                method = getMethod(),
                formData = new FormData();
            formData.append("image_file", getImageFile());
            if (getImageId() != -1) {
                formData.append("image_id", getImageId());
                formData.append('order_image', getOrderImage());
            }

            $.ajax({
                type: method,
                url: url,
                data: formData,
                contentType: false,
                processData: false,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader($("meta[name=csrf-header]").attr("content"), $("meta[name=csrf-token]").attr("content"));
                }
            }).done(function (result) {
                $("#exampleModalCenter").modal("hide");

                let imageUrl = getImageUrl(result.imageId);
                if (method === 'PUT') {
                    updateThumbnails(result, imageUrl);
                } else {
                    addNewThumbnail(result, imageUrl);
                }
            }).fail(function (error) {
                setMessage("Something went wrong! Please try again.", false);
            })
            return false;
        }
    );
}

function btnUploadClickHandler() {
    updateAttribute($image);
    reset();
}

function iconUpdateClickHandler() {
    updateAttribute($image, $(this).prev());
    reset();
}

function btnUpdateClickHandler() {
    updateAttribute($image, $imageOnDetailPage);
    reset();
}

function thumbnailImageClickHandler() {
    updateAttribute($imageOnDetailPage, $(this));
    $('.image-active').removeClass(imageActiveClass);
    $(this).addClass(imageActiveClass);
}

function reset() {
    $imageError.addClass(displayNoneClass);
    $uploadImage.val(null);
    $image.removeClass('animated-background');
    $('#image-name').html('');
    disableSaveImage();
}

function updateAttribute(targetElement, sourceElement = null, newAttr = null) {
    const myAttr = {...attrDefault};
    if (sourceElement) {
        myAttr[srcAttr] = sourceElement.attr(srcAttr);
        myAttr[imageIdAttr] = sourceElement.attr(imageIdAttr);
        myAttr[orderImageAttr] = sourceElement.attr(orderImageAttr);
    } else if (newAttr) {
        myAttr[srcAttr] = newAttr.imageUrl
        myAttr[imageIdAttr] = newAttr.imageId;
        myAttr[orderImageAttr] = newAttr.orderImage;
    }

    targetElement.attr({
        ...myAttr
    })
}

function isValidImageType(type) {
    return /image\/.*/g.test(type);
}

function readImage(input) {
    if (input.files && input.files[0]) {
        let type = input.files[0].type;
        if (isValidImageType(type)) {
            if (input.files[0].size > 10 * Math.pow(2, 20)) {
                $imageError.removeClass(displayNoneClass);
                $imageError.text("Your file size is over 10MB");
                $uploadImage.val(null);
            } else {
                let reader = new FileReader();
                reader.onload = function (e) {
                    let wrapperZone = $(input).parent();
                    $image.addClass('animated-background');
                    $image.attr('src', e.target.result);
                    $('#image-name').html(input.files[0].name);
                    enableSaveImage();
                    wrapperZone.removeClass('dragover');

                };
                reader.readAsDataURL(input.files[0]);
                $imageError.addClass(displayNoneClass);
            }
        } else {
            $imageError.removeClass(displayNoneClass);
            $imageError.text("Your file is not an image file.");
            $('.dropzone-wrapper').removeClass('dragover');
        }
    }
}

function disableSaveImage() {
    $("#btn-submit").addClass('not-allow');
    $("#btn-submit").prop("disabled", true);
}

function enableSaveImage() {
    $("#btn-submit").removeClass('not-allow');
    $("#btn-submit").prop("disabled", false);
}

function getImageFile() {
    return $uploadImage.get(0).files[0];
}

function setMessage(msg, isSuccess) {
    $imageError.removeClass(displayNoneClass);
    $imageError.text(msg);
}

function getRequestImageURL() {
    return '/api/image/activity/' + getActivityId();
}

function getImageUrl(imageId) {
    return "/api/image/" + imageId;
}

function getImageId() {
    return $image.attr(imageIdAttr);
}

function getOrderImage() {
    return $image.attr(orderImageAttr);
}

function getActivityId() {
    return $("#activity-id").val();
}

function getMethod() {
    return +getImageId() + 1 ? 'PUT' : 'POST';
}

function updateThumbnails({imageId}, imageUrl) {
    let orderImage = getOrderImage();
    let newAttr = {imageUrl, imageId, orderImage};

    if ($('.image-active').attr(orderImageAttr) === orderImage) {
        updateAttribute($('.image-active'), null, newAttr);
        updateAttribute($imageOnDetailPage, null, newAttr);
    } else {
        let $updateThumbnail = $('.thumbnail-image').filter((index, item) => $(item).attr(orderImageAttr) === orderImage);
        updateAttribute($updateThumbnail, null, newAttr);
    }
}

function addNewThumbnail({imageId}, imageUrl) {
    let orderImage = $thumbnails.children().length;
    let newThumbnail = renderThumbnail(orderImage, imageId, imageUrl);

    $btnUpload.before(newThumbnail);
    if (orderImage === 1) {
        updateAttribute($imageOnDetailPage, newThumbnail.children());
        newThumbnail.children().addClass(imageActiveClass);
        $btnUpdate.removeClass(displayNoneClass);
    } else if (orderImage === +$('#max-number-upload-images').val()) {
        $btnUpload.remove();
    }
}

function renderThumbnail(orderImage, imageId, imageUrl) {
    return $($.parseHTML(`
                            <div class="thumbnail">
                                <img class="thumbnail-image"
                                     order-image=${orderImage}
                                     image-id=${imageId}
                                     src=${imageUrl}\/>
                                <label class="icon-update"
                                       title="Update image."
                                       data-toggle="modal" data-target="#exampleModalCenter">
                                    <i class="fa fa-camera p-md-2"><\/i>
                                <\/label>
                            </div>`));
}