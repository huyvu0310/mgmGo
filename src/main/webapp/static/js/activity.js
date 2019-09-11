$("#title").keypress(function (e) {
    return e.keyCode != 13;
});

$("#editForm").on("submit", function () {
    let nameInput = document.getElementById("title");
    let desInput = document.getElementById("description");
    let nameInputValue = nameInput.value.replace(/\s+/g, ' ').trim();
    if (nameInputValue.length == 0 || nameInputValue.toString().length == 0 || nameInputValue.toString().length > 100) {
        return false;
    } else {
        $("#title").val(nameInputValue);
    }
    if (desInput.value.length == 0 || desInput.value.toString().trim().length == 0 || desInput.value.toString().trim().length > 100000) {
        return false;
    }
    //trim for address
    let addressInputValue = document.getElementById("address");
    $("#address").val(addressInputValue.value.replace(/\s+/g, ' ').trim());
    return true;
});

$("#createForm").on("submit", function () {
    let nameInput = document.getElementById("title");
    let desInput = document.getElementById("description");
    let alertNameMes = document.getElementById("alertName");
    let alertDesMes = document.getElementById("alertDes");
    let nameInputValue = nameInput.value.replace(/\s+/g, ' ').trim();
    if (nameInputValue.length == 0 || nameInputValue.toString().length == 0 || nameInputValue.toString().length > 100) {
        alertNameMes.innerHTML = "Name must not be empty, not whitespace-only, not longer than 100 characters";
        return false;
    } else {
        $("#title").val(nameInputValue);
    }
    if (desInput.value.length == 0 || desInput.value.toString().trim().length == 0 || desInput.value.toString().trim().length > 100000) {
        alertDesMes.innerHTML = "Description must not be empty, not whitespace only, not longer than 100.000 characters";
        return false;

    }
    return true;
});

$(document).ready(function () {
    let title = $("#title");
    title.on('input', function () {
        let nameEdit = title.val().replace(/\s+/g, ' ').trim(),
            alertNameMes = document.getElementById("alertName");
        if (nameEdit.length == 0 || nameEdit.toString().trim().length == 0 || nameEdit.toString().trim().length > 100) {
            alertNameMes.innerHTML = "Name must not be empty, not whitespace-only, not longer than 100 characters";
        } else {
            alertNameMes.innerHTML = "";
        }
    })
})

$(document).ready(function () {
    let title = $("#description");
    title.on('input', function () {
        let desEdit = title.val(),
            alertDesMes = document.getElementById("alertDes");
        if (desEdit.length == 0 || desEdit.toString().trim().length == 0 || desEdit.toString().trim().length > 100000) {
            alertDesMes.innerHTML = "Description must not be empty, not whitespace only, not longer than 100.000 characters";
        } else {
            alertDesMes.innerHTML = "";
        }
    })
})

$(document).ready(function () {
    new TagBox(".tag-box", 3, false);
});
function setMessageWhenFailed(msg) {
    $("#spinner").removeClass("spinner-border").removeClass("text-success");
    $("#gps-return").removeClass("alert-danger").removeClass("alert-success").removeClass("d-none");
    $("#gps-return").addClass("alert-danger");
    $("#gps-return").text(msg != null ? msg : "Something went wrong! Please try again.");
    $("#addressInputLabel").text("");
}

function hideMessage() {
    $("#gps-return").addClass("d-none");
}

$("#findLocation").click(function () {
    hideMessage();
    $("#confirmLocationBtn").attr("disabled", "disabled");
    $("#addressInput").val("");
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function (position) {
            $("#spinner").addClass("spinner-border").addClass("text-success");
            $("#addressInputLabel").text("Getting your location, please wait...");
            let urlRequest = '/location/activity/' + position.coords.latitude.toString() + '/' + position.coords.longitude + '/';
            $.get(
                urlRequest
            ).done(function (data) {
                if (data.addressGeocoding) {
                    hideMessage();
                    $("#addressInput").val(data.addressGeocoding);
                    $("#spinner").removeClass("spinner-border").removeClass("text-success");
                    $("#addressInputLabel").text("");
                    $("#confirmLocationBtn").removeAttr('disabled');
                } else {
                    setMessageWhenFailed("Something went wrong! Please try again.");
                }
            }).fail(function (data) {
                setMessageWhenFailed(data.message);
            });
        }, showErrorWhileGetLocation);
    } else {
        setMessageWhenFailed("Geolocation is not supported by this browser.");
    }
})

function showErrorWhileGetLocation(error) {
    $("#spinner").removeClass("spinner-border").removeClass("text-success");
    switch (error.code) {
        case error.PERMISSION_DENIED:
            setMessageWhenFailed("User denied the request for Geolocation, check your internet connection and try again!");
            break;
        case error.POSITION_UNAVAILABLE:
            setMessageWhenFailed("Location information is unavailable, check your browser and try again!");
            break;
        case error.TIMEOUT:
            setMessageWhenFailed("The request to get user location timed out, check your internet connection and try again!");
            break;
        case error.UNKNOWN_ERROR:
            setMessageWhenFailed("Something went wrong! Please try again.");
            break;
    }
}

$("#confirmLocationBtn").click(function () {
    $("#address").val($("#addressInput").val().replace(/\s+/g, ' ').trim());
    $("#modalGetLocation").modal('hide');
})

$("#address").focus(function () {
    $("#findLocation").css('border-top-color', '#80bdff').css('border-right-color', '#80bdff').css('border-bottom-color', '#80bdff');
})

$("#address").focusout(function () {
    $("#findLocation").css('border-top-color', '#ced4da').css('border-right-color', '#ced4da').css('border-bottom-color', '#ced4da');
})
