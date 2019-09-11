$(document).ready(function () {
    $("#rate-button").click(handleRateClick);
    $(".rate-star").mouseenter(handleStarMouseenter);
    $(".rate-star").click(handleStarClick);
    $("#rate-stars").mouseout(setStarsToCurrent);
    $("#rate-save").click(handleRateSave);

    function handleStarMouseenter(event) {
        setStars(fetchSelectedRating(event));
    }

    function handleStarClick(event) {
        setCurrentRating(fetchSelectedRating(event));
        setStarsToCurrent();
    }

    function handleRateClick(event) {
        fetchUserRating();
    }

    function handleRateSave() {
        var currentRating = getCurrentRating();
        if (currentRating < 1 || currentRating > 5) {
            setMessage("Please choose a rating!", false);
            return;
        }
        postRating(currentRating);
    }

    function disableSave(text = "...") {
        $("#rate-save").text(text);
        $("#rate-save").prop("disabled", true);
    }

    function enableSave() {
        $("#rate-save").text("Save");
        $("#rate-save").prop("disabled", false);
    }

    function showRatingScore() {
        $("#rating-score").removeClass("d-none");
    }

    function showStars() {
        $("#rate-stars").removeClass("d-none").addClass("d-flex");
    }

    function hideStars() {
        $("#rate-stars").removeClass("d-flex").addClass("d-none");
    }

    function hideModal() {
        $('#rateModal').modal('hide');
    }

    function getRequestURL() {
        return '/rating/activity/' + getActivityId();
    }

    function getActivityId() {
        return $("#activity-id").val();
    }

    function fetchSelectedRating(event) {
        return $(event.target).attr('data-rating');
    }

    function setStarsToCurrent() {
        setStars(getCurrentRating());
    }

    function getCurrentRating() {
        return $("#user-rating").val();
    }

    function setCurrentRating(rating) {
        return $("#user-rating").val(rating);
    }

    function setStars(rating) {
        for (var i = 1; i <= 5; i++) {
            var button = $(".rate-star[data-rating='" + i + "']");
            if (i <= rating) button.addClass("checked");
            else button.removeClass("checked");
        }
    }

    function setMessage(msg, isSuccess) {
        $("#rate-return").removeClass("alert-danger").removeClass("alert-success").removeClass("d-none");
        $("#rate-return").addClass("alert-" + (isSuccess ? "success" : "danger"));
        $("#rate-return").text(msg);
    }

    function hideMessage() {
        $("#rate-return").addClass("d-none");
    }

    function setAverageRating(average) {
        $("#rating-score").text(average);
        $("#star-removed").css("height", (15 + (1 - (average / 5)) * 65) + "%");
    }

    function fetchUserRating() {
        hideStars();
        hideMessage();
        disableSave();
        setCurrentRating(0);
        $.get(
            getRequestURL()
        ).done(function (data) {
            if (data.message) setMessage(data.message, false);
            else {
                setCurrentRating(data.rating);
                setStarsToCurrent();
                showStars();
                enableSave();
            }
        }).fail(function (data) {
            disableSave("Save");
            if (data.responseJSON.message) {
                setMessage(data.responseJSON.message, false);
            } else {
                setMessage("Could not load your old rating! Please try again later!", false);
            }
        });
    }

    function postRating(currentRating) {
        disableSave();
        $.ajax({
            type: "POST",
            url: getRequestURL(),
            data: {
                rating: currentRating
            },
            async: true,
            beforeSend: function (xhr) {
                xhr.setRequestHeader($("meta[name=csrf-header]").attr("content"), $("meta[name=csrf-token]").attr("content"));
            }
        }).done(function (data) {
            enableSave();
            if (data.message) setMessage(data.message, false);
            else {
                hideModal();
                setAverageRating(data.rating);
                showRatingScore();
            }
        }).fail(function (data) {
            enableSave();
            if (data.responseJSON.message) {
                setMessage(data.responseJSON.message, false);
            } else {
                setMessage("Something went wrong! Please try again.", false);
            }
        });
    }
});
