$(document).ready(function () {
    $('#btn-toggle').on('click', function (event) {
        if ($('#btn-toggle').attr('aria-expanded') === 'true') {
            $('#icon-toggle').removeClass('fa-times').addClass('fa-bars');
        } else {
            $('#icon-toggle').removeClass('fa-bars').addClass('fa-times');
        }
    })

    if ($("#message-success-crud")) {
        setTimeout(function () {
            $("#message-success-crud").remove();
        }, 4000)
    }

    $('img').one('load', function() {
        $(this).removeClass('animated-background');
    }).each(function() {
        if (this.complete) {
            $(this).trigger('load');
        }
    })
})
