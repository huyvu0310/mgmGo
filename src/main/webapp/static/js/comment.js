$(document).ready(function () {
    $("#comment__content").on('input', function () {
        commentValidate();
    });

    $("#send__comment__btn").click(function () {
        if (commentValidate()) {
            postComment();
        }
    });
});

function commentValidate() {
    let contentComment = $("#comment__content").val().replace(/\s+/g, ' ').trim();

    let alertCommentMes = document.getElementById("alertComment");
    if (contentComment.length == 0 || contentComment.toString().trim().length == 0 || contentComment.toString().trim().length > 10000) {
        alertCommentMes.innerHTML = "Comment must not be empty, not whitespace-only, not longer than 10000 characters";
        return false;
    } else {
        alertCommentMes.innerHTML = "";
        return true;
    }
}

function postComment() {
    let listWrapper = $('.lazy-loading');
    listWrapper.attr('current-page', 1);
    $.ajax({
        type: "POST",
        url: "/comment/activity/" + $("#activity-id").val(),
        data: $("#commentForm").serialize(),
        async: true,
        beforeSend: function (xhr) {
            xhr.setRequestHeader($("meta[name=csrf-header]").attr("content"), $("meta[name=csrf-token]").attr("content"));
        }
    }).done(function (data) {
        resetCommentInput();
        rerenderNewCommentList(data);
        if (parseInt(listWrapper.attr('current-page')) < parseInt(listWrapper.attr('size-of-pages'))) {
            $('#see-more').removeClass('d-none');
        }

        $('#see-more').click(function () {
            seeMore();
        })

    }).fail(function (data) {
        let alertCommentMes = document.getElementById("alertComment");
        alertCommentMes.innerHTML = "Somethings are wrong";
    });
}

function resetCommentInput() {
    $('#comment__content').val("");
}

function rerenderNewCommentList(data) {
    $("#all-comment").html(data);
}