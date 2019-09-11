$(document).ready(function () {
    $('#see-more').click(function () {
        seeMore();
    })
});

function seeMore() {
    $('.btn-loading').removeClass('d-none');
    $('#see-more').addClass('d-none');
    let listWrapper = $('.lazy-loading');
    let currentPage = +listWrapper.attr('current-page') + 1;
    $.ajax({
        type: 'GET',
        url: rebuildLink(listWrapper.attr('link'), currentPage),
        success: function (res) {
            $('.btn-loading').addClass('d-none');
            listWrapper.append(res);
            listWrapper.attr('current-page', currentPage);
            if (parseInt(listWrapper.attr('current-page')) < parseInt(listWrapper.attr('size-of-pages'))) {
                $('#see-more').removeClass('d-none');
            }
        }
    })
}
function rebuildLink(link, currentPage) {
    let index = link.indexOf("?");
    if (index < 0)
        return link + currentPage;
    return link.substring(0, index) + currentPage + link.substring(index, link.length);
}